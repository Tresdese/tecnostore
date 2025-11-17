package com.example.tecnostore.logic.dao;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.ClienteDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDAOTest {

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = new ConexionBD().getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS clientes");
            st.execute("CREATE TABLE clientes (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(255), email VARCHAR(255), telefono VARCHAR(50), activo BOOLEAN DEFAULT TRUE)");
        }
    }

    @Test
    void testRegistrarYConsultar() throws Exception {
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Juan");
        dto.setEmail("juan@example.com");
        dto.setTelefono("123456789");
        dto.setActivo(true);

        boolean ok = dao.registrarCliente(dto);
        assertTrue(ok);
        assertTrue(dto.getId() > 0);

        List<ClienteDTO> lista = dao.consultarClientes("Juan");
        assertFalse(lista.isEmpty());
        assertEquals("juan@example.com", lista.get(0).getEmail());
    }

    @Test
    void testEmailYaExisteYEliminarLogico() throws Exception {
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Ana");
        dto.setEmail("ana@example.com");
        dto.setTelefono("000");
        dto.setActivo(true);

        dao.registrarCliente(dto);
        assertTrue(dao.emailYaExiste("ana@example.com"));

        boolean eliminado = dao.eliminarLogicoCliente(dto.getId());
        assertTrue(eliminado);

        List<ClienteDTO> lista = dao.consultarClientes("Ana");
        assertTrue(lista.isEmpty());
    }
}
