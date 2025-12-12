package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLAuditoriaViewController {
    @FXML private TableView<?> tblAuditoria;
    @FXML private TextField txtUsuarioFiltro;
    @FXML private TextField txtAccionFiltro;

    @FXML
    private void initialize() {
        if (tblAuditoria != null) {
            tblAuditoria.setPlaceholder(new Label("No se encontraron registros de auditoría."));
        }
    }

    @FXML private void onLimpiar() {
        if (txtUsuarioFiltro != null) txtUsuarioFiltro.clear();
        if (txtAccionFiltro != null) txtAccionFiltro.clear();
        // TODO: recargar tabla sin filtros
    }

    @FXML private void onBuscar() {
        // TODO: aplicar filtros txtUsuarioFiltro / txtAccionFiltro a la tabla
    }

    @FXML private void onExportar() {
        // TODO: exportar registros a CSV/PDF
    }

    @FXML private void onCerrar() {
        if (tblAuditoria != null && tblAuditoria.getScene() != null) {
            Stage stage = (Stage) tblAuditoria.getScene().getWindow();
            stage.close();
        }
    }
}
