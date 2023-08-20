package com.task.dynamicregex.controllers;

import com.task.dynamicregex.dao.SocmedRegexDao;
import com.task.dynamicregex.entities.ArtifactCategory;
import com.task.dynamicregex.entities.SocmedRegex;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

public class SocmedRegexAddController implements Initializable {

    @FXML
    private Label subtitleLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField newCategoryTextField;
    @FXML
    private Button addCategoryButton;
    @FXML
    private TextField newRegexTextField;
    @FXML
    private TextField regexDescriptionTextField;
    @FXML
    private ComboBox<ArtifactCategory> categoryComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;

    private SocmedRegexDao socmedRegexDao;
    private ObservableList<ArtifactCategory> artifactCategoryList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socmedRegexDao = new SocmedRegexDao();
        artifactCategoryList = FXCollections.observableArrayList();

        try {
            artifactCategoryList.addAll(socmedRegexDao.findArtifactCategory(Common.SOCIALMEDIA.id()));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        titleLabel.setText(Common.SOCIALMEDIA.name() + " Regex");
        subtitleLabel.setText(subtitleLabel.getText() + Common.SOCIALMEDIA.name());
        categoryComboBox.setItems(artifactCategoryList);
    }

    @FXML
    private void addCategoryButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ArtifactCategory artifactCategory = new ArtifactCategory(
                UUID.randomUUID().toString(),
                newCategoryTextField.getText().trim(),
                Common.SOCIALMEDIA);

        if (socmedRegexDao.saveCategory(artifactCategory) == 1) {
            artifactCategoryList.clear();
            artifactCategoryList.addAll(socmedRegexDao.findArtifactCategory(Common.SOCIALMEDIA.id()));
            categoryComboBox.setItems(artifactCategoryList);
            newCategoryTextField.clear();
        }
    }

    @FXML
    private void addButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        SocmedRegex socmedRegex = new SocmedRegex(
                UUID.randomUUID().toString(),
                regexDescriptionTextField.getText().trim(),
                newRegexTextField.getText().trim(),
                categoryComboBox.getValue());

        if (socmedRegexDao.save(socmedRegex) == 1) {
            Helper.changePage(addButton, "socmed-regex.fxml");
        }
    }

    @FXML
    private void newCategoryTextFieldOnKeyReleased(KeyEvent keyEvent) {
        addCategoryButton.disableProperty().bind(Bindings.isEmpty(newCategoryTextField.textProperty()));
    }

    @FXML
    private void newRegexTextFieldOnKeyReleased(KeyEvent keyEvent) {
        enableAddButton();
    }

    @FXML
    private void regexDescriptionTextFieldOnKeyReleased(KeyEvent keyEvent) {
        enableAddButton();
    }

    @FXML
    private void categoryComboBoxOnAction(ActionEvent actionEvent) {
        enableAddButton();
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) {
        Helper.changePage(backButton, "socmed-regex.fxml");
    }

    private void enableAddButton() {
        addButton.disableProperty().bind(
                Bindings.isEmpty(newRegexTextField.textProperty())
                        .or(Bindings.isEmpty(regexDescriptionTextField.textProperty()))
                        .or(Bindings.isNull(categoryComboBox.getSelectionModel().selectedItemProperty())));
    }
}
