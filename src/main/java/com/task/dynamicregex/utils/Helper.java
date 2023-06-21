package com.task.dynamicregex.utils;

import com.task.dynamicregex.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class Helper {

    public static void changePage(Node node, String fxmlFile) throws IOException {
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        stage.getScene().setRoot(fxmlLoader.load());
        stage.setTitle("Dynamic Regex");
        stage.show();
    }

}
