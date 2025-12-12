package com.example.tecnostore.gui.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dto.LogAuditoriaDTO;
import com.example.tecnostore.logic.servicios.ReporteSeguridadService;
import com.example.tecnostore.logic.utils.Sesion;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class FXMLReportesViewController {
    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private Button btnGenerar;

    @FXML private TableColumn<LogAuditoriaDTO, String> colFecha;
    @FXML private TableColumn<LogAuditoriaDTO, String> colTipo;
    @FXML private TableColumn<LogAuditoriaDTO, String> colDescripcion;
    @FXML private TableColumn<LogAuditoriaDTO, String> colEstado;
    @FXML
    private Button btnVerDetalle;

    @FXML
    private Button btnExportar;

    @FXML
    private Button btnCerrar;

    @FXML private TableView<LogAuditoriaDTO> tblReportes;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> cmbTipoReporte;

    private ReporteSeguridadService reporteService;
    private LogDAO logDAO;
    private final ObservableList<LogAuditoriaDTO> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        if (tblReportes != null) {
            tblReportes.setPlaceholder(new Label("Genere un reporte para visualizar los datos."));
        }

        configurarColumnas();

        if (cmbTipoReporte != null) {
            cmbTipoReporte.setItems(FXCollections.observableArrayList(
                    Arrays.asList("Todos", "Ventas", "Inventario", "KPIs")));
            cmbTipoReporte.getSelectionModel().selectFirst();
        }

        try {
            this.reporteService = new ReporteSeguridadService();
            this.logDAO = new LogDAO();
            actualizarTablaLogs();
            if (statusLabel != null) {
                statusLabel.setText("Listo para generar reportes automáticos.");
            }
        } catch (Exception e) {
            if (statusLabel != null) {
                statusLabel.setText("No se pudo inicializar el servicio de reportes: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onGenerar(ActionEvent event) {
        if (reporteService == null) {
            setStatus("Servicio de reportes no disponible.");
            return;
        }

        try {
            String tipo = cmbTipoReporte != null ? cmbTipoReporte.getValue() : "Todos";
            Path carpetaBase = prepararCarpetaSalida();
            Path carpeta = carpetaBase;

            if ("Ventas".equalsIgnoreCase(tipo)) {
                reporteService.generarReporteVentas(construirRuta(carpetaBase, "ventas"), Sesion.getRolActual());
            } else if ("Inventario".equalsIgnoreCase(tipo)) {
                reporteService.generarReporteInventario(construirRuta(carpetaBase, "inventario"), Sesion.getRolActual());
            } else if ("KPIs".equalsIgnoreCase(tipo)) {
                reporteService.generarReporteKPIs(construirRuta(carpetaBase, "kpis"), Sesion.getRolActual());
            } else {
                var rutas = reporteService.generarReportesAutomaticos(carpetaBase.toString(), Sesion.getRolActual());
                carpeta = rutas.isEmpty() ? carpetaBase : rutas.get(0).getParent();
            }

            actualizarTablaLogs();
            setStatus(carpeta != null
                    ? "Reportes generados en " + carpeta.toAbsolutePath()
                    : "Reportes generados.");
        } catch (Exception e) {
            setStatus("Error al generar reportes: " + e.getMessage());
        }
    }

    @FXML
    private void onExportar(ActionEvent event) {
        onGenerar(event);
    }

    @FXML
    private void onVerDetalle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de reporte");
        alert.setHeaderText(null);
        alert.setContentText("Seleccione un registro en la tabla para mostrar detalles.");
        alert.showAndWait();
    }

    @FXML
    private void onCerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void actualizarTablaLogs() {
        if (tblReportes == null || logDAO == null) {
            return;
        }
        try {
            data.setAll(logDAO.obtenerTodos());
            tblReportes.setItems(data);
        } catch (Exception e) {
            setStatus("No se pudieron cargar logs: " + e.getMessage());
        }
    }

    private void setStatus(String mensaje) {
        if (statusLabel != null) {
            statusLabel.setText(mensaje);
        }
    }

    private Path prepararCarpetaSalida() throws Exception {
        Path base = Paths.get("logs", "reportes");
        Files.createDirectories(base);
        return base;
    }

    private String construirRuta(Path base, String prefijo) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return base.resolve("reporte_" + prefijo + "_" + timestamp + ".pdf").toString();
    }

    private void configurarColumnas() {
        if (colFecha != null) {
            colFecha.setCellValueFactory(cell -> {
                var fecha = cell.getValue().getFecha();
                return new SimpleStringProperty(fecha != null ? fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
            });
        }
        if (colTipo != null) {
            colTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAccion()));
        }
        if (colDescripcion != null) {
            colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));
        }
        if (colEstado != null) {
            colEstado.setCellValueFactory(cell -> new SimpleStringProperty("GENERADO"));
        }
    }
}
