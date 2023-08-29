package com.task.dynamicregex.controllers;

import com.task.dynamicregex.Main;
import com.task.dynamicregex.dao.ArtifactCategoryDao;
import com.task.dynamicregex.dao.SocmedRegexDao;
import com.task.dynamicregex.entities.ArtifactCategory;
import com.task.dynamicregex.entities.SocmedRegex;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.Helper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
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
    private ArtifactCategoryDao artifactCategoryDao;
    private ObservableList<ArtifactCategory> artifactCategoryList;
    private ArtifactCategory selectedArtifactCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socmedRegexDao = new SocmedRegexDao();
        artifactCategoryDao = new ArtifactCategoryDao();
        artifactCategoryList = FXCollections.observableArrayList();

        try {
            artifactCategoryList.addAll(artifactCategoryDao.findAll(Common.SOCIALMEDIA.id()));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        titleLabel.setText(Common.SOCIALMEDIA.name() + " Regex");
        subtitleLabel.setText(subtitleLabel.getText() + Common.SOCIALMEDIA.name());
        categoryComboBox.setItems(artifactCategoryList);
    }

    @FXML
    private void addCategoryButtonOnAction() throws SQLException, ClassNotFoundException {
        ArtifactCategory artifactCategory = new ArtifactCategory(
                UUID.randomUUID().toString(),
                newCategoryTextField.getText().trim(),
                Common.SOCIALMEDIA);

        if (artifactCategoryDao.save(artifactCategory) == 1) {
            refreshComboBox();
            newCategoryTextField.clear();
        }
    }

    @FXML
    private void addButtonOnAction() throws SQLException, ClassNotFoundException {
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
    private void newCategoryTextFieldOnKeyReleased() {
        addCategoryButton.disableProperty().bind(Bindings.isEmpty(newCategoryTextField.textProperty()));
    }

    @FXML
    private void newRegexTextFieldOnKeyReleased() {
        enableAddButton();
    }

    @FXML
    private void regexDescriptionTextFieldOnKeyReleased() {
        enableAddButton();
    }

    @FXML
    private void categoryComboBoxOnAction() {
        enableAddButton();
    }

    @FXML
    private void backButtonOnAction() {
        Helper.changePage(backButton, "socmed-regex.fxml");
    }

    @FXML
    private void editRemoveCategoryButtonOnAction() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("category-choice-dialog.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        ComboBox<ArtifactCategory> categoryNameComboBox = (ComboBox<ArtifactCategory>) dialogPane.lookup("#categoryNameComboBox");
        Optional<ButtonType> choiceDialogResult = showChoiceDialog(dialogPane, categoryNameComboBox);
        selectedArtifactCategory = categoryNameComboBox.getValue();

        if (choiceDialogResult.isPresent() && choiceDialogResult.get() == ButtonType.YES) {
            Optional<ArtifactCategory> dialogResult = showEditDialog(selectedArtifactCategory);
            if (dialogResult.isPresent() && artifactCategoryDao.update(dialogResult.get()) == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Artifact Category successfully edited");
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
                alert.showAndWait();
                refreshComboBox();
            }
        } else if (choiceDialogResult.isPresent() && choiceDialogResult.get() == ButtonType.NO) {
            Optional<ButtonType> removeConfirmButton = showConfirmAlert();
            if (removeConfirmButton.isPresent() && removeConfirmButton.get() == ButtonType.OK &&
                    artifactCategoryDao.delete(selectedArtifactCategory) == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Artifact Category successfully removed");
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
                alert.showAndWait();
                refreshComboBox();
            }
        }
    }

    private void enableAddButton() {
        addButton.disableProperty().bind(
                Bindings.isEmpty(newRegexTextField.textProperty())
                        .or(Bindings.isEmpty(regexDescriptionTextField.textProperty()))
                        .or(Bindings.isNull(categoryComboBox.getSelectionModel().selectedItemProperty())));
    }

    private void refreshComboBox() throws SQLException, ClassNotFoundException {
        selectedArtifactCategory = null;
        artifactCategoryList.clear();
        artifactCategoryList.addAll(artifactCategoryDao.findAll(Common.SOCIALMEDIA.id()));
        categoryComboBox.setItems(artifactCategoryList);
    }

    private Optional<ButtonType> showChoiceDialog(DialogPane dialogPane, ComboBox<ArtifactCategory> categoryNameComboBox) {
        categoryNameComboBox.setItems(artifactCategoryList);
        Platform.runLater(categoryNameComboBox::requestFocus);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Edit/Remove");

        Button editButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
        editButton.setText("Edit");
        editButton.getStyleClass().add("green");
        editButton.setDisable(true);
        Button removeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.NO);
        removeButton.setText("Remove");
        removeButton.getStyleClass().add("red");
        removeButton.setDisable(true);

        categoryNameComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, category, t1) -> {
                    editButton.setDisable(false);
                    removeButton.setDisable(false);
                });

        return dialog.showAndWait();
    }

    private Optional<ArtifactCategory> showEditDialog(ArtifactCategory artifactCategory) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("category-edit-dialog.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        TextField categoryNameTextField = (TextField) dialogPane.lookup("#categoryNameTextField");
        categoryNameTextField.setText(artifactCategory.getName());

        Platform.runLater(() -> {
            categoryNameTextField.requestFocus();
            categoryNameTextField.positionCaret(categoryNameTextField.getText().length());
            categoryNameTextField.deselect();
        });

        Dialog<ArtifactCategory> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Edit");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("red");

        categoryNameTextField.setOnKeyReleased(keyEvent ->
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(categoryNameTextField.getText().isBlank()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new ArtifactCategory(
                        artifactCategory.getId(),
                        categoryNameTextField.getText().trim(),
                        Common.SOCIALMEDIA);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<ButtonType> showConfirmAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to remove " + selectedArtifactCategory.getName() + "?");
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Remove");
        alert.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("red");
        return alert.showAndWait();
    }
}
