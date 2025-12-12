package com.example.tecnostore.logic.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {
    private int id;
    private int usuarioId;
    private double total;
    private LocalDateTime fecha;
    private String metodoPago;

    // Lista de productos y cantidades de la venta (para la lógica de negocio)
    private List<DetalleVentaDTO> detalles;

    // Constructores, Getters y Setters...

    public VentaDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public List<DetalleVentaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaDTO> detalles) { this.detalles = detalles; }
}