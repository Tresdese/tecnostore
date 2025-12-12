package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.ServicioProductos;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // <--- IMPORTANTE: Importar esta interfaz
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

// 1. AGREGAMOS "implements Initializable" PARA QUE SE EJECUTE LA CONFIGURACIÓN DE TABLAS
public class FXMLRegistroVentaModalController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<ProductoDTO> productTable;
    @FXML private Label subtotalLabel;
    @FXML private Label ivaLabel;
    @FXML private Label totalLabel;

    @FXML private TableColumn<ProductoDTO, String> colCodigo;
    @FXML private TableColumn<ProductoDTO, String> colDescripcion;
    @FXML private TableColumn<ProductoDTO, Double> colPrecio;
    @FXML private TableColumn<ProductoDTO, Integer> colStock;
    @FXML private TableColumn<ProductoDTO, Integer> colCantidad;
    @FXML private TableColumn<ProductoDTO, Double> colImporte;
    @FXML private Button btnAumentar;
    @FXML private Button btnDisminuir;
    @FXML private Button btnQuitar;

    private ServicioProductos servicioProductos;

    // Este método ahora SÍ se ejecutará al abrir la ventana porque implementamos la interfaz
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(FXCollections.observableArrayList());

        try {
            servicioProductos = new ServicioProductos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración de columnas (Sin esto, la tabla se ve vacía)
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colPrecio.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getStock()).asObject());
        colCantidad.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidadVenta()).asObject());
        colImporte.setCellValueFactory(cell ->
            new SimpleDoubleProperty(cell.getValue().getPrecio() * cell.getValue().getCantidadVenta()).asObject());

        // Permite disparar búsqueda con Enter
        if (searchField != null) {
            searchField.setOnAction(evt -> onBuscar());
        }

        // Carga inicial del catálogo para que la tabla no aparezca vacía
        cargarCatalogoCompleto();

        // Resaltar productos sin stock
        productTable.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
            @Override
            protected void updateItem(ProductoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getStock() <= 0) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    @FXML
    private void onBuscar() {
        String texto = searchField.getText();

        try {
            if (servicioProductos == null) {
                WindowServices.showErrorDialog("Error", "El servicio de productos no está disponible.");
                return;
            }

            var catalogo = servicioProductos.obtenerTodosProductos();
            if (catalogo == null || catalogo.isEmpty()) {
                WindowServices.showWarningDialog("Aviso", "No hay productos registrados.");
                return;
            }

            // Si no se ingresó texto, agrega todo el catálogo con cantidad 1
            if (texto == null || texto.isBlank()) {
                cargarCatalogoCompleto();
                return;
            }

            ProductoDTO encontrado = catalogo.stream()
                    .filter(p -> String.valueOf(p.getId()).equalsIgnoreCase(texto)
                            || p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (encontrado == null) {
                WindowServices.showWarningDialog("Aviso", "Producto no encontrado.");
                return;
            }

            agregarOIncrementar(encontrado);
            actualizarEtiquetas();
            searchField.clear();

        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo buscar el producto: " + e.getMessage());
        }
    }

    private void cargarCatalogoCompleto() {
        try {
            if (servicioProductos == null) {
                return;
            }
            var catalogo = servicioProductos.obtenerTodosProductos();
            productTable.getItems().clear();
            if (catalogo != null) {
                for (ProductoDTO p : catalogo) {
                    agregarOIncrementar(p, false);
                }
            }
            actualizarEtiquetas();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo cargar el catálogo: " + e.getMessage());
        }
    }

    private void agregarOIncrementar(ProductoDTO base) {
        agregarOIncrementar(base, true);
    }

    private void agregarOIncrementar(ProductoDTO base, boolean incrementar) {
        if (base == null) return;
        ProductoDTO existente = productTable.getItems().stream()
                .filter(p -> p.getId() == base.getId())
                .findFirst()
                .orElse(null);

        if (existente != null) {
            if (incrementar && existente.getCantidadVenta() < existente.getStock()) {
                existente.setCantidadVenta(existente.getCantidadVenta() + 1);
            }
            productTable.refresh();
        } else {
            ProductoDTO item = new ProductoDTO();
            item.setId(base.getId());
            item.setNombre(base.getNombre());
            item.setPrecio(base.getPrecio());
            item.setStock(base.getStock());
            item.setCantidadVenta(incrementar ? 1 : 0);
            productTable.getItems().add(item);
        }
    }

    @FXML
    private void onAumentarCantidad() {
        ProductoDTO seleccionado = productTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        if (seleccionado.getCantidadVenta() < seleccionado.getStock()) {
            seleccionado.setCantidadVenta(seleccionado.getCantidadVenta() + 1);
        }
        productTable.refresh();
        actualizarEtiquetas();
    }

    @FXML
    private void onDisminuirCantidad() {
        ProductoDTO seleccionado = productTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        if (seleccionado.getCantidadVenta() > 0) { 
            seleccionado.setCantidadVenta(seleccionado.getCantidadVenta() - 1);
            productTable.refresh();
            actualizarEtiquetas();
        }
    }

    @FXML
    private void onQuitarProducto() {
        ProductoDTO seleccionado = productTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        seleccionado.setCantidadVenta(0);
        actualizarEtiquetas();
    }

    @FXML
    private void onCancelarVenta() {
        searchField.getScene().getWindow().hide();
    }

    @FXML
    private void onHacerPago() throws IOException {
        boolean hayCantidad = productTable.getItems().stream().anyMatch(p -> p.getCantidadVenta() > 0);
        if (!hayCantidad) {
            WindowServices.showWarningDialog("Vacío", "Agregue productos antes de pagar.");
            return;
        }

        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPagoModal.fxml"));
            Parent root = loader.load();

            FXMLPagoModalController controller = loader.getController();

            // 2. CORREGIDO: Usamos el método que recibe TOTAL + PRODUCTOS (para descontar inventario)
            controller.inicializarPago(obtenerTotal(), productTable.getItems());

            controller.setOwner(owner);

            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Si el pago fue exitoso, limpiamos la tabla
            if (controller.isPagoExitoso()) {
                productTable.getItems().clear();
                actualizarEtiquetas();
            }

        } catch (IOException e) {
            e.printStackTrace();
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    // Método para calcular el total sumando los items de la tabla
    private double obtenerTotal() {
        double suma = 0;
        if (productTable != null) {
            for (ProductoDTO p : productTable.getItems()) {
                suma += p.getPrecio() * p.getCantidadVenta();
            }
        }
        return suma;
    }

    private void actualizarEtiquetas() {
        double total = obtenerTotal(); // Obtiene la suma real
        double subtotal = total / 1.16; // Desglosa IVA
        double iva = total - subtotal;

        if (totalLabel != null) totalLabel.setText(String.format("$%,.2f", total));
        if (subtotalLabel != null) subtotalLabel.setText(String.format("$%,.2f", subtotal));
        if (ivaLabel != null) ivaLabel.setText(String.format("$%,.2f", iva));
    }
}