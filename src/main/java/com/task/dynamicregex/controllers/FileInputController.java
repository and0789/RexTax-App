package com.task.dynamicregex.controllers;

import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
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
    private void browseButtonOnAction() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Mem Files", "*.mem"),
                new FileChooser.ExtensionFilter("Dump Files", "*.dmp"),
                new FileChooser.ExtensionFilter("Raw Files", "*.raw"));
        selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());

        if (selectedFile == null && Common.SELECTED_FILE != null) {
            selectedFile = Common.SELECTED_FILE;
            return;
        }

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
    private void createHashCodeButtonOnAction() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws IOException, NoSuchAlgorithmException {
                try (FileInputStream inputStream = new FileInputStream(selectedFile)) {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");

                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        digest.update(buffer, 0, bytesRead);
                    }

                    byte[] hash = digest.digest();
                    return new BigInteger(1, hash).toString(16);
                }
            }
        };

        task.setOnRunning(event -> {
            hashCodeTextField.setText("Creating hash code...");
            hashCodeTextField.setStyle("-fx-opacity: 1");
            browseButton.setDisable(true);
            backButton.setDisable(true);
            createHashCodeButton.setDisable(true);
            nextButton.setDisable(true);
        });

        task.setOnSucceeded(event -> {
            hashCodeTextField.setText(task.getValue());
            browseButton.setDisable(false);
            backButton.setDisable(false);
            createHashCodeButton.setDisable(false);
            createHashCodeButton.setText("Create Hash Code");
            nextButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            browseButton.setDisable(false);
            backButton.setDisable(false);
            createHashCodeButton.setDisable(false);
            createHashCodeButton.setText("Create Hash Code");
            nextButton.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Create hash code failed");
            alert.setContentText(task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void nextButtonOnAction() {
        Common.SELECTED_FILE = selectedFile;
        Common.HASH_CODE = hashCodeTextField.getText();
        Helper.changePage(nextButton, "social-media.fxml");
    }

    @FXML
    private void backButtonOnAction() {
        Helper.changePage(backButton, "identity-input.fxml");
    }
}
