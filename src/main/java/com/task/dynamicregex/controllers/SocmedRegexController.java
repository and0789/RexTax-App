package com.task.dynamicregex.controllers;

import com.task.dynamicregex.dao.SocmedRegexDao;
import com.task.dynamicregex.entities.Result;
import com.task.dynamicregex.entities.SocmedRegex;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocmedRegexController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private TableView<SocmedRegex> socmedRegexTableView;
    @FXML
    private Button backButton;
    @FXML
    private Button addRegexButton;
    @FXML
    private Button processButton;
    @FXML
    private TableColumn<SocmedRegex, CheckBox> selectTableColumn;
    @FXML
    private TableColumn<SocmedRegex, String> categoryTableColumn;
    @FXML
    private TableColumn<SocmedRegex, String> fieldTableColumn;
    @FXML
    private TableColumn<SocmedRegex, String> regexTableColumn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressCountLabel;

    private ObservableList<SocmedRegex> socmedRegexList;
    private List<SocmedRegex> selectedSocmedRegexList;
    private boolean isSearchCancelled = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socmedRegexList = FXCollections.observableArrayList();
        SocmedRegexDao socmedRegexDao = new SocmedRegexDao();
        selectedSocmedRegexList = FXCollections.observableArrayList();

        try {
            socmedRegexList.addAll(socmedRegexDao.findSocmedRegex(Common.SOCIALMEDIA.id()));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CheckBox selectAllCheckBox = new CheckBox();
        selectAllCheckBox.setOnAction(actionEvent -> {
            if (selectAllCheckBox.isSelected()) {
                selectedSocmedRegexList.clear();
                selectedSocmedRegexList.addAll(socmedRegexList);
                processButton.setDisable(false);
            } else {
                selectedSocmedRegexList.clear();
            }
            processButton.setDisable(selectedSocmedRegexList.isEmpty());
            socmedRegexTableView.getItems().forEach(item -> item.getMark().setSelected(selectAllCheckBox.isSelected()));
        });

        titleLabel.setText(Common.SOCIALMEDIA.name() + " Regex");
        selectTableColumn.setGraphic(selectAllCheckBox);
        socmedRegexTableView.setItems(socmedRegexList);
        selectTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMark()));
        categoryTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArtifactCategory().name()));
        fieldTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getField()));
        regexTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRegex()));

        socmedRegexTableView.getItems().forEach(item -> item.getMark().setOnAction(actionEvent -> {
            if (item.getMark().isSelected()) {
                selectedSocmedRegexList.add(item);
            } else {
                selectAllCheckBox.setSelected(false);
                selectedSocmedRegexList.remove(item);
            }

            processButton.setDisable(selectedSocmedRegexList.isEmpty());
            boolean isSelectedAll = socmedRegexTableView.getItems().stream().map(SocmedRegex::getMark).toList().stream().allMatch(CheckBox::isSelected);
            selectAllCheckBox.setSelected(isSelectedAll);
        }));

    }

    @FXML
    private void addRegexButtonOnAction(ActionEvent actionEvent) {
        Helper.changePage(addRegexButton, "socmed-regex-add.fxml");
    }

    @FXML
    private void processButtonOnAction(ActionEvent actionEvent) {
        Task<ObservableList<Result>> task = new Task<>() {
            @Override
            protected ObservableList<Result> call() {
                ObservableList<Result> combinedResults = FXCollections.observableArrayList();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Common.SELECTED_FILE), StandardCharsets.UTF_8))) {
                    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    List<Future<ObservableList<Result>>> futures = new ArrayList<>();
                    char[] buffer = new char[8192];
                    int bytesRead;
                    AtomicInteger resultsCount = new AtomicInteger();

                    while ((bytesRead = reader.read(buffer)) != -1) {
                        if (isSearchCancelled) {
                            executorService.shutdownNow();
                            return null;
                        }
                        String line = new String(buffer, 0, bytesRead);
                        Callable<ObservableList<Result>> task2 = () -> {
                            ObservableList<Result> results = FXCollections.observableArrayList();

                            for (SocmedRegex socmedRegex : selectedSocmedRegexList) {
                                Pattern pattern = Pattern.compile(socmedRegex.getRegex());
                                Matcher matcher = pattern.matcher(line);
                                while (matcher.find()) {
                                    resultsCount.getAndIncrement();
                                    results.add(new Result(socmedRegex.getArtifactCategory().name(), socmedRegex.getField(), matcher.group()));
                                    updateMessage(resultsCount + " results found");
                                }
                            }

                            return results;
                        };

                        Future<ObservableList<Result>> future = executorService.submit(task2);
                        futures.add(future);
                    }

                    for (Future<ObservableList<Result>> future : futures) {
                        ObservableList<Result> results = future.get();
                        combinedResults.addAll(results);
                    }

                    executorService.shutdown();
                } catch (IOException | RuntimeException | OutOfMemoryError | ExecutionException |
                         InterruptedException e) {
                    e.printStackTrace();
                }

                return combinedResults;
            }
        };

        task.setOnRunning(event -> {
            AnchorPane.setBottomAnchor(socmedRegexTableView, 140.0);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressBar.setStyle("-fx-accent: #0C80D4");
            progressCountLabel.setVisible(true);
            progressCountLabel.setStyle("-fx-text-fill: black");
            backButton.setText("Cancel");
            addRegexButton.setDisable(true);
            processButton.setDisable(true);
            backButton.setOnAction(cancelEvent -> {
                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Cancel Search");
                alert.setContentText("Are you sure you want to cancel the search?");
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                alert.showAndWait();

                if (alert.getResult() == buttonTypeYes) {
                    isSearchCancelled = true;
                }
            });
        });

        task.setOnSucceeded(event -> {
            progressCountLabel.textProperty().unbind();
            if (!isSearchCancelled) {
                Common.RESULTS = task.getValue();
                Helper.changePage(processButton, "result.fxml");
            } else {
                isSearchCancelled = false;
                backButton.setText("Back");
                backButton.setOnAction(this::backButtonOnAction);
                addRegexButton.setDisable(false);
                processButton.setDisable(false);
                progressBar.setProgress(1);
                progressBar.setStyle("-fx-accent: #FF3F3F");
                progressCountLabel.setStyle("-fx-text-fill: white");
                progressCountLabel.setText("Search cancelled");
            }
        });

        task.setOnFailed(event -> {
            progressBar.setProgress(1);
            progressBar.setStyle("-fx-accent: #FF3F3F");
            progressCountLabel.setStyle("-fx-text-fill: white");
            progressCountLabel.textProperty().unbind();
            progressCountLabel.setText("Search failed");
            task.getException().printStackTrace();
        });

        progressCountLabel.textProperty().bind(task.messageProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) {
        Helper.changePage(backButton, "social-media.fxml");
    }
}
