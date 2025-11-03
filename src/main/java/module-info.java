module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires spring.security.crypto;
    
    opens com.example to javafx.fxml;
    exports com.example;
    exports com.example.tecnostore.dao;

}
