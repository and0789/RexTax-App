package com.task.dynamicregex.controllers;

import com.task.dynamicregex.entities.Result;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ResultController implements Initializable {
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<Result> resultTableView;
    @FXML
    private TableColumn<Result, Integer> noTableColumn;
    @FXML
    private TableColumn<Result, String> categoryTableColumn;
    @FXML
    private TableColumn<Result, String> fieldTableColumn;
    @FXML
    private TableColumn<Result, String> resultTableColumn;
    @FXML
    private Button backButton;
    @FXML
    private Button exportButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressCountLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText("Result");
        resultTableView.setItems(Common.RESULTS);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(resultTableView.getItems().indexOf(data.getValue()) + 1));
        categoryTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoryName()));
        fieldTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getField()));
        resultTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResult()));
        if (resultTableView.getItems().isEmpty()) {
            exportButton.setDisable(true);
        }
    }

    @FXML
    private void exportButtonOnAction(ActionEvent actionEvent) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();

                List<Result> sortedResults = resultTableView.getItems()
                        .sorted(Comparator.comparing(Result::getCategoryName).thenComparing(Result::getField))
                        .stream().toList();

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(sortedResults);

                String analysisDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm"));
                String datetimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                String longDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

                param.put("data_source", dataSource);
                param.put("investigators_name", Common.CASE_IDENTITY.getInvestigatorsName());
                param.put("handled_case", Common.CASE_IDENTITY.getHandledCase());
                param.put("case_description", Common.CASE_IDENTITY.getCaseDescription());
                param.put("analysis_date", analysisDate);
                param.put("file_name", Common.SELECTED_FILE.getName());
                param.put("hash_code", Common.HASH_CODE);
                param.put("datetime_now", datetimeNow);
                param.put("long_date", longDate);

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/task/dynamicregex/jasper-report/result-report.jasper");
                    JasperPrint print = JasperFillManager.fillReport(inputStream, param, dataSource);
                    JasperViewer viewer = new JasperViewer(print, false);
                    viewer.setVisible(true);
                    viewer.setFitPageZoomRatio();
                } catch (JRException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        };

        task.setOnRunning(event -> {
            AnchorPane.setBottomAnchor(resultTableView, 140.0);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressBar.setStyle("-fx-accent: #0C80D4");
            progressCountLabel.setVisible(true);
            progressCountLabel.setStyle("-fx-text-fill: black");
            progressCountLabel.setText("Exporting...");
        });

        task.setOnSucceeded(event -> {
            progressBar.setProgress(1);
            progressBar.setStyle("-fx-accent: #00bd90");
            progressCountLabel.setStyle("-fx-text-fill: white");
            progressCountLabel.setText("Export successful");
        });

        task.setOnFailed(event -> {
            progressBar.setProgress(1);
            progressBar.setStyle("-fx-accent: #FF3F3F");
            progressCountLabel.setStyle("-fx-text-fill: white");
            progressCountLabel.setText("Export failed");
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) {
        Helper.changePage(backButton, "socmed-regex.fxml");
        Common.RESULTS.clear();
    }
}
