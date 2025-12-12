package com.example.tecnostore.gui.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.gui.controller.FXMLSucursalModalController.SucursalData;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionSucursalesViewController {
    private static final Logger LOGGER = LogManager.getLogger(FXMLGestionSucursalesViewController.class);
    private static final String VIEW_BASE = "/com/example/tecnostore/gui/views/";

    @FXML private TableView<SucursalData> sucursalTable;

    private final ObservableList<SucursalData> sucursales = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            configurarTabla();
            cargarSucursalesInicial();
        } catch (Exception e) {
            LOGGER.error("Error al inicializar gestión de sucursales: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar la vista de sucursales: " + e.getMessage());
        }
    }

    @FXML
    private void onNuevaSucursal() {
        openSucursalModal("Nueva sucursal", true, null);
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
    private void onEliminarSucursal() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una sucursal.").showAndWait();
            return;
        }
        showEliminarModal();
    }

    private void openSucursalModal(String title, boolean isNew, SucursalData existente) {
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
                controller.setSucursalActual(clonar(existente));
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
                    sucursales.add(resultado);
                } else if (existente != null) {
                    int idx = sucursales.indexOf(existente);
                    if (idx >= 0) {
                        sucursales.set(idx, resultado);
                    }
                }
                refrescarTabla();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de sucursal: " + e.getMessage()).showAndWait();
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
            SucursalData seleccionada = getSeleccionada();
            if (seleccionada != null) {
                controller.setNombreSucursal(seleccionada.getNombre());
            }

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Eliminar sucursal");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            if (controller.isConfirmado() && seleccionada != null) {
                sucursales.remove(seleccionada);
                refrescarTabla();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de eliminacion: " + e.getMessage()).showAndWait();
        }
    }

    private void configurarTabla() {
        if (sucursalTable == null) return;
        // Crear columnas si no existen fx:id en el FXML
        if (sucursalTable.getColumns().isEmpty()) {
            TableColumnFactory.addColumn(sucursalTable, "Nombre", SucursalData::getNombre);
            TableColumnFactory.addColumn(sucursalTable, "Dirección", SucursalData::getDireccion);
            TableColumnFactory.addColumn(sucursalTable, "Teléfono", SucursalData::getTelefono);
            TableColumnFactory.addColumn(sucursalTable, "Estado", SucursalData::getEstado);
        }
        sucursalTable.setItems(sucursales);
    }

    private void cargarSucursalesInicial() {
        // TODO: Reemplazar con carga desde DAO cuando exista SucursalDAO
        sucursales.clear();
    }

    private void refrescarTabla() {
        if (sucursalTable != null) {
            sucursalTable.refresh();
        }
    }

    private boolean isSelectionEmpty() {
        return sucursalTable == null || sucursalTable.getSelectionModel().getSelectedItem() == null;
    }

    private SucursalData getSeleccionada() {
        return sucursalTable != null ? sucursalTable.getSelectionModel().getSelectedItem() : null;
    }

    private SucursalData clonar(SucursalData src) {
        if (src == null) return null;
        SucursalData copia = new SucursalData();
        copia.setNombre(src.getNombre());
        copia.setDireccion(src.getDireccion());
        copia.setTelefono(src.getTelefono());
        copia.setEstado(src.getEstado());
        return copia;
    }

    private Stage getOwnerStage() {
        if (sucursalTable == null || sucursalTable.getScene() == null) {
            return null;
        }
        return (Stage) sucursalTable.getScene().getWindow();
    }

    /** Utilidad para crear columnas simples sin fx:id. */
    private static class TableColumnFactory {
        private TableColumnFactory() {}

        static <T> void addColumn(TableView<T> table, String title, java.util.function.Function<T, String> mapper) {
            javafx.scene.control.TableColumn<T, String> col = new javafx.scene.control.TableColumn<>(title);
            col.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue())));
            table.getColumns().add(col);
        }
    }
}
