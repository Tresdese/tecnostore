package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.ServicioProductos;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

public class FXMLInsercionProductoController implements Initializable {
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
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboEstado.getItems().addAll("activo", "inactivo");
        comboEstado.getSelectionModel().select("activo");

        btnCancelar.setOnAction(this::handleCancelar);
        btnGuardar.setOnAction(this::handleGuardar);
    }    

    private void handleCancelar(ActionEvent event) {
        btnCancelar.getScene().getWindow().hide();
    }

    private void handleGuardar(ActionEvent event) {
        String nombre = tfNombre.getText();
        String descripcion = tfDescripcion.getText();
        String precioText = tfPrecio.getText();
        String stockText = tfStock.getText();
        String sucursalText = tfSucursal.getText();
        String estado = comboEstado.getSelectionModel().getSelectedItem();

        if (nombre == null || nombre.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "El nombre es obligatorio").showAndWait();
            return;
        }

        double precio;
        int stock;
        int sucursalId;
        try {
            precio = Double.parseDouble(precioText);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Precio inválido").showAndWait();
            return;
        }
        try {
            stock = Integer.parseInt(stockText);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Stock inválido").showAndWait();
            return;
        }
        try {
            sucursalId = Integer.parseInt(sucursalText);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Sucursal ID inválido").showAndWait();
            return;
        }

        boolean activo = "activo".equalsIgnoreCase(estado);

        ProductoDTO producto = new ProductoDTO();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setSucursal_id(sucursalId);
        producto.setActivo(activo);

        try {
            ServicioProductos servicio = new ServicioProductos();
            servicio.insertarProducto(producto);
            new Alert(Alert.AlertType.INFORMATION, "Producto agregado correctamente").showAndWait();
            btnGuardar.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al agregar producto: " + e.getMessage()).showAndWait();
        }
    }
}
