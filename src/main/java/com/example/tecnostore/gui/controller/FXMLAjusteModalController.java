package com.example.tecnostore.gui.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Window;

public class FXMLAjusteModalController {
    @FXML
    private Button btnConfirmar;

    @FXML
    private Button btnCancelar;

    private static final Logger LOGGER = LogManager.getLogger(FXMLAjusteModalController.class);

    @FXML private ComboBox<ProductoDTO> productoCombo;
    @FXML private RadioButton entradaRadio;
    @FXML private RadioButton salidaRadio;
    @FXML private TextField cantidadField;
    @FXML private TextArea razonArea;
    @FXML private Label realizadoPor;

    private ProductoDAO productoDAO;
    private ToggleGroup tipoMovimientoGroup;
    private Window owner;
//
    public void setOwner(Window owner) {
        this.owner = owner;
    }

    @FXML
    private void initialize() {
        try {
            productoDAO = new ProductoDAO();
            cargarProductos();
            configurarRadios();
            updateRealizadoPor();
        } catch (Exception e) {
            LOGGER.error("Error inicializando modal de ajuste: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el ajuste: " + e.getMessage());
            deshabilitar();
        }
    }

    private void cargarProductos() throws Exception {
        List<ProductoDTO> productos = productoDAO.obtenerTodos();
        productoCombo.setItems(FXCollections.observableArrayList(productos));
        productoCombo.setConverter(new javafx.util.StringConverter<ProductoDTO>() {
            @Override
            public String toString(ProductoDTO p) {
                return p == null ? "" : p.getNombre();
            }

            @Override
            public ProductoDTO fromString(String string) {
                return productoCombo.getItems().stream()
                        .filter(p -> p.getNombre().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });
        if (!productos.isEmpty()) {
            productoCombo.getSelectionModel().selectFirst();
        }
    }

    private void configurarRadios() {
        tipoMovimientoGroup = new ToggleGroup();
        entradaRadio.setToggleGroup(tipoMovimientoGroup);
        salidaRadio.setToggleGroup(tipoMovimientoGroup);
        entradaRadio.setSelected(true);
    }

    private void updateRealizadoPor() {
        if (realizadoPor != null) {
            String usuario = Sesion.getUsuarioSesion() != null ? Sesion.getUsuarioSesion().getUsuario() : "";
            realizadoPor.setText("Ajuste realizado por: " + usuario);
        }
    }

    @FXML
    private void onConfirmar() {
        if (!validar()) {
            return;
        }

        ProductoDTO producto = productoCombo.getValue();
        int cantidad = Integer.parseInt(cantidadField.getText().trim());
        boolean esEntrada = entradaRadio.isSelected();

        int ajuste = esEntrada ? cantidad : -cantidad;
        int stockFinal = producto.getStock() + ajuste;
        if (stockFinal < 0) {
            try {
                WindowServices.showWarningDialog("Validación", "El stock no puede quedar negativo.");
            } catch (java.io.IOException e) {
                LOGGER.error("Error mostrando diálogo de advertencia: {}", e.getMessage(), e);
            }
            return;
        }

        try {
            productoDAO.actualizarStock(producto.getId(), stockFinal);
            LOGGER.info("Ajuste aplicado al producto {}: {} (stock final: {})", producto.getId(), ajuste, stockFinal);
            cerrar();
        } catch (Exception e) {
            LOGGER.error("Error al aplicar ajuste: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo aplicar el ajuste: " + e.getMessage());
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private boolean validar() {
        try {
            if (productoCombo == null || productoCombo.getValue() == null) {
                WindowServices.showWarningDialog("Validación", "Seleccione un producto.");
                return false;
            }
            if (cantidadField == null || cantidadField.getText() == null || cantidadField.getText().isBlank()) {
                WindowServices.showWarningDialog("Validación", "Ingrese la cantidad a ajustar.");
                return false;
            }
            try {
                int val = Integer.parseInt(cantidadField.getText().trim());
                if (val <= 0) {
                    WindowServices.showWarningDialog("Validación", "La cantidad debe ser mayor a cero.");
                    return false;
                }
            } catch (NumberFormatException e) {
                WindowServices.showWarningDialog("Validación", "La cantidad debe ser numérica.");
                return false;
            }
            if (razonArea == null || razonArea.getText() == null || razonArea.getText().isBlank()) {
                WindowServices.showWarningDialog("Validación", "Ingrese la razón del movimiento.");
                return false;
            }
            if (tipoMovimientoGroup == null || tipoMovimientoGroup.getSelectedToggle() == null) {
                WindowServices.showWarningDialog("Validación", "Seleccione el tipo de movimiento.");
                return false;
            }
        } catch (java.io.IOException e) {
            LOGGER.error("Error mostrando diálogo de validación: {}", e.getMessage(), e);
        }
        return true;
    }

    private void deshabilitar() {
        if (productoCombo != null) productoCombo.setDisable(true);
        if (cantidadField != null) cantidadField.setDisable(true);
        if (razonArea != null) razonArea.setDisable(true);
        if (entradaRadio != null) entradaRadio.setDisable(true);
        if (salidaRadio != null) salidaRadio.setDisable(true);
    }

    private void cerrar() {
        if (owner != null) {
            owner.hide();
            return;
        }
        if (realizadoPor != null && realizadoPor.getScene() != null) {
            realizadoPor.getScene().getWindow().hide();
        }
    }
}
