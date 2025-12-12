module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires java.base;
    requires transitive javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.github.librepdf.openpdf;
    requires org.jfree.jfreechart;
    requires java.desktop;
    requires googleauth;
    
    opens com.example to javafx.fxml;
    opens com.example.tecnostore.gui.controller to javafx.fxml;
    opens com.example.tecnostore.logic.dto to javafx.base, javafx.fxml;
    exports com.example;
    exports com.example.tecnostore.logic.dao;
    exports com.example.tecnostore.logic.dto;
    exports com.example.tecnostore.logic.servicios;

}