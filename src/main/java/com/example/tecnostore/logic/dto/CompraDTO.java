package com.example.tecnostore.logic.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CompraDTO {
    // --- Mapeo DIRECTO de la BD ---
    private int id;
    private String proveedor;
    private LocalDateTime fecha;
    private double total;
    private int usuario_id; // Mantener underscore para mapear la BD
    private int sucursal_id;
    // ---------------------------------

    // --- Campos para Lógica de Negocio (No están en la tabla principal) ---
    private String estado;
    private List<DetalleCompraDTO> itemsComprados;
    // ---------------------------------------------------------------------


    public CompraDTO() {
    }

    public CompraDTO(int id, String proveedor, LocalDateTime fecha, double total, int usuario_id, int sucursal_id) {
        this.id = id;
        this.proveedor = proveedor;
        this.fecha = fecha;
        this.total = total;
        this.usuario_id = usuario_id;
        this.sucursal_id = sucursal_id;
    }

    // --- Getters y Setters de la BD ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getUsuario_id() { return usuario_id; }
    public void setUsuario_id(int usuario_id) { this.usuario_id = usuario_id; }

    public int getSucursal_id() { return sucursal_id; }
    public void setSucursal_id(int sucursal_id) { this.sucursal_id = sucursal_id; }

    // --- Getters y Setters de Lógica de Negocio ---

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetalleCompraDTO> getItemsComprados() { return itemsComprados; }
    public void setItemsComprados(List<DetalleCompraDTO> itemsComprados) { this.itemsComprados = itemsComprados; }

    @Override
    public String toString() {
        return "CompraDTO{" +
                "id=" + id +
                ", proveedor='" + proveedor + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", usuario_id=" + usuario_id +
                ", sucursal_id=" + sucursal_id +
                ", estado='" + estado + '\'' + // Incluir estado en el debug
                '}';
    }
}
