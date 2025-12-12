package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.ServicioProductos;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionProductosController implements Initializable {

    @FXML
    private TableView<ProductoDTO> productTable;
    @FXML
    private TableColumn<ProductoDTO, Integer> idColumn;
    @FXML
    private TableColumn<ProductoDTO, String> nameColumn;
    @FXML
    private TableColumn<ProductoDTO, Double> priceColumn;
    @FXML
    private TableColumn<ProductoDTO, Integer> stockColumn;
    @FXML
    private TableColumn<ProductoDTO, String> statusColumn;
    @FXML
    private TableColumn<ProductoDTO, String> descriptionColumn;
    @FXML
    private Button insertProductButton;
    @FXML
    private Button editProductButton;
    @FXML
    private Button deleteProductButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUserRole(Sesion.getRolActual());
        configurarTabla();
        cargarInformacion();
    }

    private void setUserRole(String usuarioRol) {
        if (usuarioRol.equals("CAJERO")) {
            setButtonVisibility(editProductButton, false);
            setButtonVisibility(deleteProductButton, false);
            setButtonVisibility(insertProductButton, false);
        } else if (usuarioRol.equals("ADMINISTRADOR")) {
            insertProductButton.setDisable(true);
            editProductButton.setDisable(true);
            deleteProductButton.setDisable(true);
        } else {
            insertProductButton.setDisable(true);
            editProductButton.setDisable(true);
            deleteProductButton.setDisable(true);
        }
    }

    private ObservableList<ProductoDTO> observableListProductos;

    private void configurarTabla(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        statusColumn.setCellValueFactory(cellData -> {
            ProductoDTO producto = cellData.getValue();
            String texto = (producto != null && producto.isActivo()) ? "Activo" : "Inactivo";
            return new SimpleStringProperty(texto);
        });
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }
    
    private void cargarInformacion() {
         try {
            ServicioProductos servicio = new ServicioProductos();
            List<ProductoDTO> productos = servicio.obtenerTodosProductos();
            observableListProductos = FXCollections.observableArrayList();
            if (productos != null && !productos.isEmpty()) {
                observableListProductos.addAll(productos);
            }
            productTable.setItems(observableListProductos);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron cargar los productos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleInsertProductButton(ActionEvent event) {
        try {
            WindowServices ws = new WindowServices();
            ws.openModal("FXMLInsercionProducto.fxml", "Alta de Producto");

            cargarInformacion();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario de inserción");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleEditProductButton(ActionEvent event) {
        ProductoDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Por favor, seleccione un producto para editar.",
                    javafx.scene.control.ButtonType.OK);
            alert.setTitle("Advertencia");
            alert.setHeaderText("No se ha seleccionado ningún producto");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLEdicionProducto.fxml"));
            Parent root = loader.load();

            FXMLEdicionProductoController controller = loader.getController();
            controller.setProducto(selectedProduct);

            Stage modalStage = new Stage();
            modalStage.setTitle("Edición de Producto");
            modalStage.initModality(Modality.WINDOW_MODAL);

            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            cargarInformacion();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario de edición");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteProductButton(ActionEvent event) {
        ProductoDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("No se ha seleccionado ningún producto");
            alert.setContentText("Por favor, seleccione un producto para eliminar.");
            alert.showAndWait();
            return;
        }

        try {
            ServicioProductos servicio = new ServicioProductos();
            servicio.eliminarProducto(selectedProduct);
            observableListProductos.remove(selectedProduct);

            cargarInformacion();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo eliminar el producto");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }
}
