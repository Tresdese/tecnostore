package com.example.tecnostore.gui.controller;

import java.io.IOException;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLAjusteInventarioViewController {

    // Necesario para acceder a los métodos delegados
    private final WindowServices windowServices = new WindowServices();

    @FXML private TextField txtIdProducto;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtExistenciaActual;
    @FXML private TextField txtCantidadAjuste;
    @FXML private TextArea txtMotivo;
    @FXML private Button btnAplicar;
    @FXML private Button btnCancelar;
    @FXML private TableView<?> ajusteTable;

    @FXML
    private void initialize() {
        ajusteTable.setPlaceholder(new Label("No hay registros de ajuste de inventario."));
    }

    @FXML private void onBuscar() {
        // Lógica de búsqueda de ajustes (ej., llamando a AjusteService.buscarTodos())
    }

    // *** DELEGACIÓN A WINDOWSERVICES ***
    @FXML
    private void onNuevoAjuste() {
        try {
            // Delegamos la apertura del modal al servicio (openModal es para modales sin datos complejos de entrada)
            windowServices.openModal("FXMLAjusteModal.fxml", "Nuevo Ajuste de Inventario");

            // Si la modal fue exitosa, aquí se llamaría a onBuscar() para refrescar la tabla.
            // onBuscar();

        } catch (Exception e) {
            // Usamos WindowServices.showErrorDialog en lugar de la Alert manual
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de ajuste: " + e.getMessage());
        }
    }
}