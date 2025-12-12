package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dto.LogAuditoriaDTO;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.Collections;

public class FXMLAuditoriaViewController {
    @FXML private TableView<LogAuditoriaDTO> resultTable;

    private LogDAO logDAO;
    private ObservableList<LogAuditoriaDTO> masterData;


    @FXML
    private void initialize() {
        resultTable.setPlaceholder(new Label("No se encontraron registros de auditoría."));
        masterData = FXCollections.observableArrayList();

        try {
            this.logDAO = new LogDAO();
            cargarDatos();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el servicio de logs: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        if (logDAO == null) return;
        try {
            masterData.setAll(logDAO.obtenerTodos());
            resultTable.setItems(masterData);
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Error al cargar los logs de auditoría: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        // No hay campos de filtro en la interfaz, por lo que Limpiar restablece la vista.

        // Se establece la lista de la tabla al estado inicial (la lista completa).
        resultTable.setItems(masterData);
    }

    @FXML private void onBuscar() {
        // Dado que no hay filtros de búsqueda implementados en la UI,
        // la acción "Buscar" simplemente recarga los datos desde la BD.
        cargarDatos();
    }
}