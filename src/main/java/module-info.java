module com.task.dynamicregex {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.task.dynamicregex to javafx.fxml;
    exports com.task.dynamicregex;
    exports com.task.dynamicregex.entities;
    exports com.task.dynamicregex.utils;
    opens com.task.dynamicregex.utils to javafx.fxml;
    exports com.task.dynamicregex.controllers;
    opens com.task.dynamicregex.controllers to javafx.fxml;
}