package com.example.tecnostore.logic.servicios;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dto.LogAuditoriaDTO;
import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.dto.VentaResumenDTO;
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

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final LogDAO logDAO;

    public ReporteSeguridadService() throws Exception {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.logDAO = new LogDAO();
    }

    public List<Path> generarReportesAutomaticos(String directorioSalida, String rolUsuario) {
        validarPermisos(rolUsuario);

        Path carpetaReportes = prepararDirectorioSalida(directorioSalida);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        List<Path> rutas = new ArrayList<>();
        rutas.add(generarReporteVentas(carpetaReportes.resolve("reporte_ventas_" + timestamp + ".pdf").toString(), rolUsuario));
        rutas.add(generarReporteInventario(carpetaReportes.resolve("reporte_inventario_" + timestamp + ".pdf").toString(), rolUsuario));
        rutas.add(generarReporteKPIs(carpetaReportes.resolve("reporte_kpis_" + timestamp + ".pdf").toString(), rolUsuario));

        return rutas;
    }

    public Path generarReporteVentas(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Path rutaArchivo = Paths.get(ruta);

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile())) {
            List<VentaResumenDTO> ventas = ventaDAO.obtenerVentas();

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                agregarEncabezado(documento, "Reporte de Ventas - TecnoStore");
                documento.add(new Paragraph("\nTransacciones Registradas (Datos enmascarados):\n\n"));

                if (ventas.isEmpty()) {
                    documento.add(new Paragraph("No hay ventas registradas."));
                } else {
                    ventas.forEach(v -> {
                        String usuarioOculto = enmascararUsuario(v.getUsuario());
                        String alerta = (v.getMontoTotal() > 10000) ? " [ALERTA: MONTO ATIPICO]" : "";
                        documento.add(new Paragraph("User: " + usuarioOculto + " | Monto: $" + v.getMontoTotal() + alerta));
                    });
                }

                documento.add(new Paragraph("\n\nGráfica de ventas por usuario:\n"));
                insertarGrafica(documento, construirDatasetVentas(ventas));
            } finally {
                documento.close();
            }

            registrarLogReporte("VENTAS", rutaArchivo);

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de ventas: {}", e.getMessage(), e);
        }

        return rutaArchivo;
    }

    public Path generarReporteInventario(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Path rutaArchivo = Paths.get(ruta);

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile())) {
            List<ProductoDTO> productos = productoDAO.obtenerTodos();

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

                if (productos.isEmpty()) {
                    tabla.addCell("(sin registros)");
                    tabla.addCell("-");
                    tabla.addCell("-");
                } else {
                    productos.forEach(prod -> {
                        tabla.addCell(prod.getNombre() != null ? prod.getNombre() : "-");
                        tabla.addCell(String.valueOf(prod.getStock()));
                        tabla.addCell(clasificarStock(prod.getStock()));
                    });
                }

                documento.add(tabla);
                documento.add(new Paragraph("\nNota: Los ítems en estado CRITICO requieren reorden inmediata."));
            } finally {
                documento.close();
            }

            registrarLogReporte("INVENTARIO", rutaArchivo);

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de inventario: {}", e.getMessage(), e);
        }

        return rutaArchivo;
    }

    public Path generarReporteKPIs(String ruta, String rolUsuario) {
        validarPermisos(rolUsuario);

        Path rutaArchivo = Paths.get(ruta);

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile())) {
            List<VentaResumenDTO> ventas = ventaDAO.obtenerVentas();

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                agregarEncabezado(documento, "Tablero de KPIs (Indicadores Clave)");
                documento.add(new Paragraph("\nResumen Ejecutivo del Mes:\n\n"));

                Font fontGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);

                double totalVentas = ventas.stream().mapToDouble(VentaResumenDTO::getMontoTotal).sum();
                double ticketPromedio = ventas.isEmpty() ? 0 : totalVentas / ventas.size();
                String vendedorTop = ventas.stream()
                        .collect(Collectors.groupingBy(v -> v.getUsuario() != null ? v.getUsuario() : "(sin usuario)", Collectors.summingDouble(VentaResumenDTO::getMontoTotal)))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("Sin datos");

                documento.add(new Paragraph("Ticket Promedio de Venta:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph(String.format("$ %.2f MXN\n", ticketPromedio), fontGrande));

                documento.add(new Paragraph("Vendedor con mayor monto acumulado:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph(vendedorTop + "\n", fontGrande));

                documento.add(new Paragraph("Total de ventas registradas:", FontFactory.getFont(FontFactory.HELVETICA, 14)));
                documento.add(new Paragraph(String.format("$ %.2f MXN\n", totalVentas), fontGrande));
            } finally {
                documento.close();
            }

            registrarLogReporte("KPIS", rutaArchivo);

        } catch (Exception e) {
            if (e instanceof SecurityException se) throw se;
            LOGGER.error("Error al generar reporte de KPIs: {}", e.getMessage(), e);
        }

        return rutaArchivo;
    }

    private void validarPermisos(String rol) {
        String rolEfectivo = rol != null ? rol : Sesion.getRolActual();
        if (!"ADMIN".equalsIgnoreCase(rolEfectivo)) {
            throw new SecurityException("ACCESO DENEGADO: El usuario no tiene permisos de Administrador.");
        }
    }

    public List<VentaResumenDTO> obtenerVentasParaUI() {
        try {
            return ventaDAO.obtenerVentas();
        } catch (Exception e) {
            LOGGER.error("No se pudieron obtener ventas para UI: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void agregarEncabezado(Document doc, String titulo) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        doc.add(new Paragraph(titulo, fontTitulo));
        doc.add(new Paragraph("Generado por: Sistema ERP Seguro | Fecha: " + new Date()));
        doc.add(new Paragraph("----------------------------------------------------------------"));
    }

    private void insertarGrafica(Document doc, DefaultCategoryDataset dataset) throws Exception {
        if (dataset.getColumnCount() == 0) {
            doc.add(new Paragraph("Sin datos suficientes para graficar."));
            return;
        }

        JFreeChart chart = ChartFactory.createBarChart("", "Usuario", "Monto", dataset, PlotOrientation.VERTICAL, false, true, false);
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

    private DefaultCategoryDataset construirDatasetVentas(List<VentaResumenDTO> ventas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ventas.stream()
                .collect(Collectors.groupingBy(v -> enmascararUsuario(v.getUsuario()), Collectors.summingDouble(VentaResumenDTO::getMontoTotal)))
                .forEach((usuario, monto) -> dataset.addValue(monto, "Ventas", usuario));
        return dataset;
    }

    private String enmascararUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return "***";
        }
        return usuario.length() > 2 ? usuario.substring(0, 2) + "****" : usuario + "***";
    }

    private Path prepararDirectorioSalida(String rutaBase) {
        Path base = rutaBase != null && !rutaBase.isBlank()
                ? Paths.get(rutaBase)
                : Paths.get("logs", "reportes");
        try {
            Files.createDirectories(base);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo preparar el directorio de reportes: " + e.getMessage(), e);
        }
        return base;
    }

    private void registrarLogReporte(String tipo, Path ruta) {
        try {
            LogAuditoriaDTO dto = new LogAuditoriaDTO();
            dto.setUsuarioId(Sesion.getUsuarioSesion() != null ? Sesion.getUsuarioSesion().getId() : null);
            dto.setAccion("REPORTE_" + tipo.toUpperCase());
            dto.setDescripcion("Generado en " + ruta.toAbsolutePath());
            logDAO.registrar(dto);
        } catch (Exception e) {
            LOGGER.warn("No se pudo registrar log para reporte {}: {}", tipo, e.getMessage());
        }
    }
}