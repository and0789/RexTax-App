package com.andreseptian.controllers;

import com.andreseptian.dao.CaseIdentityDao;
import com.andreseptian.entities.CaseIdentity;
import com.andreseptian.utils.Common;
import com.andreseptian.utils.Helper;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

public class IdentityInputController implements Initializable {

    @FXML
    private TextField investigatorTextField;
    @FXML
    private TextField caseTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button nextButton;

    private final CaseIdentityDao caseIdentityDao = new CaseIdentityDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Common.CASE_IDENTITY != null) {
            investigatorTextField.setText(Common.CASE_IDENTITY.getInvestigatorsName());
            caseTextField.setText(Common.CASE_IDENTITY.getHandledCase());
            descriptionTextField.setText(Common.CASE_IDENTITY.getCaseDescription());
            nextButton.setDisable(false);
        }
    }

    @FXML
    private void nextButtonOnAction() throws SQLException, ClassNotFoundException {
        if (Common.CASE_IDENTITY == null) {
            CaseIdentity caseIdentity = new CaseIdentity(
                    UUID.randomUUID().toString(),
                    investigatorTextField.getText().trim(),
                    caseTextField.getText().trim(),
                    descriptionTextField.getText().trim());

            if (caseIdentityDao.save(caseIdentity) == 1) {
                Common.CASE_IDENTITY = caseIdentity;
                Helper.changePage(nextButton, "file-input.fxml");
            }
            return;
        }

        Common.CASE_IDENTITY.setInvestigatorsName(investigatorTextField.getText().trim());
        Common.CASE_IDENTITY.setHandledCase(caseTextField.getText().trim());
        Common.CASE_IDENTITY.setCaseDescription(descriptionTextField.getText().trim());

        if (caseIdentityDao.update(Common.CASE_IDENTITY) == 1) {
            Helper.changePage(nextButton, "file-input.fxml");
        }
    }

    @FXML
    private void investigatorTextFieldOnKeyReleased() {
        enableNextButton();
    }

    @FXML
    private void caseTextFieldOnKeyReleased() {
        enableNextButton();
    }

    @FXML
    private void descriptionTextFieldOnKeyReleased() {
        enableNextButton();
    }

    private void enableNextButton() {
        nextButton.disableProperty().bind(
                Bindings.isEmpty(investigatorTextField.textProperty())
                        .or(Bindings.isEmpty(caseTextField.textProperty()))
                        .or(Bindings.isEmpty(descriptionTextField.textProperty())));
    }
}
