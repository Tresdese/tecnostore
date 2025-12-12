package com.example.tecnostore.logic.dto;

public class DetalleCompraDTO {
    private int id;
    private int compra_id;
    private int producto_id;
    private int cantidad;
    private double precio_compra;
    private String nombreProducto;
    private double subtotal;

    public DetalleCompraDTO() {
    }

    public DetalleCompraDTO(int id, int compra_id, int producto_id, int cantidad, double precio_compra) {
        this.id = id;
        this.compra_id = compra_id;
        this.producto_id = producto_id;
        this.cantidad = cantidad;
        this.precio_compra = precio_compra;
        this.subtotal = cantidad * precio_compra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompra_id() {
        return compra_id;
    }

    public void setCompra_id(int compra_id) {
        this.compra_id = compra_id;
    }

    public int getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(int producto_id) {
        this.producto_id = producto_id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.cantidad * this.precio_compra;
    }

    public double getPrecio_compra() {
        return precio_compra;
    }

    public void setPrecio_compra(double precio_compra) {
        this.precio_compra = precio_compra;
        this.subtotal = this.cantidad * this.precio_compra;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "DetalleCompraDTO{" +
                "id=" + id +
                ", producto_id=" + producto_id +
                ", cantidad=" + cantidad +
                ", precio=" + precio_compra +
                '}';
    }
}
