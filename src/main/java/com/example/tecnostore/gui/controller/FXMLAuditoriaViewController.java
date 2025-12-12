package com.example.tecnostore.gui.controller;

import java.io.IOException;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dto.LogAuditoriaDTO;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLAuditoriaViewController {
    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnExportar;

    @FXML
    private Button btnCerrar;

    @FXML private TableView<LogAuditoriaDTO> tblAuditoria;
    @FXML private TableColumn<LogAuditoriaDTO, String> colFecha;
    @FXML private TableColumn<LogAuditoriaDTO, String> colUsuario;
    @FXML private TableColumn<LogAuditoriaDTO, String> colAccion;
    @FXML private TableColumn<LogAuditoriaDTO, String> colDetalles;
    @FXML private TableColumn<LogAuditoriaDTO, String> colIp;
    @FXML private TextField txtUsuarioFiltro;
    @FXML private TextField txtAccionFiltro;

    private LogDAO logDAO;
    private ObservableList<LogAuditoriaDTO> masterData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        if (tblAuditoria != null) {
            tblAuditoria.setPlaceholder(new Label("No se encontraron registros de auditoría."));
        }
        configurarColumnas();

        try {
            this.logDAO = new LogDAO();
            cargarDatos();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el servicio de logs: " + e.getMessage());
        }
    }

    private void configurarColumnas() {
        if (colFecha != null) {
            colFecha.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getFecha() != null ? cd.getValue().getFecha().toString() : ""));
        }
        if (colUsuario != null) {
            colUsuario.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getNombreUsuario() != null && !cd.getValue().getNombreUsuario().isBlank()
                    ? cd.getValue().getNombreUsuario()
                    : (cd.getValue().getUsuarioId() != null ? cd.getValue().getUsuarioId().toString() : "")));
        }
        if (colAccion != null) {
            colAccion.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getAccion() != null ? cd.getValue().getAccion() : ""));
        }
        if (colDetalles != null) {
            colDetalles.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getDescripcion() != null ? cd.getValue().getDescripcion() : ""));
        }
        if (colIp != null) {
            colIp.setCellValueFactory(cd -> new SimpleStringProperty(""));
        }
    }

    private void cargarDatos() {
        if (logDAO == null) return;
        try {
            masterData.setAll(logDAO.obtenerTodos());
            aplicarFiltros();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Error al cargar los logs de auditoría: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String usuarioTerm = txtUsuarioFiltro != null ? txtUsuarioFiltro.getText().trim().toLowerCase() : "";
        String accionTerm = txtAccionFiltro != null ? txtAccionFiltro.getText().trim().toLowerCase() : "";

        var filtrados = masterData.stream().filter(log -> {
            boolean usuarioCoincide = usuarioTerm.isEmpty()
                || (log.getNombreUsuario() != null && log.getNombreUsuario().toLowerCase().contains(usuarioTerm))
                || (log.getUsuarioId() != null && log.getUsuarioId().toString().toLowerCase().contains(usuarioTerm));
            boolean accionCoincide = accionTerm.isEmpty()
                    || (log.getAccion() != null && log.getAccion().toLowerCase().contains(accionTerm));
            return usuarioCoincide && accionCoincide;
        }).toList();

        if (tblAuditoria != null) {
            tblAuditoria.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    @FXML
    private void onLimpiar() {
        if (txtUsuarioFiltro != null) txtUsuarioFiltro.clear();
        if (txtAccionFiltro != null) txtAccionFiltro.clear();
        aplicarFiltros();
    }

    @FXML
    private void onBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void onExportar() {
        try {
            WindowServices.showInformationDialog("Auditoría", "La exportación aún no está implementada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCerrar() {
        if (tblAuditoria != null && tblAuditoria.getScene() != null) {
            Stage stage = (Stage) tblAuditoria.getScene().getWindow();
            stage.close();
        }
    }
}
