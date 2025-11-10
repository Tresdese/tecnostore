module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires spring.security.crypto;
    requires java.base;
    requires javafx.graphics;
    
    opens com.example to javafx.fxml;
    opens com.example.tecnostore.gui.controller to javafx.fxml;
    opens com.example.tecnostore.logic.dto to javafx.base, javafx.fxml;
    exports com.example;
    exports com.example.tecnostore.logic.dao;

}
