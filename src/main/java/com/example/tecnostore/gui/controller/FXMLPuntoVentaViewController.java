package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class FXMLPuntoVentaViewController {
    private final WindowServices windowServices = new WindowServices();

    @FXML private VBox rootContainer;

    @FXML
    private void onHacerVenta() {
        if (rootContainer == null || rootContainer.getScene() == null) {
            return;
        }

        try {
            windowServices.openModal("FXMLRegistroVentaModal.fxml", "Registro de venta");


        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el registro de venta: " + e.getMessage());
        }
    }
}