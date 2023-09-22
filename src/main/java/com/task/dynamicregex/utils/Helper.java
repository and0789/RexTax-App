package com.task.dynamicregex.utils;

import com.task.dynamicregex.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class Helper {

    public static void changePage(Node node, String fxmlFile) {
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        try {
            stage.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("RegTax Social Media Investigations in Volatile Memory");
        stage.show();
    }

}
