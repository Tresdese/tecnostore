package com.example.tecnostore.logic.servicios;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.utils.Sesion;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ReporteSeguridadService {

    private static final Logger LOGGER = LogManager.getLogger(ReporteSeguridadService.class);

    public void generarReporteVentas(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        try (ConexionBD bd = new ConexionBD();
             Connection conn = bd.getConnection();
             FileOutputStream fos = new FileOutputStream(ruta)) {

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                agregarEncabezado(documento, "Reporte de Ventas - TecnoStore");
                documento.add(new Paragraph("\nTransacciones Registradas (Datos enmascarados):\n\n"));

                String sql = "SELECT usuario, monto_total FROM ventas";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String usuario = rs.getString("usuario");
                        double monto = rs.getDouble("monto_total");
                        String usuarioOculto = (usuario != null && usuario.length() > 2)
                                ? usuario.substring(0, 2) + "****" : "***";
                        String alerta = (monto > 10000) ? " [ALERTA: MONTO ATIPICO]" : "";
                        documento.add(new Paragraph("User: " + usuarioOculto + " | Monto: $" + monto + alerta));
                    }
                }

                documento.add(new Paragraph("\n\nGráfica de rendimiento semanal:\n"));
                insertarGrafica(documento);
            } finally {
                documento.close();
            }

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de ventas: {}", e.getMessage(), e);
        }
    }

    public void generarReporteInventario(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        try (ConexionBD bd = new ConexionBD();
             Connection conn = bd.getConnection();
             FileOutputStream fos = new FileOutputStream(ruta)) {

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                agregarEncabezado(documento, "Reporte de Inventario General");
                documento.add(new Paragraph("\nEstado actual del almacén:\n\n"));

                PdfPTable tabla = new PdfPTable(3);
                tabla.addCell("Producto");
                tabla.addCell("Stock");
                tabla.addCell("Estado");

                String sql = "SELECT nombre, stock FROM productos ORDER BY nombre";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    boolean hayDatos = false;
                    while (rs.next()) {
                        hayDatos = true;
                        String nombre = rs.getString("nombre");
                        int stock = rs.getInt("stock");
                        tabla.addCell(nombre != null ? nombre : "-");
                        tabla.addCell(String.valueOf(stock));
                        tabla.addCell(clasificarStock(stock));
                    }

                    if (!hayDatos) {
                        tabla.addCell("(sin registros)");
                        tabla.addCell("-");
                        tabla.addCell("-");
                    }
                }

                documento.add(tabla);
                documento.add(new Paragraph("\nNota: Los ítems en estado CRITICO requieren reorden inmediata."));
            } finally {
                documento.close();
            }

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de inventario: {}", e.getMessage(), e);
        }
    }

    public void generarReporteKPIs(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                agregarEncabezado(documento, "Tablero de KPIs (Indicadores Clave)");
                documento.add(new Paragraph("\nResumen Ejecutivo del Mes:\n\n"));

                Font fontGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);

                documento.add(new Paragraph("Ticket Promedio de Venta:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph("$ 3,450.00 MXN\n", fontGrande));

                documento.add(new Paragraph("Producto Más Vendido:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph("Laptop Dell Inspiron (15 unidades)\n", fontGrande));

                documento.add(new Paragraph("Tasa de Devoluciones:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph("1.5% (Óptimo)\n", fontGrande));
            } finally {
                documento.close();
            }

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de KPIs: {}", e.getMessage(), e);
        }
    }

    private void validarPermisos(String rol) {
        String rolEfectivo = rol != null ? rol : Sesion.getRolActual();
        if (!"ADMIN".equalsIgnoreCase(rolEfectivo)) {
            throw new SecurityException("ACCESO DENEGADO: El usuario no tiene permisos de Administrador.");
        }
    }

    private void agregarEncabezado(Document doc, String titulo) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        doc.add(new Paragraph(titulo, fontTitulo));
        doc.add(new Paragraph("Generado por: Sistema ERP Seguro | Fecha: " + new Date()));
        doc.add(new Paragraph("----------------------------------------------------------------"));
    }

    private void insertarGrafica(Document doc) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1000, "Ventas", "Lunes");
        dataset.addValue(1500, "Ventas", "Martes");
        dataset.addValue(800, "Ventas", "Miercoles");
        JFreeChart chart = ChartFactory.createBarChart("", "Dia", "Monto", dataset, PlotOrientation.VERTICAL, false, true, false);
        Path chartPath = Files.createTempFile("reporte_seguridad_chart", ".png");
        try {
            ChartUtils.saveChartAsPNG(chartPath.toFile(), chart, 500, 300);
            Image img = Image.getInstance(chartPath.toString());
            doc.add(img);
        } finally {
            Files.deleteIfExists(chartPath);
        }

    }

    private String clasificarStock(int stock) {
        if (stock <= 0) return "CRITICO";
        if (stock <= 3) return "BAJO STOCK";
        if (stock >= 80) return "EXCESO";
        return "NORMAL";
    }
}