package com.example.tecnostore.logic.servicios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.ClienteDTO;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClienteServiceTest {

    private static ClienteService clienteService;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setup() throws Exception {

        try (Connection conn = new ConexionBD().getConnection();
             Statement st = conn.createStatement()) {

                st.execute("DROP TABLE IF EXISTS clientes");
            st.execute("CREATE TABLE clientes (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(255), email VARCHAR(255), telefono VARCHAR(50), activo BOOLEAN DEFAULT TRUE)");

            String product = conn.getMetaData().getDatabaseProductName().toLowerCase();
            boolean isMySql = product.contains("mysql");
            boolean isH2 = product.contains("h2");

            try {
                if (isMySql) {
                    st.execute("SET FOREIGN_KEY_CHECKS = 0");
                } else if (isH2) {
                    st.execute("SET REFERENTIAL_INTEGRITY FALSE");
                }
            } catch (SQLException ignore) {
                // Si la sentencia no es compatible, continuar
            }

            st.execute("TRUNCATE TABLE clientes");

            try {
                if (isMySql) {
                    st.execute("SET FOREIGN_KEY_CHECKS = 1");
                } else if (isH2) {
                    st.execute("SET REFERENTIAL_INTEGRITY TRUE");
                }
            } catch (SQLException ignore) {
                // Ignorar si no es compatible
            }
            System.out.println("--- Base de datos limpiada para pruebas ---");

        } catch (SQLException e) {
            System.err.println("Error limpiando la base de datos: " + e.getMessage());
        }

        clienteService = new ClienteService();
    }

    @Test
    @Order(1)
    void prueba1_RegistrarClienteNuevo() throws SQLException {
        System.out.println("Prueba 1: Registrar...");
        ClienteDTO c = new ClienteDTO(0, "Cliente de Prueba", "prueba@test.com", "123456", true);
        String resultado = clienteService.registrarCliente(c);
        assertEquals("Cliente registrado exitosamente.", resultado);
    }

    @Test
    @Order(2)
    void prueba2_EvitarDuplicidad() throws SQLException {
        System.out.println("Prueba 2: Evitar duplicados...");
        ClienteDTO c = new ClienteDTO(0, "Otro Cliente", "prueba@test.com", "987654", true);
        String resultado = clienteService.registrarCliente(c);
        assertTrue(resultado.contains("Error: El email"));
    }

    @Test
    @Order(3)
    void prueba3_ConsultarCliente() throws Exception {
        System.out.println("Prueba 3: Consultar...");
        var clientes = clienteService.buscarClientes("Prueba");
        assertFalse(clientes.isEmpty());
        assertEquals("prueba@test.com", clientes.get(0).getEmail());
    }

    @Test
    @Order(4)
    void prueba4_EliminacionLogica() throws Exception {
        System.out.println("Prueba 4: Eliminar lógicamente...");
        int idClientePrueba = clienteService.buscarClientes("prueba@test.com").get(0).getId();

        String resultado = clienteService.eliminarCliente(idClientePrueba);
        assertEquals("Cliente eliminado (lógicamente).", resultado);

        List<ClienteDTO> clientes = clienteService.buscarClientes("prueba@test.com");
        assertTrue(clientes.isEmpty());
    }

    @Test
    @Order(5)
    void prueba5_ValidacionNombreVacio() throws SQLException {
        System.out.println("Prueba 5: Validación de nombre vacío...");
        ClienteDTO c = new ClienteDTO(0, "", "vacio@test.com", "11111", true);
        String resultado = clienteService.registrarCliente(c);
        assertEquals("Error: El nombre no puede estar vacío.", resultado);
    }

    @Test
    @Order(6)
    void prueba6_GenerarReportes() throws Exception {
        System.out.println("--- EJECUTANDO PRUEBA DE REPORTES ---");
        ClienteService servicio = new ClienteService();

        servicio.generarReporteClientesActivos();
        servicio.generarReporteEstadisticas();
        System.out.println("--- FIN DE REPORTES ---");
    }
}