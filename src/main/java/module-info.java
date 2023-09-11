module com.andreseptian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires log4j;
    requires jasperreports;


    opens com.andreseptian to javafx.fxml;
    exports com.andreseptian;
    exports com.andreseptian.entities;
    exports com.andreseptian.utils;
    opens com.andreseptian.utils to javafx.fxml;
    exports com.andreseptian.controllers;
    opens com.andreseptian.controllers to javafx.fxml;
}