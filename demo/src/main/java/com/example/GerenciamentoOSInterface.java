package com.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GerenciamentoOSInterface extends JFrame {

    private JTextField txtIdOs;
    private JTextArea txtProcedimentos;
    private JComboBox<String> cbStatus;
    
    // Parâmetros de conexão sincronizados com a TelaPrincipal
    private final String URL = "jdbc:mysql://localhost:3306/tvsom_db?allowPublicKeyRetrieval=true&useSSL=false";
    private final String USER = "root";
    private final String PASS = "1234"; // Coloque a mesma senha da TelaPrincipal

    public GerenciamentoOSInterface() {
        setTitle("Controle Interno - TVSOMREPARO");
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCentral = new JPanel(new GridLayout(0, 1, 5, 5));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        painelCentral.add(new JLabel("Nº da OS:"));
        txtIdOs = new JTextField();
        painelCentral.add(txtIdOs);

        JButton btnBuscar = new JButton("Buscar OS");
        btnBuscar.addActionListener(e -> buscarOS());
        painelCentral.add(btnBuscar);

        painelCentral.add(new JLabel("Status Atual:"));
        String[] opcoesStatus = {"Espera de orçamento", "Em andamento", "Autorizada", "Pronto para entrega"};
        cbStatus = new JComboBox<>(opcoesStatus);
        painelCentral.add(cbStatus);

        painelCentral.add(new JLabel("Procedimentos / Relato Técnico:"));
        txtProcedimentos = new JTextArea(5, 20);
        txtProcedimentos.setLineWrap(true);
        painelCentral.add(new JScrollPane(txtProcedimentos));

        JButton btnSalvar = new JButton("Atualizar Registro Interno");
        btnSalvar.setBackground(new Color(41, 128, 185));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> atualizarOS());

        add(painelCentral, BorderLayout.CENTER);
        add(btnSalvar, BorderLayout.SOUTH);
    }

    private void buscarOS() {
        String sql = "SELECT procedimentos, status_os FROM ordens_servico WHERE id_os = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(txtIdOs.getText()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtProcedimentos.setText(rs.getString("procedimentos"));
                cbStatus.setSelectedItem(rs.getString("status_os"));
            } else {
                JOptionPane.showMessageDialog(this, "OS não encontrada no banco!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar: " + e.getMessage());
        }
    }

    private void atualizarOS() {
        String sql = "UPDATE ordens_servico SET procedimentos = ?, status_os = ? WHERE id_os = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtProcedimentos.getText());
            stmt.setString(2, cbStatus.getSelectedItem().toString());
            stmt.setInt(3, Integer.parseInt(txtIdOs.getText()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage());
        }
    }
}