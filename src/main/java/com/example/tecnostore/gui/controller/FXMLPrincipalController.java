package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FXMLPrincipalController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(FXMLPrincipalController.class);

    @FXML
    private Button logOutButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button registerButton;
    private Button editButton;
    private Button deleteButton;
    @FXML
    private Button employeeListButton;
    @FXML
    private Button reportesButton;

    private UsuarioDTO usuarioDTO;
    @FXML
    private Button manageProductsButton;

    private WindowServices windowServices = new WindowServices();
    @FXML
    private Button gestionarSucursalesButton;
    @FXML
    private Button registrosAuditoriaButton;
    @FXML
    private Button puntoVentaButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDTO = null;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenido");
        }
        setButtonVisibility(registerButton, false);
        setButtonVisibility(editButton, false);
        setButtonVisibility(deleteButton, false);
        setButtonVisibility(employeeListButton, false);
        setUsuarioDTO(Sesion.getUsuarioSesion());

        System.out.println("Rol en sesión: " + Sesion.getRolActual());
    }

    @FXML
    private void logOutActionButton(ActionEvent event) {
        logOut(event);
    }

    private void logOut(ActionEvent event) {
        // Limpia la sesión antes de regresar a la pantalla de login
        Sesion.cerrarSesion();
        try {
            windowServices.goToLoginWindow(event);
        } catch (IOException e) {
            LOGGER.error("Error al encontrar ventana: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            LOGGER.error("Error al encontrar pointer: {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cerrar sesión: {}", e.getMessage(), e);
        } finally {
            System.out.println("Sesión cerrada. Usuario en sesión: " + Sesion.getUsuarioSesion());
        }
    }

    void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public void setUsuarioDTO(UsuarioDTO usuarioActual) {
        this.usuarioDTO = usuarioActual;
        // Actualiza la UI cuando se establece el usuario
        if (usuarioActual != null) {
            setUsername(usuarioActual);
            // pasar el id y rol al método que actualiza botones
            setUserRole(Sesion.getRolActual());
        }
    }

    private void setUsername(UsuarioDTO usuarioActual) {
        welcomeLabel.setText("Bienvenido, " + usuarioActual.getUsuario());
    }

    private void setUserRole(String usuarioRol) {

        switch (usuarioRol) {
            case "ADMIN": // Admin
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(puntoVentaButton, false); // << AÑADIDO
                break;
            case "CAJERO": // cajero
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, false);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(puntoVentaButton, true); // << AÑADIDO (TRUE)
                break;
            case "SUPERADMINISTRADOR": // superadministrador
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(puntoVentaButton, false); // << AÑADIDO
                break;
            case "GERENTE DE INVENTARIO": // gerente
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(puntoVentaButton, false); // << AÑADIDO
                break;
            default:
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, false);
                setButtonVisibility(reportesButton, false);
                setButtonVisibility(puntoVentaButton, false); // << AÑADIDO
                break;
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        registrarBoton();
    }


    @FXML
    private void handleEmployeeListButton(ActionEvent event) {
        handleManageUsersButton(event);
    }

    @FXML
    private void handleManageProductsButton(ActionEvent event) {
        try {
            windowServices.openModal("FXMLGestionProductos.fxml", "Gestión de productos");
        } catch (Exception e) {
            LOGGER.error("Error al abrir el formulario de gestión de productos: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleManageUsersButton(ActionEvent event) {
        try {
            windowServices.openModal("FXMLGestionUsuariosView.fxml", "Gestión de usuarios");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el formulario de gestión de usuarios: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReportesButton(ActionEvent event) {
        try {
            windowServices.openModal("FXMLReportesView.fxml", "Panel de Reportes");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el panel de reportes: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el panel de reportes: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void registrarBoton() {
        try {
            windowServices.openModal("FXMLUsuarioModal.fxml", "Registro de Usuario");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el formulario de registro: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el formulario de registro: " + e.getMessage());
            alert.showAndWait();
        }
    }//

    private void gestionarSucursales() {
        try {
            windowServices.openModal("FXMLGestionSucursalesView.fxml", "Gestión de Sucursales");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el formulario de gestión de sucursales: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el formulario de gestión de sucursales: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handlegestionarSucursalesButton(ActionEvent event) {
        gestionarSucursales();
    }

    @FXML
    private void handleRegistrosAuditoriaButton(ActionEvent event) {
    }

    @FXML
    private void handlePuntoVentaButton(ActionEvent event) {
        try {
            windowServices.openModal("FXMLPuntoVentaView.fxml", "Punto de Ventas");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el Punto de Venta: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo abrir el Punto de Venta: " + e.getMessage());
        }
    }
}
