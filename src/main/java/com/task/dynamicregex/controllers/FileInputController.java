package com.task.dynamicregex.controllers;

import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class FileInputController implements Initializable {

    @FXML
    private TextField fileTextField;
    @FXML
    private Button browseButton;
    @FXML
    private TextField hashCodeTextField;
    @FXML
    private Button createHashCodeButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Common.SELECTED_FILE != null && Common.HASH_CODE != null) {
            selectedFile = Common.SELECTED_FILE;
            fileTextField.setText(selectedFile.getPath());
            hashCodeTextField.setText(Common.HASH_CODE);
            createHashCodeButton.setDisable(false);
            nextButton.setDisable(false);
            fileTextField.setStyle("-fx-opacity: 1");
            hashCodeTextField.setStyle("-fx-opacity: 1");
        }
    }

    @FXML
    private void browseButtonOnAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            fileTextField.setText(selectedFile.getPath());
            fileTextField.setStyle("-fx-opacity: 1");
            createHashCodeButton.setDisable(false);
        } else {
            fileTextField.setText("");
            fileTextField.setStyle("-fx-opacity: 0.4");
            createHashCodeButton.setDisable(true);
        }

        hashCodeTextField.setText("");
        hashCodeTextField.setStyle("-fx-opacity: 0.4");
        nextButton.setDisable(true);

    }

    @FXML
    private void createHashCodeButtonOnAction(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(Path.of(selectedFile.getPath()));
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        String checksum = new BigInteger(1, hash).toString(16);
        hashCodeTextField.setText(checksum);
        hashCodeTextField.setStyle("-fx-opacity: 1");
        nextButton.setDisable(false);
    }

    @FXML
    private void nextButtonOnAction(ActionEvent actionEvent) throws IOException {
        Common.SELECTED_FILE = selectedFile;
        Common.HASH_CODE = hashCodeTextField.getText();
        Helper.changePage(nextButton, "social-media.fxml");
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(backButton, "identity-input.fxml");
    }
}
