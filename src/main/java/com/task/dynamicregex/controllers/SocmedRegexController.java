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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
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
    private TableColumn<SocmedRegex, String> fieldTableColumn;
    @FXML
    private TableColumn<SocmedRegex, String> regexTableColumn;

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
    private void processButtonOnAction(ActionEvent actionEvent) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(Common.SELECTED_FILE));
        String line;
        ObservableList<Result> results = FXCollections.observableArrayList();

        while ((line = bufferedReader.readLine()) != null) {
            for (SocmedRegex socmedRegex : selectedSocmedRegexList) {
                Pattern pattern = Pattern.compile(socmedRegex.getRegex());
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    results.add(new Result(socmedRegex.getField(), line));
                }
            }
        }

        Common.RESULTS = results;
        Helper.changePage(processButton, "result.fxml");

    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(backButton, "social-media.fxml");
    }
}
