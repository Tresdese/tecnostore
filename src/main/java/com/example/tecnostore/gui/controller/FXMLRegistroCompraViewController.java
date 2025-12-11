package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FXMLRegistroCompraViewController {
    @FXML private TextField numCompraField;
    @FXML private TableView<?> productsTable;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotal;

    @FXML
    private void initialize() {
        productsTable.setPlaceholder(new Label("No hay productos en la orden de compra"));
    }

    @FXML private void onGuardarBorrador() { /* TODO: implement draft save */ }
    @FXML private void onRegistrarCompra() { /* TODO: implement register purchase */ }
}
