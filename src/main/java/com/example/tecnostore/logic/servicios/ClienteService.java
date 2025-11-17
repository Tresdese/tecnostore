package com.example.tecnostore.logic.servicios;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.logic.dao.ClienteDAO;
import com.example.tecnostore.logic.dto.ClienteDTO;

public class ClienteService {

    private ClienteDAO clienteDAO;

    public ClienteService() throws Exception {
        this.clienteDAO = new ClienteDAO();
    }

    public String registrarCliente(ClienteDTO cliente) throws SQLException {
        try {
            if (clienteDAO.emailYaExiste(cliente.getEmail())) {
                return "Error: El email '" + cliente.getEmail() + "' ya está registrado.";
            }

            if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                return "Error: El nombre no puede estar vacío.";
            }

            if (clienteDAO.registrarCliente(cliente)) {
                return "Cliente registrado exitosamente.";
            } else {
                return "Error: No se pudo registrar al cliente.";
            }

        } catch (SQLException e) {
            return "Error al registrar: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    public String actualizarCliente(ClienteDTO cliente) throws SQLException {
        try {
            clienteDAO.actualizarCliente(cliente);
            return "Cliente actualizado.";
        } catch (SQLException e) {
            return "Error al actualizar: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    public String eliminarCliente(int id) throws SQLException {
        try {
            clienteDAO.eliminarLogicoCliente(id);
            return "Cliente eliminado (lógicamente).";
        } catch (SQLException e) {
            return "Error al eliminar: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        } 
    }

    public List<ClienteDTO> buscarClientes(String criterio) throws Exception {
        try {
            return clienteDAO.consultarClientes(criterio);
        } catch (SQLException e) {
            System.err.println("Error al buscar: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void generarReporteClientesActivos() throws Exception {
        System.out.println("\n--- REPORTE DE CLIENTES ACTIVOS ---");
        List<ClienteDTO> activos = buscarClientes("");

        if (activos.isEmpty()) {
            System.out.println("No hay clientes activos para mostrar.");
            return;
        }

        activos.forEach(cliente -> {
            System.out.printf("ID: %d | Nombre: %s | Email: %s\n",
                    cliente.getId(), cliente.getNombre(), cliente.getEmail());
        });
        System.out.println("------------------------------------");
    }

    public void generarReporteEstadisticas() throws Exception {
        System.out.println("\n--- REPORTE DE ESTADÍSTICAS ---");
        try {
            List<ClienteDTO> activos = buscarClientes("");
            int inactivos = 0;

            System.out.printf("Total Clientes Activos: %d\n", activos.size());
            System.out.printf("Total Clientes Inactivos: %d (Simulado)\n", inactivos);
            System.out.printf("Total de Registros: %d\n", (activos.size() + inactivos));
            System.out.println("---------------------------------");

        } catch (Exception e) {
            System.err.println("Error al generar estadísticas: " + e.getMessage());
        }
    }
}