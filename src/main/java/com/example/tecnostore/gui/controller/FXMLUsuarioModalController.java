package com.example.tecnostore.gui.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.RolDAO;
import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.RolDTO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;
import com.example.tecnostore.logic.utils.PasswordHasher;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class FXMLUsuarioModalController {

    private static final Logger LOGGER = LogManager.getLogger(FXMLUsuarioModalController.class);

    @FXML private TextField nombreField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<RolDTO> rolCombo;
    @FXML private CheckBox activoCheckbox;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    private boolean isNew = true;
    private Window owner;
    private UsuarioDTO usuarioActual;

    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private ServicioDeAutenticacion authService;

    @FXML
    private void initialize() {
        try {
            usuarioDAO = new UsuarioDAO();
            rolDAO = new RolDAO();
            authService = new ServicioDeAutenticacion();
            cargarRoles();
            applyState();
        } catch (Exception e) {
            LOGGER.error("Error inicializando DAOs: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el formulario: " + e.getMessage());
            deshabilitarFormulario();
        }
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
        applyState();
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public void setUsuarioActual(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
        if (usuario != null) {
            isNew = false;
            nombreField.setText(usuario.getUsuario());
            activoCheckbox.setSelected(usuario.isActivo());
            seleccionarRol(usuario.getRol_id());
        }
        applyState();
    }

    private void cargarRoles() throws Exception {
        List<RolDTO> roles = rolDAO.buscarTodos();
        rolCombo.setItems(FXCollections.observableArrayList(roles));
        rolCombo.setConverter(new StringConverter<RolDTO>() {
            @Override
            public String toString(RolDTO rol) {
                return rol == null ? "" : rol.getNombre();
            }

            @Override
            public RolDTO fromString(String string) {
                return rolCombo.getItems().stream()
                        .filter(r -> r.getNombre().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        if (!roles.isEmpty()) {
            rolCombo.getSelectionModel().selectFirst();
        }
    }

    private void seleccionarRol(int rolId) {
        rolCombo.getItems().stream()
                .filter(r -> r.getId() == rolId)
                .findFirst()
                .ifPresent(r -> rolCombo.getSelectionModel().select(r));
    }

    private void applyState() {
        if (rolCombo == null || activoCheckbox == null) {
            return;
        }
        if (isNew) {
            activoCheckbox.setSelected(true);
        }
    }

    @FXML
    private void onGuardar() {
        if (!validarCampos()) {
            return;
        }

        RolDTO rolSeleccionado = rolCombo.getValue();
        String nombreUsuario = nombreField.getText().trim();
        String password = passwordField.getText();
        boolean activo = activoCheckbox.isSelected();

        try {
            if (isNew) {
                UsuarioDTO nuevo = new UsuarioDTO();
                nuevo.setNombre(nombreUsuario);
                nuevo.setUsuario(nombreUsuario); // se usa el mismo valor como username
                nuevo.setContrasenaHash(password);
                nuevo.setRol_id(rolSeleccionado.getId());
                nuevo.setActivo(activo);
                authService.guardarUsuario(nuevo);
            } else if (usuarioActual != null) {
                usuarioActual.setNombre(nombreUsuario);
                usuarioActual.setUsuario(nombreUsuario);
                if (password != null && !password.isBlank()) {
                    usuarioActual.setContrasenaHash(PasswordHasher.hashPassword(password));
                }
                usuarioActual.setRol_id(rolSeleccionado.getId());
                usuarioActual.setActivo(activo);
                usuarioDAO.actualizar(usuarioActual);
            }
            cerrar();
        } catch (Exception e) {
            LOGGER.error("Error al guardar usuario: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

    private boolean validarCampos() {
        boolean respuesta = true;
        
        if (nombreField == null || passwordField == null || rolCombo == null) {
            respuesta = false;
        }
        String nombreUsuario = nombreField.getText();
        try {
            if (nombreUsuario == null || nombreUsuario.isBlank()) {
                WindowServices.showWarningDialog("Validación", "El nombre de usuario es obligatorio.");
                respuesta = false;
            }
            if (rolCombo.getValue() == null) {
                WindowServices.showWarningDialog("Validación", "Debe seleccionar un rol.");
                respuesta = false;
            }
            if (isNew && (passwordField.getText() == null || passwordField.getText().isBlank())) {
                WindowServices.showWarningDialog("Validación", "La contraseña es obligatoria para un usuario nuevo.");
                respuesta = false;
            }
        } catch (Exception e) {
            LOGGER.error("Error mostrando diálogo de validación: {}", e.getMessage(), e);
            respuesta = false;
        }
        return respuesta;
    }

    private void deshabilitarFormulario() {
        if (guardarButton != null) guardarButton.setDisable(true);
        if (cancelarButton != null) cancelarButton.setDisable(true);
    }

    private void cerrar() {
        if (cancelarButton != null && cancelarButton.getScene() != null) {
            cancelarButton.getScene().getWindow().hide();
        }
    }
}
