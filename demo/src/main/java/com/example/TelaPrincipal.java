package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private Color fundo = new Color(236, 240, 245);
    private Color azul = new Color(41, 128, 185);

    private JTextArea areaOrcamento;
    private JTextArea areaAutorizada;
    private JTextArea areaPronta;

    // Configurações do Banco de Dados
    private final String URL = "jdbc:mysql://localhost:3306/tvsom_db?allowPublicKeyRetrieval=true&useSSL=false";
    private final String USER = "root";
    private final String PASS = "1234"; // <--- COLOQUE SUA SENHA AQUI

    public TelaPrincipal() {
        setTitle("Sistema de Gestão - TVSOMREPARO");
        setSize(1150, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarTela();
        atualizarListas();

        setVisible(true);
        // Dentro do construtor da TelaPrincipal, logo após criarTela();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                atualizarListas(); // Recarrega os dados do banco automaticamente
            }
        });
    }

    private void criarTela() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(fundo);

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titulo = new JLabel("Painel de Ordens de Serviço");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(titulo, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // ================= AÇÕES =================
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        acoes.setBackground(fundo);
        acoes.setBorder(new EmptyBorder(5, 20, 50, 20));

        JButton btnNova = criarBotao("Nova OS", azul, Color.WHITE);
        btnNova.addActionListener(e -> new PrincipalInterface().setVisible(true));

        JLabel lblBusca = new JLabel("Pesquisar Cliente:");
        JTextField txtCliente = criarInput("Cliente");
        JButton btnBusca = criarBotao("Buscar", Color.BLUE, Color.WHITE);

        JLabel lblOS = new JLabel("Pesquisar OS:");
        JTextField txtOS = criarInput("OS");

        JButton btnBuscarOS = criarBotao("Buscar PDF", Color.BLUE, Color.WHITE);
        btnBuscarOS.addActionListener(e -> {
            String busca = txtOS.getText().trim();
            if (!busca.isEmpty()) {
                try {
                    int num = Integer.parseInt(busca);
                    String nomeArquivo = String.format("OS_%04d.pdf", num);
                    java.io.File arquivo = new java.io.File(nomeArquivo);
                    if (arquivo.exists()) {
                        java.awt.Desktop.getDesktop().open(arquivo);
                    } else {
                        JOptionPane.showMessageDialog(this, "Arquivo PDF não encontrado.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Número de OS inválido.");
                }
            }
        });

        JButton btnGerenciamento = criarBotao("Gerenciamento Interno", new Color(44, 62, 80), Color.WHITE);
        btnGerenciamento.addActionListener(e -> new GerenciamentoOSInterface().setVisible(true));

        // Botão para atualizar o painel manualmente
        JButton btnAtualizar = criarBotao("⟳ Atualizar Painel", new Color(39, 174, 96), Color.WHITE);
        btnAtualizar.addActionListener(e -> atualizarListas());

        acoes.add(btnNova);
        acoes.add(lblBusca);
        acoes.add(txtCliente);
        acoes.add(btnBusca);
        acoes.add(lblOS);
        acoes.add(txtOS);
        acoes.add(btnBuscarOS);
        acoes.add(btnGerenciamento);
        acoes.add(btnAtualizar);

        root.add(acoes, BorderLayout.NORTH);

        // ================= COLUNAS =================
        JPanel colunas = new JPanel(new GridLayout(1, 3, 25, 0));
        colunas.setBorder(new EmptyBorder(10, 25, 25, 25));
        colunas.setBackground(fundo);

        areaOrcamento = criarArea();
        areaAutorizada = criarArea();
        areaPronta = criarArea();

        colunas.add(criarCard("Aguardando Orçamento", areaOrcamento));
        colunas.add(criarCard("Autorizadas / Em Manutenção", areaAutorizada));
        colunas.add(criarCard("Prontas para Entrega", areaPronta));

        root.add(colunas, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void atualizarListas() {
        areaOrcamento.setText("");
        areaAutorizada.setText("");
        areaPronta.setText("");

        String sql = "SELECT id_os, cliente_nome, aparelho_modelo, status_os FROM ordens_servico";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_os");
                String cliente = rs.getString("cliente_nome");
                String modelo = rs.getString("aparelho_modelo");
                String status = rs.getString("status_os");

                String linha = String.format(" OS %d | %s\n └ %s\n--------------------------\n", id, cliente, modelo);

                // Lógica de separação por coluna baseada no status do banco
                if (status != null) {
                    if (status.equalsIgnoreCase("Espera de orçamento")) {
                        areaOrcamento.append(linha);
                    } else if (status.equalsIgnoreCase("Em andamento") || status.equalsIgnoreCase("Autorizada")) {
                        areaAutorizada.append(linha);
                    } else if (status.equalsIgnoreCase("Pronto para entrega")) {
                        areaPronta.append(linha);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao Banco de Dados: " + e.getMessage());
        }
    }

    // ================= MÉTODOS AUXILIARES DE UI (Mantidos conforme original) =================
    private JPanel criarCard(String titulo, JTextArea area) {
        JPanel card = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 15, 15);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBorder(new EmptyBorder(10, 5, 10, 5));
        card.add(lbl, BorderLayout.NORTH);
        card.add(new JScrollPane(area), BorderLayout.CENTER);
        return card;
    }

    private JTextField criarInput(String placeholder) {
        JTextField campo = new JTextField(10);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return campo;
    }

    private JButton criarBotao(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextArea criarArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12)); // Fonte mono para alinhar melhor
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}