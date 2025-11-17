package com.example;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/com/example/tecnostore/gui/views/FXMLIngreso.fxml"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        URL url = App.class.getResource(fxml);
        if (url == null) {
            throw new IOException("FXML no encontrado: " + fxml);
        }
        try {
            Class<?> fxLoaderClass = Class.forName("javafx.fxml.FXMLLoader");
            java.lang.reflect.Method loadMethod = fxLoaderClass.getMethod("load", URL.class);
            return (Parent) loadMethod.invoke(null, url);
        } catch (ClassNotFoundException e) {
            throw new IOException("JavaFX FXMLLoader no disponible: " + fxml, e);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Error al cargar FXML mediante reflexión: " + fxml, e);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}