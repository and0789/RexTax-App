package com.task.dynamicregex.controllers;

import com.task.dynamicregex.entities.Result;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText("Result");
        resultTableView.setItems(Common.RESULTS);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(resultTableView.getItems().indexOf(data.getValue()) + 1));
        fieldTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getField()));
        resultTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResult()));
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(backButton, "socmed-regex.fxml");
    }
}
