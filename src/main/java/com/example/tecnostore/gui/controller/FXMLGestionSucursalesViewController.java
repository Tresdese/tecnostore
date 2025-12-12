package com.example.tecnostore.gui.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.gui.controller.FXMLSucursalModalController.SucursalData;
import com.example.tecnostore.logic.dao.SucursalDAO;
import com.example.tecnostore.logic.dto.SucursalDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionSucursalesViewController {
    @FXML
    private Button agregarButton;

    @FXML
    private Button editarButton;

    @FXML
    private Button eliminarButton;

    @FXML
    private TextField searchField;

    @FXML private TableColumn<SucursalDTO, Number> colId;
    @FXML private TableColumn<SucursalDTO, String> colNombre;
    @FXML private TableColumn<SucursalDTO, String> colDireccion;
    @FXML private TableColumn<SucursalDTO, String> colTelefono;
    @FXML private TableColumn<SucursalDTO, String> colActivo;

    private static final Logger LOGGER = LogManager.getLogger(FXMLGestionSucursalesViewController.class);
    private static final String VIEW_BASE = "/com/example/tecnostore/gui/views/";

    @FXML private TableView<SucursalDTO> branchTable;

    private final ObservableList<SucursalDTO> sucursales = FXCollections.observableArrayList();
    private SucursalDAO sucursalDAO;
    private WindowServices windowServices = new WindowServices();

    @FXML
    private void initialize() {
        setUserRole(Sesion.getRolActual());
        try {
            sucursalDAO = new SucursalDAO();
            configurarTabla();
            cargarSucursales();
        } catch (Exception e) {
            LOGGER.error("Error al inicializar gestión de sucursales: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar la vista de sucursales: " + e.getMessage());
        }
    }

    private void setUserRole(String usuarioRol) {
        if (usuarioRol.equals("ADMIN")) {
            windowServices.setButtonVisibility(agregarButton, true);
            windowServices.setButtonVisibility(editarButton, true);
            windowServices.setButtonVisibility(eliminarButton, true);
        } else if (usuarioRol.equals("CAJERO")) {
            windowServices.setButtonVisibility(agregarButton, false);
            windowServices.setButtonVisibility(editarButton, false);
            windowServices.setButtonVisibility(eliminarButton, false);
        } else if (usuarioRol.equals("GERENTE DE INVENTARIO")) {
            windowServices.setButtonVisibility(agregarButton, false);
            windowServices.setButtonVisibility(editarButton, false);
            windowServices.setButtonVisibility(eliminarButton, false);
        } else if (usuarioRol.equals("SUPERADMINISTRADOR")) {
            windowServices.setButtonVisibility(agregarButton, true);
            windowServices.setButtonVisibility(editarButton, true);
            windowServices.setButtonVisibility(eliminarButton, true);
        }
    }

    @FXML
    private void onNuevaSucursal() {
        openSucursalModal("Nueva sucursal", true, null);
    }

    @FXML
    private void onAgregar() {
        onNuevaSucursal();
    }

    @FXML
    private void onEditarSeleccionada() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una sucursal.").showAndWait();
            return;
        }
        openSucursalModal("Editar sucursal", false, getSeleccionada());
    }

    @FXML
    private void onEditar() {
        onEditarSeleccionada();
    }

    @FXML
    private void onEliminarSucursal() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una sucursal.").showAndWait();
            return;
        }
        showEliminarModal();
    }

    @FXML
    private void onEliminar() {
        onEliminarSucursal();
    }

    @FXML
    private void onBuscar() {
        cargarSucursales();
    }

    @FXML
    private void onLimpiar() {
        if (searchField != null) {
            searchField.clear();
        }
        cargarSucursales();
    }

    private void openSucursalModal(String title, boolean isNew, SucursalDTO existente) {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLSucursalModal.fxml"));
            Parent root = loader.load();
            FXMLSucursalModalController controller = loader.getController();
            controller.setIsNew(isNew);
            controller.setOwner(owner);
            if (!isNew && existente != null) {
                controller.setSucursalActual(desdeDTO(existente));
            }

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            SucursalData resultado = controller.getSucursalActual();
            if (resultado != null) {
                if (isNew) {
                    insertarSucursal(resultado);
                } else if (existente != null) {
                    actualizarSucursal(existente, resultado);
                }
                cargarSucursales();
            }
        } catch (IOException e) {
            LOGGER.error("Error al abrir/cargar el modal de sucursal: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de sucursal: " + e.getMessage());
        }
    }

    private void showEliminarModal() {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLEliminarSucursalModal.fxml"));
            Parent root = loader.load();
            FXMLEliminarSucursalModalController controller = loader.getController();
            SucursalDTO dto = getSeleccionada();
            if (dto != null) {
                controller.setNombreSucursal(dto.getNombre());
            }

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Eliminar sucursal");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            if (controller.isConfirmado() && dto != null) {
                darBajaLogica(dto);
                cargarSucursales();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de eliminacion: " + e.getMessage()).showAndWait();
        }
    }

    private void configurarTabla() {
        if (branchTable == null) return;
        if (colId != null) colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getId()));
        if (colNombre != null) colNombre.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getNombre()));
        if (colDireccion != null) colDireccion.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDireccion()));
        if (colTelefono != null) colTelefono.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelefono()));
        if (colActivo != null) colActivo.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().isActivo() ? "Sí" : "No"));
        branchTable.setItems(sucursales);
    }

    private void cargarSucursales() {
        sucursales.clear();
        try {
            if (sucursalDAO == null) {
                sucursalDAO = new SucursalDAO();
            }
            var todas = sucursalDAO.seleccionarTodos();
            String filtro = searchField != null ? searchField.getText() : null;
            if (filtro != null && !filtro.isBlank()) {
                String term = filtro.trim().toLowerCase();
                todas = todas.stream()
                        .filter(s -> s.getNombre().toLowerCase().contains(term)
                                || s.getDireccion().toLowerCase().contains(term)
                                || s.getTelefono().toLowerCase().contains(term))
                        .toList();
            }
            sucursales.addAll(todas);
            refrescarTabla();
        } catch (Exception e) {
            LOGGER.error("No se pudieron cargar sucursales: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudieron cargar las sucursales: " + e.getMessage());
        }
    }

    private Stage getOwnerStage() {
        if (branchTable == null || branchTable.getScene() == null) {
            return null;
        }
        return (Stage) branchTable.getScene().getWindow();
    }

    @FXML
    private void onCerrar() {
        Stage stage = getOwnerStage();
        if (stage != null) {
            stage.close();
        }
    }

    private void insertarSucursal(SucursalData data) {
        try {
            SucursalDTO dto = aDTO(data);
            sucursalDAO.insertar(dto);
            WindowServices.showInformationDialog("Éxito", "Sucursal creada correctamente");
        } catch (Exception e) {
            LOGGER.error("Error al insertar sucursal: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo crear la sucursal: " + e.getMessage());
        }
    }

    private void actualizarSucursal(SucursalDTO existente, SucursalData nueva) {
        try {
            existente.setNombre(nueva.getNombre());
            existente.setDireccion(nueva.getDireccion());
            existente.setTelefono(nueva.getTelefono());
            existente.setActivo("Activo".equalsIgnoreCase(nueva.getEstado()));
            sucursalDAO.actualizar(existente);
            WindowServices.showInformationDialog("Éxito", "Sucursal actualizada correctamente");
        } catch (Exception e) {
            LOGGER.error("Error al actualizar sucursal: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo actualizar la sucursal: " + e.getMessage());
        }
    }

    private void darBajaLogica(SucursalDTO dto) {
        try {
            sucursalDAO.darBajaLogica(dto);
            WindowServices.showInformationDialog("Éxito", "Sucursal eliminada correctamente");
        } catch (Exception e) {
            LOGGER.error("Error al eliminar sucursal: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo eliminar la sucursal: " + e.getMessage());
        }
    }

    private void refrescarTabla() {
        if (branchTable != null) {
            branchTable.refresh();
        }
    }

    private boolean isSelectionEmpty() {
        return branchTable == null || branchTable.getSelectionModel().getSelectedItem() == null;
    }

    private SucursalDTO getSeleccionada() {
        return branchTable != null ? branchTable.getSelectionModel().getSelectedItem() : null;
    }

    private SucursalData desdeDTO(SucursalDTO dto) {
        if (dto == null) return null;
        SucursalData data = new SucursalData();
        data.setNombre(dto.getNombre());
        data.setDireccion(dto.getDireccion());
        data.setTelefono(dto.getTelefono());
        data.setEstado(dto.isActivo() ? "Activo" : "Inactivo");
        return data;
    }

    private SucursalDTO aDTO(SucursalData data) {
        if (data == null) return null;
        SucursalDTO dto = new SucursalDTO();
        dto.setNombre(data.getNombre());
        dto.setDireccion(data.getDireccion());
        dto.setTelefono(data.getTelefono());
        dto.setActivo("Activo".equalsIgnoreCase(data.getEstado()));
        return dto;
    }

    /** Utilidad para crear columnas simples sin fx:id. */
    private static class TableColumnFactory { private TableColumnFactory() {} }
}
