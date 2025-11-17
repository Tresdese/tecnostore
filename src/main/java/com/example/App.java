package com.example;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends Application {

    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Iniciando aplicación JavaFX");
        scene = new Scene(loadFXML("/com/example/tecnostore/gui/views/FXMLIngreso.fxml"), 640, 480);
        stage.setScene(scene);
        stage.show();
        LOGGER.info("Ventana principal mostrada");
    }

    public static void setRoot(String fxml) throws IOException {
        LOGGER.debug("Cambiando root a: {}", fxml);
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        URL url = App.class.getResource(fxml);
        if (url == null) {
            LOGGER.error("FXML no encontrado: {}", fxml);
            throw new IOException("FXML no encontrado: " + fxml);
        }
        try {
            Class<?> fxLoaderClass = Class.forName("javafx.fxml.FXMLLoader");
            java.lang.reflect.Method loadMethod = fxLoaderClass.getMethod("load", URL.class);
            return (Parent) loadMethod.invoke(null, url);
        } catch (ClassNotFoundException e) {
            LOGGER.error("JavaFX FXMLLoader no disponible para: {}", fxml, e);
            throw new IOException("JavaFX FXMLLoader no disponible: " + fxml, e);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Error al cargar FXML mediante reflexión: {}", fxml, e);
            throw new IOException("Error al cargar FXML mediante reflexión: " + fxml, e);
        }
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