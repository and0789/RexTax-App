package com.task.dynamicregex.controllers;

import com.task.dynamicregex.dao.SocmedRegexDao;
import com.task.dynamicregex.entities.SocmedRegex;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    private TextField newRegexTextField;
    @FXML
    private TextField regexDescriptionTextField;
    @FXML
    private Button addButton;
    @FXML
    private TextField desiredPatternTextField;
    @FXML
    private Button processButton;
    @FXML
    private TextField regexRecommendationTextField;
    @FXML
    private Button copyButton;
    @FXML
    private Button backButton;

    private SocmedRegexDao socmedRegexDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socmedRegexDao = new SocmedRegexDao();

        titleLabel.setText(Common.SOCIALMEDIA.getName() + " Regex");
        subtitleLabel.setText(subtitleLabel.getText() + Common.SOCIALMEDIA.getName());
    }

    @FXML
    private void addButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        SocmedRegex socmedRegex = new SocmedRegex(
                UUID.randomUUID().toString(),
                regexDescriptionTextField.getText().trim(),
                newRegexTextField.getText().trim(),
                Common.SOCIALMEDIA);

        if (socmedRegexDao.save(socmedRegex) == 1) {
            Helper.changePage(addButton, "socmed-regex.fxml");
        }
    }

    @FXML
    private void processButtonOnAction(ActionEvent actionEvent) {
    }

    @FXML
    private void copyButtonOnAction(ActionEvent actionEvent) {
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
    private void backButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(backButton, "socmed-regex.fxml");
    }

    private void enableAddButton() {
        addButton.disableProperty().bind(
                Bindings.isEmpty(newRegexTextField.textProperty())
                        .or(Bindings.isEmpty(regexDescriptionTextField.textProperty())));
    }
}
