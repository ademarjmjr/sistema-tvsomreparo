package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL = "jdbc:mysql://localhost:3306/tvsom_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";    
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco", e);
        }
    }
}