package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.dao.ProductoDAO; // Necesario para la búsqueda
import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.VentaService;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FXMLRegistroVentaModalController {

    @FXML private TextField searchField;
    @FXML private TextField quantityField;
    @FXML private TableView<DetalleVentaDTO> detailsTable;
    @FXML private TableColumn<DetalleVentaDTO, String> colNombreProducto; // Asumido
    @FXML private TableColumn<DetalleVentaDTO, Integer> colCantidad;
    @FXML private TableColumn<DetalleVentaDTO, Double> colPrecio;
    @FXML private TableColumn<DetalleVentaDTO, Double> colSubtotal;
    @FXML private Label lblTotal;

    private ObservableList<DetalleVentaDTO> ventaItems;
    private double totalVenta = 0.0;

    private VentaService ventaService;
    private ProductoDAO productoDAO; // Usamos el DAO directamente para la búsqueda simple

    @FXML
    private void initialize() {
        ventaItems = FXCollections.observableArrayList();
        detailsTable.setItems(ventaItems);
        configurarColumnas();

        try {
            this.ventaService = new VentaService();
            this.productoDAO = new ProductoDAO();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo inicializar los servicios: " + e.getMessage());
        }
    }

    private void configurarColumnas() {
        // Configuraciones estándar de columnas
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colSubtotal.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(
                cellData.getValue().getCantidad() * cellData.getValue().getPrecioVenta()).asObject()
        );
    }

    private double obtenerTotal() {
        return totalVenta;
    }

    /**
     * Simula buscar el producto por ID o nombre, usando la lista completa del DAO.
     * Esto es menos eficiente que una búsqueda SQL directa, pero cumple con las restricciones de métodos.
     */
    private ProductoDTO buscarProductoLocal(String input) throws Exception {
        // Se asume que la entrada puede ser ID (número) o Nombre
        List<ProductoDTO> todos = productoDAO.obtenerTodos();

        // 1. Intentar por ID
        try {
            int id = Integer.parseInt(input.trim());
            Optional<ProductoDTO> found = todos.stream().filter(p -> p.getId() == id).findFirst();
            if (found.isPresent()) return found.get();
        } catch (NumberFormatException e) {
            // Si no es un número, intentar por nombre.
        }

        // 2. Intentar por Nombre
        return todos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(input.trim()))
                .findFirst()
                .orElse(null);
    }


    // *** LÓGICA DE AÑADIR PRODUCTO ***
    @FXML
    private void onAddProduct() throws IOException {
        String input = searchField.getText();
        String qtyInput = quantityField.getText();

        if (input == null || input.isBlank() || qtyInput == null || qtyInput.isBlank()) {
            WindowServices.showWarningDialog("Validación", "Debe ingresar el producto y la cantidad.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(qtyInput.trim());
            if (cantidad <= 0) {
                WindowServices.showWarningDialog("Validación", "La cantidad debe ser mayor a cero.");
                return;
            }

            ProductoDTO producto = buscarProductoLocal(input);

            if (producto == null || !producto.isActivo()) {
                WindowServices.showWarningDialog("Error", "Producto no encontrado o inactivo.");
                return;
            }
            if (producto.getStock() < cantidad) {
                WindowServices.showWarningDialog("Error", "Stock insuficiente. Disponibles: " + producto.getStock());
                return;
            }

            DetalleVentaDTO nuevoDetalle = new DetalleVentaDTO();
            nuevoDetalle.setProductoId(producto.getId());
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPrecioVenta(producto.getPrecio());
            // *** AJUSTE FINAL: ASIGNAR NOMBRE DEL PRODUCTO PARA LA TABLA ***
            nuevoDetalle.setNombreProducto(producto.getNombre());

            ventaItems.add(nuevoDetalle);
            calcularTotal();
            searchField.clear();
            quantityField.clear();

        } catch (NumberFormatException e) {
            WindowServices.showWarningDialog("Validación", "La cantidad ingresada no es un número válido.");
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Error al buscar el producto: " + e.getMessage());
        }
    }

    private void calcularTotal() {
        totalVenta = ventaItems.stream()
                .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioVenta())
                .sum();
        lblTotal.setText(String.format("$%,.2f", totalVenta));
    }

    // *** LÓGICA DE PAGO (Apertura de Modal) ***
    @FXML
    private void onHacerPago() throws IOException {
        if (ventaItems.isEmpty()) {
            WindowServices.showWarningDialog("Venta vacía", "Debe agregar productos para realizar el pago.");
            return;
        }

        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            final String PATH_TO_PAGO_MODAL = "../views/FXMLPagoModal.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_TO_PAGO_MODAL));
            Parent root = loader.load();

            FXMLPagoModalController controller = loader.getController();

            // 1. CORRECCIÓN: Transferencia de datos de venta
            controller.setTotalPagar(obtenerTotal());
            controller.setDetallesVenta(new ArrayList<>(ventaItems)); // << MÉTODO AÑADIDO Y USADO

            controller.setOwner(owner);

            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Finalizar Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    @FXML private void onRemoveProduct() throws IOException {
        DetalleVentaDTO selectedItem = detailsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            ventaItems.remove(selectedItem);
            calcularTotal();
        } else {
            WindowServices.showWarningDialog("Selección", "Debe seleccionar un producto para eliminar de la lista.");
        }
    }

    @FXML private void onCancelarVenta() throws IOException {
        ventaItems.clear();
        calcularTotal();
        WindowServices.showInformationDialog("Venta Cancelada", "La lista de productos ha sido borrada.");
    }

    // Este método debe ser usado por FXMLPagoModalController para cerrar la ventana de registro de venta
    public void cerrar() {
        if (lblTotal.getScene() != null) {
            lblTotal.getScene().getWindow().hide();
        }
    }
}