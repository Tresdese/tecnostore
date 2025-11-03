module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires spring.security.crypto;
    requires java.base;
    
    opens com.example to javafx.fxml;
    opens com.example.tecnostore.gui.controller to javafx.fxml;
    exports com.example;
    exports com.example.tecnostore.logic.dao;

}
