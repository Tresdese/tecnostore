package com.example.tecnostore.data_access;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * SECCIÓN CRÍTICA DE SEGURIDAD:
 * - Aquí se leen y usan las credenciales de la base de datos (URL, usuario, password).
 * - Riesgo: si este archivo o el archivo de configuración ('config.properties') está en el
 *   repositorio o en un artefacto distribuido, las credenciales quedarían expuestas.
 * - Recomendación (no técnica): mover las credenciales fuera del código (variables de entorno
 *   o gestor de secretos) y usar un pool de conexiones para evitar fugas.
 */

public class ConexionBD implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());

    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection connection = null;

    public ConexionBD() throws Exception {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("No se encontró 'config.properties' en el classpath. Colóquelo en src/main/resources/");
            }
            properties.load(input);
            this.URL = properties.getProperty("db.url");
            this.USER = properties.getProperty("db.user");
            this.PASSWORD = properties.getProperty("db.password");

            if (this.URL == null || this.URL.isBlank()) {
                throw new IOException("Propiedad 'db.url' faltante en config.properties");
            }

            try {
                // Abre la conexión usando las credenciales. Atención: no imprimir ni loggear la contraseña.
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "No se pudo establecer la conexión con la base de datos", ex);
                throw new Exception("Error al conectar a la base de datos: " + ex.getMessage(), ex);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error leyendo config.properties", e);
            throw new Exception("Error en BD: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión abierta correctamente.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al obtener/abrir la conexión", ex);
            System.out.println("Error al obtener/abrir la conexión: " + ex.getMessage());
            throw ex;
        }
        return this.connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                LOGGER.info("Conexión cerrada correctamente.");
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión", e);
            }
        }
    }
}
