package com.example.tecnostore.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionBD {
    public Connection conn;

    protected void cerrar(PreparedStatement stm) throws Exception{
        if (stm != null) stm.close();
    }

    protected void cerrar(ResultSet rst) throws Exception{
        if (rst != null) rst.close();
    }

    public ConexionBD() throws Exception {
        String driver = "com.mysql.cj.jdbc.Driver";
        String user = "root";
        String pass = "admin";
        String basedatos = "tecnostorebd";
        String server = "jdbc:mysql://localhost:3306/" + basedatos;
        
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(server, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error en BD: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new Exception("Error al cargar el driver JDBC: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
