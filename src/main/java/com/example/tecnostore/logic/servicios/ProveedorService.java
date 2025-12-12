package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ProveedorService {

    // tus credenciales
    private final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_ventas";
    private final String DB_USER = "root";
    private final String DB_PASS = "4422";

    private static final Pattern PATRON_RFC = Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void registrarProveedor(String nombre, String rfc, String email) throws Exception {
        String rfcLimpio = rfc.trim().toUpperCase();
        String emailLimpio = email.trim().toLowerCase();

        if (!PATRON_RFC.matcher(rfcLimpio).matches()) {
            throw new SecurityException("el rfc tiene caracteres invalidos o sospechosos");
        }
        if (!PATRON_EMAIL.matcher(emailLimpio).matches()) {
            throw new IllegalArgumentException("el correo no tiene formato valido");
        }

        String sql = "INSERT INTO proveedores (nombre_empresa, rfc, email, activo) VALUES (?, ?, ?, true)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, rfcLimpio);
            stmt.setString(3, emailLimpio);
            stmt.executeUpdate();
            System.out.println("proveedor registrado: " + nombre);

        } catch (SQLException e) {
            throw new Exception("error al guardar en bd, revisar logs");
        }
    }

    // --- NUEVO: CUMPLE CON EL REQUISITO DE 'ELIMINACIÓN LÓGICA' ---
    public void darBajaProveedor(String rfc) throws Exception {
        // No borramos (DELETE), solo desactivamos (UPDATE) para mantener historial
        String sql = "UPDATE proveedores SET activo = false WHERE rfc = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rfc);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("proveedor desactivado correctamente (baja logica)");
            } else {
                throw new Exception("proveedor no encontrado");
            }
        } catch (SQLException e) {
            throw new Exception("error de base de datos al dar de baja");
        }
    }
}