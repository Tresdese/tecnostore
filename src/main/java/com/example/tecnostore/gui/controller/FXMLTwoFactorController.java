/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.TwoFactorService;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author super
 */
public class FXMLTwoFactorController implements Initializable {

    @FXML private TextField txtCodigo;

    private UsuarioDTO user;
    private Runnable on2FAOk;

    private final TwoFactorService twoFactorService = new TwoFactorService();

    public void setUser(UsuarioDTO user) {
        this.user = user;
    }

    public void setOn2FAOk(Runnable on2FAOk) {
        this.on2FAOk = on2FAOk;
    }

    @FXML
    private void onVerificar() {
        try {
            if (user == null || user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isBlank()) {
                WindowServices.showErrorDialog("2FA", "No se encontró la configuración de doble factor para este usuario.");
                cerrarVentana();
                return;
            }

            int code = Integer.parseInt(txtCodigo.getText().trim());
            boolean ok = twoFactorService.verificarCodigo(user.getTwoFactorSecret(), code);

            if (ok) {
                // Cerrar ventana y notificar al login
                cerrarVentana();
                if (on2FAOk != null) on2FAOk.run();
            } else {
                WindowServices.showErrorDialog("2FA", "Código incorrecto. Intenta de nuevo.");
            }
        } catch (NumberFormatException e) {
            WindowServices.showErrorDialog("2FA", "Ingresa un código numérico válido.");
        }
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            if (txtCodigo != null) {
                txtCodigo.setEditable(true);
                txtCodigo.requestFocus();
            }
        });
    }
}
