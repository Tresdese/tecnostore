package com.example.tecnostore.gui.controller;

import java.io.IOException;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPuntoVentaViewController {
    private static final String VIEW_PATH = "/com/example/tecnostore/gui/views/";

    @FXML private VBox rootContainer;

    @FXML
    private void onHacerVenta() {
        if (rootContainer == null || rootContainer.getScene() == null) {
            return;
        }
        Stage owner = (Stage) rootContainer.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PATH + "FXMLRegistroVentaModal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Registro de venta");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el registro de venta: " + e.getMessage());
        }
    }
}
