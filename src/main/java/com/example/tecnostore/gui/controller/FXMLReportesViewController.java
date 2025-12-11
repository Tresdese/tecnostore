package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class FXMLReportesViewController {
    @FXML private TableView<?> reportTable;

    @FXML
    private void initialize() {
        reportTable.setPlaceholder(new Label("Genere un reporte para visualizar los datos."));
    }

    @FXML private void onGenerar() { /* TODO: generate report */ }
    @FXML private void onExportar() { /* TODO: export report */ }
}
