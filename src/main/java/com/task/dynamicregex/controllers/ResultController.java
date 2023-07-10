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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private TableColumn<Result, String> fieldTableColumn;
    @FXML
    private TableColumn<Result, String> resultTableColumn;
    @FXML
    private Button backButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressCountLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText("Result");
        resultTableView.setItems(Common.RESULTS);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(resultTableView.getItems().indexOf(data.getValue()) + 1));
        fieldTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getField()));
        resultTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResult()));
    }

    @FXML
    private void exportButtonOnAction(ActionEvent actionEvent) {
        if (resultTableView.getItems().isEmpty()) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BasicConfigurator.configure();
                HashMap<String, Object> param = new HashMap<>();
                List<Result> resultList = new ArrayList<>();
                int totalPages = 0;

                for (Result item : resultTableView.getItems()) {
                    Result result = new Result(item.getField(), item.getResult());
                    resultList.add(result);
                }

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(resultList);

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

//                int numThreads = Runtime.getRuntime().availableProcessors();
//                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//                List<Future<JasperPrint>> futures = new ArrayList<>();
//                JasperPrint combinedPrint = null;
//
//                for (int i = 0; i < numThreads; i++) {
//                    Callable<JasperPrint> task2 = () -> {
//                        InputStream inputStream = this.getClass().getResourceAsStream("/com/task/dynamicregex/jasper-report/result-report.jasper");
//                        return JasperFillManager.fillReport(inputStream, param, new JREmptyDataSource());
//                    };
//
//                    Future<JasperPrint> future = executorService.submit(task2);
//                    futures.add(future);
//                }
//
//                for (Future<JasperPrint> future : futures) {
//                    try {
//                        JasperPrint filledReport = future.get();
//                        if (combinedPrint == null) {
//                            combinedPrint = filledReport;
//                        } else {
//                            for (JRPrintPage page : filledReport.getPages()) {
//                                combinedPrint.addPage(page);
//                            }
//                        }
//                    } catch (ExecutionException | InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                executorService.shutdown();
//
//                if (combinedPrint != null) {
//                    JasperViewer viewer = new JasperViewer(combinedPrint, false);
//                    viewer.setVisible(true);
//                    viewer.setFitPageZoomRatio();
//                }

                try {
                    InputStream inputStream = this.getClass().getResourceAsStream("/com/task/dynamicregex/jasper-report/result-report.jasper");
                    JasperPrint print = JasperFillManager.fillReport(inputStream, param, new JREmptyDataSource());

                    for (JRPrintPage page : print.getPages()) {
                        totalPages++;
                    }

                    JasperViewer viewer = new JasperViewer(print, false);
                    viewer.setVisible(true);
                    viewer.setFitPageZoomRatio();
                } catch (JRException e) {
                    throw new RuntimeException(e);
                }

                final int finalTotalPages = totalPages;
                updateMessage(finalTotalPages + " pages exported");

                return null;
            }
        };

        task.setOnRunning(event -> {
            AnchorPane.setBottomAnchor(resultTableView, 140.0);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressCountLabel.setVisible(true);
        });

        task.setOnSucceeded(event -> {
            progressBar.setProgress(0);
            progressCountLabel.textProperty().unbind();
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
        Helper.changePage(backButton, "socmed-regex.fxml");
    }
}
