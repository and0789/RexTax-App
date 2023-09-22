package com.task.dynamicregex.controllers;

import com.task.dynamicregex.Main;
import com.task.dynamicregex.dao.ArtifactCategoryDao;
import com.task.dynamicregex.dao.SocialMediaDao;
import com.task.dynamicregex.entities.SocialMedia;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.CustomListCell;
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
import java.util.*;

public class SocialMediaController implements Initializable {

    @FXML
    private TextField addSocialMediaTextField;
    @FXML
    private Button addButton;
    @FXML
    private ListView<SocialMedia> firstSocialMediaListView;
    @FXML
    private ListView<SocialMedia> secondSocialMediaListView;
    @FXML
    private Button backButton;

    private ObservableList<SocialMedia> socialMediaList;
    private SocialMediaDao socialMediaDao;
    private ArtifactCategoryDao artifactCategoryDao;
    private SocialMedia selectedSocialMedia;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socialMediaList = FXCollections.observableArrayList();
        socialMediaDao = new SocialMediaDao();
        artifactCategoryDao = new ArtifactCategoryDao();

        try {
            socialMediaList.addAll(socialMediaDao.findAll());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        displaySocialMediaList();
        firstSocialMediaListView.setCellFactory(param -> new CustomListCell());
        secondSocialMediaListView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void addButtonOnAction() throws SQLException, ClassNotFoundException {
        SocialMedia socialMedia = new SocialMedia(
                UUID.randomUUID().toString(),
                addSocialMediaTextField.getText().trim());

        if (socialMediaDao.save(socialMedia) == 1) {
            refreshTableViews();
        }
    }

    @FXML
    private void firstSocialMediaListViewOnMouseClicked() {
        selectedSocialMedia = firstSocialMediaListView.getSelectionModel().getSelectedItem();
        if (selectedSocialMedia != null) {
            Common.SOCIALMEDIA = selectedSocialMedia;
            Helper.changePage(firstSocialMediaListView, "socmed-regex.fxml");
        }
    }

    @FXML
    private void secondSocialMediaListViewOnMouseClicked() {
        selectedSocialMedia = secondSocialMediaListView.getSelectionModel().getSelectedItem();
        if (selectedSocialMedia != null) {
            Common.SOCIALMEDIA = selectedSocialMedia;
            Helper.changePage(secondSocialMediaListView, "socmed-regex.fxml");
        }
    }

    @FXML
    private void addSocialMediaTextFieldOnKeyReleased() {
        addButton.disableProperty().bind(Bindings.isEmpty(addSocialMediaTextField.textProperty()));
    }

    @FXML
    private void backButtonOnAction() {
        Helper.changePage(backButton, "file-input.fxml");
    }

    @FXML
    private void editRemoveSocialMediaButtonOnAction() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("socialmedia-choice-dialog.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        ComboBox<SocialMedia> socialMediaNameComboBox = (ComboBox<SocialMedia>) dialogPane.lookup("#socialMediaNameComboBox");
        Optional<ButtonType> choiceDialogResult = showChoiceDialog(dialogPane, socialMediaNameComboBox);
        selectedSocialMedia = socialMediaNameComboBox.getValue();

        if (choiceDialogResult.isPresent() && choiceDialogResult.get() == ButtonType.YES) {
            Optional<SocialMedia> dialogResult = showEditDialog(selectedSocialMedia);
            if (dialogResult.isPresent() && socialMediaDao.update(dialogResult.get()) == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Social Media successfully edited");
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
                alert.showAndWait();
                refreshTableViews();
            }
        } else if (choiceDialogResult.isPresent() && choiceDialogResult.get() == ButtonType.NO) {
            if (artifactCategoryDao.getArtifactCategoryCount(selectedSocialMedia) > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText("Can't remove \"" + selectedSocialMedia.name() + "\"");
                alert.setContentText("Check if there is Artifact Category in the Social Media!");
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
                alert.showAndWait();
                return;
            }

            Optional<ButtonType> removeConfirmButton = showConfirmAlert();
            if (removeConfirmButton.isPresent() && removeConfirmButton.get() == ButtonType.OK &&
                    socialMediaDao.delete(selectedSocialMedia) == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("SocialMedia successfully removed");
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
                alert.showAndWait();
                refreshTableViews();
            }
        }
    }

    private void displaySocialMediaList() {
        int halfIndex = (int) Math.ceil(socialMediaList.size() / 2.0);
        List<SocialMedia> firstHalf  = socialMediaList.subList(0, halfIndex);
        List<SocialMedia> secondHalf = socialMediaList.subList(halfIndex, socialMediaList.size());

        firstSocialMediaListView.setItems(FXCollections.observableArrayList(firstHalf));
        secondSocialMediaListView.setItems(FXCollections.observableArrayList(secondHalf));
    }

    private void refreshTableViews() throws SQLException, ClassNotFoundException {
        selectedSocialMedia = null;
        socialMediaList.clear();
        socialMediaList.addAll(socialMediaDao.findAll());
        displaySocialMediaList();
        addSocialMediaTextField.clear();
    }

    private Optional<ButtonType> showChoiceDialog(DialogPane dialogPane, ComboBox<SocialMedia> socialMediaNameComboBox) {
        socialMediaNameComboBox.setItems(socialMediaList);
        Platform.runLater(socialMediaNameComboBox::requestFocus);

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

        socialMediaNameComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, socialMedia, t1) -> {
                    editButton.setDisable(false);
                    removeButton.setDisable(false);
                });

        return dialog.showAndWait();
    }

    private Optional<SocialMedia> showEditDialog(SocialMedia socialMedia) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("socialmedia-edit-dialog.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        TextField socialMediaNameTextField = (TextField) dialogPane.lookup("#socialMediaNameTextField");
        socialMediaNameTextField.setText(socialMedia.name());

        Platform.runLater(() -> {
            socialMediaNameTextField.requestFocus();
            socialMediaNameTextField.positionCaret(socialMediaNameTextField.getText().length());
            socialMediaNameTextField.deselect();
        });

        Dialog<SocialMedia> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Edit");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("red");

        socialMediaNameTextField.setOnKeyReleased(keyEvent ->
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(socialMediaNameTextField.getText().isBlank()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new SocialMedia(
                        socialMedia.id(),
                        socialMediaNameTextField.getText().trim());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<ButtonType> showConfirmAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to remove \"" + selectedSocialMedia.name() + "\"?");
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/task/dynamicregex/style.css")).toExternalForm());
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Remove");
        alert.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("red");
        return alert.showAndWait();
    }
}
