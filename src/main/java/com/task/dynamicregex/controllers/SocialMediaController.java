package com.task.dynamicregex.controllers;

import com.task.dynamicregex.dao.SocialMediaDao;
import com.task.dynamicregex.entities.SocialMedia;
import com.task.dynamicregex.utils.Common;
import com.task.dynamicregex.utils.CustomListCell;
import com.task.dynamicregex.utils.Helper;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

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
    private SocialMedia selectedSocialMedia;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socialMediaList = FXCollections.observableArrayList();
        socialMediaDao = new SocialMediaDao();

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
    private void addButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        SocialMedia socialMedia = new SocialMedia(
                UUID.randomUUID().toString(),
                addSocialMediaTextField.getText().trim());

        if (socialMediaDao.save(socialMedia) == 1) {
            socialMediaList.clear();
            socialMediaList.addAll(socialMediaDao.findAll());
            displaySocialMediaList();
            addSocialMediaTextField.clear();
        }
    }

    @FXML
    private void firstSocialMediaListViewOnMouseClicked(MouseEvent mouseEvent) {
        selectedSocialMedia = firstSocialMediaListView.getSelectionModel().getSelectedItem();
        if (selectedSocialMedia != null) {
            Common.SOCIALMEDIA = selectedSocialMedia;
            Helper.changePage(firstSocialMediaListView, "socmed-regex.fxml");
        }
    }

    @FXML
    private void secondSocialMediaListViewOnMouseClicked(MouseEvent mouseEvent) {
        selectedSocialMedia = secondSocialMediaListView.getSelectionModel().getSelectedItem();
        if (selectedSocialMedia != null) {
            Common.SOCIALMEDIA = selectedSocialMedia;
            Helper.changePage(secondSocialMediaListView, "socmed-regex.fxml");
        }
    }

    private void displaySocialMediaList() {
        int halfIndex = (int) Math.ceil(socialMediaList.size() / 2.0);
        List<SocialMedia> firstHalf  = socialMediaList.subList(0, halfIndex);
        List<SocialMedia> secondHalf = socialMediaList.subList(halfIndex, socialMediaList.size());

        firstSocialMediaListView.setItems(FXCollections.observableArrayList(firstHalf));
        secondSocialMediaListView.setItems(FXCollections.observableArrayList(secondHalf));
    }

    @FXML
    private void addSocialMediaTextFieldOnKeyReleased(KeyEvent keyEvent) {
        addButton.disableProperty().bind(Bindings.isEmpty(addSocialMediaTextField.textProperty()));
    }

    @FXML
    private void backButtonOnAction(ActionEvent actionEvent) {
        Helper.changePage(backButton, "file-input.fxml");
    }
}
