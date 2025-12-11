package com.example.tecnostore.gui.controller;

import java.io.IOException;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXMLRegistroVentaModalController {
    @FXML private TextField searchField;
    @FXML private TableView<?> productTable;
    @FXML private Label subtotalLabel;
    @FXML private Label ivaLabel;
    @FXML private Label totalLabel;

    @FXML private void onBuscar() { /* TODO: buscar */ }

    @FXML private void onCancelarVenta() { searchField.getScene().getWindow().hide(); }

    @FXML
    private void onHacerPago() {
        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPagoModal.fxml"));
            Parent root = loader.load();
            FXMLPagoModalController controller = loader.getController();
            controller.setTotalPagar(obtenerTotal());
            controller.setOwner(owner);

            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    private double obtenerTotal() {
        if (totalLabel == null || totalLabel.getText() == null) {
            return 0;
        }
        String texto = totalLabel.getText().replaceAll("[^0-9,.-]", "").replace(",", "");
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
