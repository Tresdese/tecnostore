package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class FXMLSucursalModalController {
    @FXML private TextField nombreField;
    @FXML private TextField direccionField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> estadoCombo;

    private boolean isNew = true;
    private Window owner;
    private SucursalData sucursalActual;

    public void setIsNew(boolean isNew) { this.isNew = isNew; }
    public void setOwner(Window owner) { this.owner = owner; }

    /** Permite precargar datos cuando se edita. */
    public void setSucursalActual(SucursalData sucursal) {
        this.sucursalActual = sucursal;
        if (sucursal != null) {
            isNew = false;
            if (nombreField != null) nombreField.setText(sucursal.getNombre());
            if (direccionField != null) direccionField.setText(sucursal.getDireccion());
            if (telefonoField != null) telefonoField.setText(sucursal.getTelefono());
            if (estadoCombo != null) estadoCombo.setValue(sucursal.getEstado());
        }
    }

    @FXML
    private void initialize() {
        estadoCombo.getItems().setAll("Activo", "Inactivo");
        if (isNew) estadoCombo.setValue("Activo");
        if (sucursalActual != null) {
            setSucursalActual(sucursalActual);
        }
    }//

    @FXML
    private void onConfirmar() {
        if (!validarCampos()) {
            return;
        }

        SucursalData data = sucursalActual == null ? new SucursalData() : sucursalActual;
        data.setNombre(nombreField.getText().trim());
        data.setDireccion(direccionField.getText().trim());
        data.setTelefono(telefonoField.getText().trim());
        data.setEstado(estadoCombo.getValue());
        sucursalActual = data;

        cerrar();
    }

    @FXML private void onCancelar() { cerrar(); }

    private boolean validarCampos() {
        try {
            if (nombreField == null || direccionField == null || telefonoField == null || estadoCombo == null) {
                WindowServices.showErrorDialog("Error", "El formulario no se inicializó correctamente.");
                return false;
            }
            if (nombreField.getText() == null || nombreField.getText().isBlank()) {
                WindowServices.showWarningDialog("Validación", "El nombre es obligatorio.");
                return false;
            }
            if (direccionField.getText() == null || direccionField.getText().isBlank()) {
                WindowServices.showWarningDialog("Validación", "La dirección es obligatoria.");
                return false;
            }
            if (estadoCombo.getValue() == null) {
                WindowServices.showWarningDialog("Validación", "Seleccione el estado.");
                return false;
            }
            String tel = telefonoField.getText();
            if (tel != null && !tel.isBlank() && !tel.matches("[0-9+\\-\\s]{7,20}")) {
                WindowServices.showWarningDialog("Validación", "Teléfono inválido. Use solo dígitos, +, - o espacios.");
                return false;
            }
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void cerrar() {
        if (nombreField != null && nombreField.getScene() != null) {
            nombreField.getScene().getWindow().hide();
        }
    }

    /** DTO simple para transportar datos del modal. */
    public static class SucursalData {
        private String nombre;
        private String direccion;
        private String telefono;
        private String estado;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }

    public SucursalData getSucursalActual() {
        return sucursalActual;
    }
}
