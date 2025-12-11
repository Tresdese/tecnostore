package com.example.tecnostore.logic.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WindowServices {
    private static final Logger LOGGER = LogManager.getLogger(WindowServices.class);

    private static final String VIEW_PATH = "/com/example/tecnostore/gui/views/";

    public WindowServices() {
    }

    public FXMLLoader loadSimpleFXML(String fxmlPath) throws IOException, NullPointerException {
        return FXMLLoader.load(getClass().getResource(VIEW_PATH + fxmlPath));
    }

    public static Parent loadFXML(String fxmlPath) throws IOException {
        URL url = App.class.getResource(VIEW_PATH + fxmlPath);
        if (url == null) {
            LOGGER.error("FXML no encontrado: {}", fxmlPath);
            throw new IOException("FXML no encontrado: " + fxmlPath);
        }
        try {
            Class<?> fxLoaderClass = Class.forName("javafx.fxml.FXMLLoader");
            java.lang.reflect.Method loadMethod = fxLoaderClass.getMethod("load", URL.class);
            return (Parent) loadMethod.invoke(null, url);
        } catch (ClassNotFoundException e) {
            LOGGER.error("JavaFX FXMLLoader no disponible para: {}", fxmlPath, e);
            throw new IOException("JavaFX FXMLLoader no disponible: " + fxmlPath, e);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Error al cargar FXML mediante reflexión: {}", fxmlPath, e);
            throw new IOException("Error al cargar FXML mediante reflexión: " + fxmlPath, e);
        }
    }

    // abre una nueva ventana modal
    public void openModal(String fxmlPath, String title) throws IOException, NullPointerException {
        Parent vista = FXMLLoader.load(getClass().getResource(VIEW_PATH + fxmlPath));
        Scene escena = new Scene(vista);
        Stage escenario = new Stage();
        escenario.setScene(escena);
        escenario.setTitle(title);
        escenario.initModality(Modality.APPLICATION_MODAL);
        escenario.showAndWait();
    }

    // actualiza la ventana actual
    public void goToWindow(String fxmlPath, ActionEvent event, String title) throws IOException, NullPointerException {
        Parent vista = FXMLLoader.load(getClass().getResource(VIEW_PATH + fxmlPath));
        Scene escena = new Scene(vista);
        Node source = (Node) event.getSource();
        Stage escenario = (Stage) source.getScene().getWindow();
        escenario.setScene(escena);
        escenario.setTitle(title);
        escenario.show();
    }

    public void goToLoginWindow(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("FXMLIngreso.fxml", event, "Ingreso");
    }

    public void goToPrincipalWindow(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("FXMLPrincipal.fxml", event, "Ventana Principal");
    }

    public void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static boolean showConfirmationDialog(String titulo, String contenido)
            throws IOException, NullPointerException {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        Optional<ButtonType> buttonSeleccion = alerta.showAndWait();
        return (buttonSeleccion.get() == ButtonType.OK);
    }

    public static void showErrorDialog(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static void showInformationDialog(String titulo, String contenido)
            throws IOException, NullPointerException {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static void showWarningDialog(String titulo, String contenido)
            throws IOException, NullPointerException {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
