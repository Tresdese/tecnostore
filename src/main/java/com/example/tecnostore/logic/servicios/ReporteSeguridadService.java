package com.example.tecnostore.logic.servicios;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtils;

import java.io.FileOutputStream;
import java.io.File;
import java.sql.*;
import java.awt.Color;

public class ReporteSeguridadService {
    private final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_ventas";
    private final String DB_USER = "root";
    private final String DB_PASS = "4422";

    public void generarReporteVentas(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            agregarEncabezado(documento, "Reporte de Ventas - TecnoStore");
            documento.add(new Paragraph("\nTransacciones Registradas (Seguridad: Datos Enmascarados):\n\n"));

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT usuario, monto_total FROM ventas";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String usuario = rs.getString("usuario");
                double monto = rs.getDouble("monto_total");
                String usuarioOculto = (usuario.length() > 2) ? usuario.substring(0, 2) + "****" : "***";
                String alerta = (monto > 10000) ? " [ALERTA: MONTO ATIPICO]" : "";
                documento.add(new Paragraph("User: " + usuarioOculto + " | Monto: $" + monto + alerta));
            }

            documento.add(new Paragraph("\n\nGrafica de Rendimiento Semanal:\n"));
            insertarGrafica(documento);

            documento.close();
            conn.close();
        } catch (Exception e) {
            if (e instanceof SecurityException) throw (SecurityException) e;
            e.printStackTrace();
        }
    }

    public void generarReporteInventario(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            agregarEncabezado(documento, "Reporte de Inventario General");
            documento.add(new Paragraph("\nEstado actual del almacen:\n\n"));

            PdfPTable tabla = new PdfPTable(3);
            tabla.addCell("Producto");
            tabla.addCell("Stock");
            tabla.addCell("Estado");

            Object[][] datos = {
                    {"Laptop Dell Inspiron", "5", "BAJO STOCK"},
                    {"Mouse Logitech", "45", "Normal"},
                    {"Monitor Samsung 24", "12", "Normal"},
                    {"Teclado Mecanico", "2", "CRITICO"},
                    {"Cable HDMI 2m", "100", "Exceso"}
            };

            for (Object[] fila : datos) {
                tabla.addCell(fila[0].toString());
                tabla.addCell(fila[1].toString());
                tabla.addCell(fila[2].toString());
            }
            documento.add(tabla);
            documento.add(new Paragraph("\nNota: Los items en estado CRITICO requieren reorden inmediata."));

            documento.close();
        } catch (Exception e) {
            if (e instanceof SecurityException) throw (SecurityException) e;
            e.printStackTrace();
        }
    }

    public void generarReporteKPIs(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            agregarEncabezado(documento, "Tablero de KPIs (Indicadores Clave)");
            documento.add(new Paragraph("\nResumen Ejecutivo del Mes:\n\n"));

            Font fontGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);

            documento.add(new Paragraph("Ticket Promedio de Venta:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
            documento.add(new Paragraph("$ 3,450.00 MXN\n", fontGrande));

            documento.add(new Paragraph("Producto Mas Vendido:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
            documento.add(new Paragraph("Laptop Dell Inspiron (15 unidades)\n", fontGrande));

            documento.add(new Paragraph("Tasa de Devoluciones:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
            documento.add(new Paragraph("1.5% (Optimo)\n", fontGrande));

            documento.close();
        } catch (Exception e) {
            if (e instanceof SecurityException) throw (SecurityException) e;
            e.printStackTrace();
        }
    }

    private void validarPermisos(String rol) {
        if (!"ADMIN".equals(rol)) {
            throw new SecurityException("ACCESO DENEGADO: El usuario no tiene permisos de Administrador.");
        }
    }

    private void agregarEncabezado(Document doc, String titulo) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        doc.add(new Paragraph(titulo, fontTitulo));
        doc.add(new Paragraph("Generado por: Sistema ERP Seguro | Fecha: " + new java.util.Date()));
        doc.add(new Paragraph("----------------------------------------------------------------"));
    }

    private void insertarGrafica(Document doc) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1000, "Ventas", "Lunes");
        dataset.addValue(1500, "Ventas", "Martes");
        dataset.addValue(800, "Ventas", "Miercoles");
        JFreeChart chart = ChartFactory.createBarChart("", "Dia", "Monto", dataset, PlotOrientation.VERTICAL, false, true, false);
        File chartFile = new File("temp_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 500, 300);
        Image img = Image.getInstance("temp_chart.png");
        doc.add(img);
    }
}