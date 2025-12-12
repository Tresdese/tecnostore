package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.TwoFactorService;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(FXMLPrincipalController.class);

    @FXML
    private Button logOutButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button employeeListButton;
    @FXML
    private Button reportesButton;
    @FXML
    private Button twoFactorButton;
    @FXML
    private Button manageProductsButton;
    @FXML
    private Button gestionarSucursalesButton;
    @FXML
    private Button registrosAuditoriaButton;
    @FXML
    private Button puntoVentaButton;

    private UsuarioDTO usuarioDTO;
    private WindowServices windowServices = new WindowServices();
    private UsuarioDAO usuarioDAO;
    private final TwoFactorService twoFactorService = new TwoFactorService();

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
        setButtonVisibility(twoFactorButton, false);
        setButtonVisibility(puntoVentaButton, false);
        setUsuarioDTO(Sesion.getUsuarioSesion());

        System.out.println("Rol en sesión: " + Sesion.getRolActual());

        try {
            usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            LOGGER.error("No se pudo inicializar UsuarioDAO: {}", e.getMessage(), e);
        }
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
            case "ADMIN": // Administrador
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(twoFactorButton, true);
                setButtonVisibility(puntoVentaButton, false);
                setButtonVisibility(gestionarSucursalesButton, true);
                setButtonVisibility(registrosAuditoriaButton, true);
                break;

            case "CAJERO": // Cajero
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, false);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(twoFactorButton, true);
                setButtonVisibility(puntoVentaButton, true);
                setButtonVisibility(gestionarSucursalesButton, false);
                setButtonVisibility(registrosAuditoriaButton, false);
                break;

            case "SUPERADMINISTRADOR": // Superadministrador
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(twoFactorButton, true);
                setButtonVisibility(puntoVentaButton, false);
                setButtonVisibility(gestionarSucursalesButton, true);
                setButtonVisibility(registrosAuditoriaButton, true);
                break;

            case "GERENTE DE INVENTARIO": // Gerente
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                setButtonVisibility(reportesButton, true);
                setButtonVisibility(twoFactorButton, true);
                setButtonVisibility(puntoVentaButton, false);
                setButtonVisibility(gestionarSucursalesButton, true);
                setButtonVisibility(registrosAuditoriaButton, true);
                break;

            default: // Cualquier otro rol o usuario no logueado
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, false);
                setButtonVisibility(reportesButton, false);
                setButtonVisibility(twoFactorButton, false);
                setButtonVisibility(puntoVentaButton, false);
                setButtonVisibility(gestionarSucursalesButton, false);
                setButtonVisibility(registrosAuditoriaButton, false);
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
        abrirRegistrosAuditoria();
    }

    private void abrirRegistrosAuditoria() {
        try {
            windowServices.openModal("FXMLAuditoriaView.fxml", "Registros de Auditoría");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el formulario de registros de auditoría: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el formulario de registros de auditoría: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleActivar2FAButton(ActionEvent event) {
        UsuarioDTO usuario = Sesion.getUsuarioSesion();
        if (usuario == null) {
            WindowServices.showErrorDialog("2FA", "No hay usuario en sesión.");
            return;
        }

        try {
            asegurarDAO();
            asegurarSecret(usuario);

            String otpauth = twoFactorService.generarOtpAuthUrl(usuario.getUsuario(), "TecnoStore", usuario.getTwoFactorSecret());
            String qrUrl = construirQrUrl(otpauth);

            abrirModalConfiguracion2FA(usuario, qrUrl);
        } catch (Exception e) {
            WindowServices.showErrorDialog("2FA", "No se pudo iniciar la configuración: " + e.getMessage());
            LOGGER.error("Error al configurar 2FA: {}", e.getMessage(), e);
        }
    }

    private void abrirModalConfiguracion2FA(UsuarioDTO usuario, String qrUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLConfigurar2FA.fxml"));
            Parent root = loader.load();
            FXMLConfigurar2FAController controller = loader.getController();
            controller.setData(qrUrl, usuario.getTwoFactorSecret(), () -> abrirVerificacion2FA(usuario));

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Configurar 2FA");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (Exception e) {
            WindowServices.showErrorDialog("2FA", "No se pudo abrir la configuración: " + e.getMessage());
            LOGGER.error("Error al abrir modal de configuración 2FA: {}", e.getMessage(), e);
        }
    }

    private void abrirVerificacion2FA(UsuarioDTO usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLTwoFactor.fxml"));
            Parent root = loader.load();
            FXMLTwoFactorController controller = loader.getController();
            controller.setUser(usuario);
            controller.setOn2FAOk(() -> {
                try {
                    usuario.setTwoFactorEnabled(true);
                    asegurarDAO();
                    usuarioDAO.actualizar(usuario);
                    Sesion.setUsuarioSesion(usuario);
                    WindowServices.showInformationDialog("2FA", "Código verificado. 2FA activado.");
                } catch (Exception e) {
                    WindowServices.showErrorDialog("2FA", "Se verificó el código, pero no se pudo activar en la cuenta: " + e.getMessage());
                    LOGGER.error("Error al activar 2FA tras verificación: {}", e.getMessage(), e);
                }
            });

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Verificar 2FA");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (Exception e) {
            WindowServices.showErrorDialog("2FA", "No se pudo abrir la verificación: " + e.getMessage());
            LOGGER.error("Error al abrir verificación 2FA: {}", e.getMessage(), e);
        }
    }

    private void asegurarSecret(UsuarioDTO usuario) throws Exception {
        if (usuario.getTwoFactorSecret() != null && !usuario.getTwoFactorSecret().isBlank()) {
            return;
        }
        var key = twoFactorService.generarSecretKey();
        usuario.setTwoFactorSecret(key.getKey());
        usuario.setTwoFactorEnabled(false);
        asegurarDAO();
        usuarioDAO.actualizar(usuario);
        Sesion.setUsuarioSesion(usuario);
    }

    private String construirQrUrl(String otpauthUrl) {
        String encoded = URLEncoder.encode(otpauthUrl, StandardCharsets.UTF_8);
        return "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=" + encoded + "&chld=L|0";
    }

    private void asegurarDAO() throws Exception {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO();
        }
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
