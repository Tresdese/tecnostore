package com.example.tecnostore.gui.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLAjusteInventarioViewController {
    @FXML
    private TextField txtIdProducto;

    @FXML
    private TextField txtNombreProducto;

    @FXML
    private TextField txtExistenciaActual;

    @FXML
    private TextField txtCantidadAjuste;

    @FXML
    private TextArea txtMotivo;

    @FXML
    private Button btnAplicar;

    @FXML
    private Button btnCancelar;

    @FXML private TableView<?> ajusteTable;

    @FXML
    private void initialize() {
        ajusteTable.setPlaceholder(new Label("No hay registros de ajuste de inventario."));
    }

    @FXML private void onBuscar() { /* TODO: search adjustments */ }

    @FXML
    private void onNuevoAjuste() {
        if (ajusteTable == null || ajusteTable.getScene() == null) {
            return;
        }

        Stage owner = (Stage) ajusteTable.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLAjusteModal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Nuevo Ajuste de Inventario");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de ajuste: " + e.getMessage()).showAndWait();
        }
    }
}
