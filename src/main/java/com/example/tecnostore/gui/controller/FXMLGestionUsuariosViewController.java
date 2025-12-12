package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.RolDAO;
import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.RolDTO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionUsuariosViewController {
    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiarBusqueda;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnCerrar;

    private static final Logger LOGGER = LogManager.getLogger(FXMLGestionUsuariosViewController.class);
    private static final String VIEW_BASE = "/com/example/tecnostore/gui/views/";

    @FXML private TextField txtBuscarUsuario;
    @FXML private TableView<UsuarioDTO> tblUsuarios;
    @FXML private TableColumn<UsuarioDTO, Integer> colId;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableColumn<UsuarioDTO, String> colCorreo;
    @FXML private TableColumn<UsuarioDTO, String> colRol;
    @FXML private TableColumn<UsuarioDTO, String> colEstado;

    private WindowServices windowServices = new WindowServices();
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private final Map<Integer, String> rolesPorId = new HashMap<>();

    @FXML
    private void initialize() {
        setUserRole(Sesion.getRolActual());
        try {
            usuarioDAO = new UsuarioDAO();
            rolDAO = new RolDAO();
            cargarRoles();
            configurarTabla();
            cargarUsuarios("");
        } catch (Exception e) {
            LOGGER.error("Error inicializando gestión de usuarios: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar la vista: " + e.getMessage());
        }
    }

    private void cargarRoles() throws Exception {
        List<RolDTO> roles = rolDAO.buscarTodos();
        rolesPorId.clear();
        for (RolDTO rol : roles) {
            rolesPorId.put(rol.getId(), rol.getNombre());
        }
    }

    private void configurarTabla() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colCorreo != null) colCorreo.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        if (colRol != null) {
            colRol.setCellValueFactory(cd -> {
                UsuarioDTO u = cd.getValue();
                String rolNombre = rolesPorId.getOrDefault(u.getRol_id(), "");
                return new SimpleStringProperty(rolNombre);
            });
        }
        if (colEstado != null) {
            colEstado.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().isActivo() ? "Activo" : "Inactivo"));
        }
    }

    @FXML private void onBuscar() { cargarUsuarios(textoBusqueda()); }

    @FXML
    private void onLimpiarBusqueda() {
        if (txtBuscarUsuario != null) {
            txtBuscarUsuario.clear();
        }
        cargarUsuarios("");
    }

    @FXML
    private void onAgregar() {
        openUsuarioModal("Crear nuevo usuario", true, null);
    }

    @FXML
    private void onEditar() {
        UsuarioDTO seleccionado = getSeleccionado();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un usuario en la tabla.").showAndWait();
            return;
        }
        openUsuarioModal("Editar usuario", false, seleccionado);
    }

    @FXML
    private void onEliminar() {
        if (getSeleccionado() == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un usuario en la tabla.").showAndWait();
            return;
        }
        openBajaModal();
    }

    @FXML
    private void onCerrar() {
        Stage owner = getOwnerStage();
        if (owner != null) {
            owner.close();
        }
    }

    private void openUsuarioModal(String title, boolean isNew, UsuarioDTO usuario) {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLUsuarioModal.fxml"));
            Parent root = loader.load();
            FXMLUsuarioModalController controller = loader.getController();
            controller.setIsNew(isNew);
            controller.setOwner(owner);
            if (!isNew && usuario != null) {
                controller.setUsuarioActual(usuario);
            }

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            cargarUsuarios(textoBusqueda());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de usuario: " + e.getMessage()).showAndWait();
        }
    }

    private void openBajaModal() {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLBajaUsuarioModal.fxml"));
            Parent root = loader.load();

            FXMLBajaUsuarioModalController controller = loader.getController();
            UsuarioDTO seleccionado = getSeleccionado();

            if (seleccionado != null) {
                controller.setUsuarioActual(seleccionado);
            }

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Dar de baja usuario");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Muestra y espera; la baja se ejecuta dentro del modal
            modalStage.showAndWait();

            // Refrescamos la tabla para reflejar el cambio de estado (Activo -> Inactivo)
            cargarUsuarios(textoBusqueda());

        } catch (Exception e) {
            LOGGER.error("Error al abrir el modal de baja de usuario: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de baja de usuario: " + e.getMessage());
        }
    }

    private void cargarUsuarios(String filtro) {
        try {
            List<UsuarioDTO> usuarios = usuarioDAO.buscarTodos();
            if (filtro != null && !filtro.isBlank()) {
                String f = filtro.toLowerCase();
                usuarios = usuarios.stream()
                        .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(f))
                                  || (u.getUsuario() != null && u.getUsuario().toLowerCase().contains(f)))
                        .collect(Collectors.toList());
            }
            if (tblUsuarios != null) {
                tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            }
        } catch (Exception e) {
            LOGGER.error("Error al cargar usuarios: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private String textoBusqueda() {
        return txtBuscarUsuario != null ? txtBuscarUsuario.getText() : "";
    }

    private UsuarioDTO getSeleccionado() {
        if (tblUsuarios == null) return null;
        return tblUsuarios.getSelectionModel().getSelectedItem();
    }

    private Stage getOwnerStage() {
        if (tblUsuarios == null || tblUsuarios.getScene() == null) {
            return null;
        }
        return (Stage) tblUsuarios.getScene().getWindow();
    }

    private void setUserRole(String usuarioRol) {
        if (usuarioRol.equals("ADMIN")) {
            windowServices.setButtonVisibility(btnAgregar, true);
            windowServices.setButtonVisibility(btnEditar, true);
            windowServices.setButtonVisibility(btnEliminar, true);
        } else if (usuarioRol.equals("CAJERO")) {
            windowServices.setButtonVisibility(btnAgregar, false);
            windowServices.setButtonVisibility(btnEditar, false);
            windowServices.setButtonVisibility(btnEliminar, false);
        } else if (usuarioRol.equals("GERENTE DE INVENTARIO")) {
            windowServices.setButtonVisibility(btnAgregar, false);
            windowServices.setButtonVisibility(btnEditar, false);
            windowServices.setButtonVisibility(btnEliminar, false);
        } else if (usuarioRol.equals("SUPERADMINISTRADOR")) {
            windowServices.setButtonVisibility(btnAgregar, true);
            windowServices.setButtonVisibility(btnEditar, true);
            windowServices.setButtonVisibility(btnEliminar, true);
        }
    }
}
