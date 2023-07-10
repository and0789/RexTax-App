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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    private TableColumn<SocmedRegex, String> fieldTableColumn;
    @FXML
    private TableColumn<SocmedRegex, String> regexTableColumn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressCountLabel;

    private ObservableList<SocmedRegex> socmedRegexList;
    private List<SocmedRegex> selectedSocmedRegexList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socmedRegexList = FXCollections.observableArrayList();
        SocmedRegexDao socmedRegexDao = new SocmedRegexDao();
        selectedSocmedRegexList = FXCollections.observableArrayList();

        try {
            socmedRegexList.addAll(socmedRegexDao.findBySocialMediaId(Common.SOCIALMEDIA.getId()));
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

        titleLabel.setText(Common.SOCIALMEDIA.getName() + " Regex");
        selectTableColumn.setGraphic(selectAllCheckBox);
        socmedRegexTableView.setItems(socmedRegexList);
        selectTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMark()));
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
    private void addRegexButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(addRegexButton, "socmed-regex-add.fxml");
    }

    @FXML
    private void processButtonOnAction(ActionEvent actionEvent) {
        Task<ObservableList<Result>> task = new Task<>() {
            @Override
            protected ObservableList<Result> call() throws Exception {
                Stream<String> lines = Files.lines(Common.SELECTED_FILE.toPath());
                List<String> wordList = lines.flatMap(line -> Arrays.stream(line.split("\\s+"))).toList();
                lines.close();

                int numThreads = Runtime.getRuntime().availableProcessors();
                int chunkSize = wordList.size() / numThreads;

                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
                List<Future<ObservableList<Result>>> futures = new ArrayList<>();

                for (int i = 0; i < numThreads; i++) {
                    int startIndex = i * chunkSize;
                    int endIndex = (i == numThreads - 1) ? wordList.size() : (i + 1) * chunkSize;

                    List<String> subList = wordList.subList(startIndex, endIndex);

                    Callable<ObservableList<Result>> task2 = () -> {
                        ObservableList<Result> results = FXCollections.observableArrayList();
                        int wordCount = 0;

                        for (String sub : subList) {
                            for (SocmedRegex socmedRegex : selectedSocmedRegexList) {
                                Pattern pattern = Pattern.compile(socmedRegex.getRegex());
                                Matcher matcher = pattern.matcher(sub);
                                if (matcher.matches()) {
                                    results.add(new Result(socmedRegex.getField(), sub));
                                    wordCount++;
                                    if (wordCount % 1000 == 0) {
                                        updateMessage(wordCount + " results found");
                                    }
                                }
                            }
                        }

                        return results;
                    };

                    Future<ObservableList<Result>> future = executorService.submit(task2);
                    futures.add(future);
                }

                ObservableList<Result> combinedResults = FXCollections.observableArrayList();

                for (Future<ObservableList<Result>> future : futures) {
                    try {
                        ObservableList<Result> results = future.get();
                        combinedResults.addAll(results);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                executorService.shutdown();
                return combinedResults;
            }
        };

        task.setOnRunning(event -> {
            AnchorPane.setBottomAnchor(socmedRegexTableView, 140.0);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressCountLabel.setVisible(true);
        });

        task.setOnSucceeded(event -> {
            progressBar.setProgress(0);
            progressCountLabel.textProperty().unbind();
            Common.RESULTS = task.getValue();
            try {
                Helper.changePage(processButton, "result.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        task.setOnFailed(event -> {
            progressBar.setProgress(0);
            progressCountLabel.setText("Failed");
            progressCountLabel.textProperty().unbind();
            System.out.println(task.getException().getMessage());
        });

        progressCountLabel.textProperty().bind(task.messageProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(backButton, "social-media.fxml");
    }
}
