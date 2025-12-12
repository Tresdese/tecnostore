package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.ServicioProductos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLEdicionProductoController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfDescripcion;
    @FXML
    private TextField tfPrecio;
    @FXML
    private TextField tfStock;
    @FXML
    private TextField tfSucursal;
    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
//
    private ProductoDTO producto;

    private String originalNombre;
    private String originalDescripcion;
    private double originalPrecio;
    private int originalStock;
    private int originalSucursal;
    private boolean originalActivo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    comboEstado.getItems().addAll("activo", "inactivo");

    btnCancelar.setOnAction(this::handleCancelar);
    btnGuardar.setOnAction(this::handleGuardar);
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
        if (producto == null) return;

        originalNombre = producto.getNombre();
        originalDescripcion = producto.getDescripcion();
        originalPrecio = producto.getPrecio();
        originalStock = producto.getStock();
        originalSucursal = producto.getSucursal_id();
        originalActivo = producto.isActivo();

        tfNombre.setText(originalNombre);
        tfDescripcion.setText(originalDescripcion);
        tfPrecio.setText(String.valueOf(originalPrecio));
        tfStock.setText(String.valueOf(originalStock));
        tfSucursal.setText(String.valueOf(originalSucursal));
        comboEstado.getSelectionModel().select(originalActivo ? "Activo" : "Inactivo");
    }

    private void handleCancelar(ActionEvent event) {
        btnCancelar.getScene().getWindow().hide();
    }

    private void handleGuardar(ActionEvent event) {
        if (producto == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Producto no cargado.");
            alert.showAndWait();
            return;
        }

        boolean changed = false;

        String newNombre = tfNombre.getText();
        if (newNombre != null && !newNombre.equals(originalNombre)) {
            producto.setNombre(newNombre);
            changed = true;
        }

        String newDescripcion = tfDescripcion.getText();
        if (newDescripcion != null && !newDescripcion.equals(originalDescripcion)) {
            producto.setDescripcion(newDescripcion);
            changed = true;
        }

        if (!tfPrecio.getText().equals(String.valueOf(originalPrecio))) {
            try {
                double p = Double.parseDouble(tfPrecio.getText());
                producto.setPrecio(p);
                changed = true;
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Precio inválido.");
                alert.showAndWait();
                return;
            }
        }

        if (!tfStock.getText().equals(String.valueOf(originalStock))) {
            try {
                int s = Integer.parseInt(tfStock.getText());
                producto.setStock(s);
                changed = true;
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Stock inválido.");
                alert.showAndWait();
                return;
            }
        }

        if (!tfSucursal.getText().equals(String.valueOf(originalSucursal))) {
            try {
                int su = Integer.parseInt(tfSucursal.getText());
                producto.setSucursal_id(su);
                changed = true;
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Sucursal ID inválido.");
                alert.showAndWait();
                return;
            }
        }

        String estado = comboEstado.getSelectionModel().getSelectedItem();
        boolean nuevoActivo = "Activo".equalsIgnoreCase(estado);
        if (nuevoActivo != originalActivo) {
            producto.setActivo(nuevoActivo);
            changed = true;
        }

        if (!changed) {
            Alert info = new Alert(Alert.AlertType.INFORMATION, "No hubo cambios para guardar.");
            info.showAndWait();
            return;
        }

        try {
            ServicioProductos servicio = new ServicioProductos();
            servicio.actualizarProducto(producto);
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            ok.showAndWait();
            
            btnGuardar.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al actualizar producto: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
