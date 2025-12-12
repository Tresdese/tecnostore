package com.example;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.tecnostore.logic.utils.WindowServices;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Iniciando aplicación JavaFX");
        scene = new Scene(WindowServices.loadFXML("FXMLIngreso.fxml"), 640, 480);
        stage.setScene(scene);
        stage.show();
        LOGGER.info("Ventana principal mostrada");
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("Lanzando la aplicación");
            launch();
        } catch (Exception e) {
            LOGGER.fatal("Error al lanzar la aplicación", e);
            throw e;
        }
    }
}