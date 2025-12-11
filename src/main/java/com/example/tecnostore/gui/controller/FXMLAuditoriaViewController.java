package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class FXMLAuditoriaViewController {
    @FXML private TableView<?> resultTable;

    @FXML
    private void initialize() {
        resultTable.setPlaceholder(new Label("No se encontraron registros de auditoría."));
    }

    @FXML private void onLimpiar() { /* TODO: clear filters */ }
    @FXML private void onBuscar() { /* TODO: search logs */ }
}
