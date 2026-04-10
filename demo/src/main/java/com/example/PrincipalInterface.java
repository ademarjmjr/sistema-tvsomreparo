package com.example;

import com.example.service.PdfService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
//import javax.swing.border.TitledBorder;
import java.awt.*;

public class PrincipalInterface extends JFrame {
    private JTextField txtBusca, txtNumeroOs, txtNome, txtCpf, txtProduto, txtMarca, txtModelo, txtSerie, txtValor;
    private JTextArea txtCondicao, txtDefeito;
    private PdfService pdfService = new PdfService();
    private Color azulLogo = new Color(0, 173, 239);

    public PrincipalInterface() {
        setTitle("TVSOMREPARO - Gestão de Ordens de Serviço");
        setSize(1000, 600); // Largura maior para caber as duas colunas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel Principal com margens
        JPanel painelMestre = new JPanel(new BorderLayout(10, 10));
        painelMestre.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelMestre.setBackground(Color.WHITE);

        // --- 1. BUSCA (TOPO) ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("🔍 Pesquisar OS (Nº):"));
        txtBusca = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelMestre.add(painelBusca, BorderLayout.NORTH);

        // --- 2. ÁREA CENTRAL (DUAS COLUNAS) ---
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- COLUNA ESQUERDA (DADOS) ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4; gbc.weighty = 0.5;
        centro.add(criarPainelDados(), gbc);

        gbc.gridy = 1;
        centro.add(criarPainelEquipamento(), gbc);

        // --- COLUNA DIREITA (DESCRIÇÕES) ---
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.6; gbc.gridheight = 1;
        centro.add(criarPainelDescricoes(), gbc);

        painelMestre.add(centro, BorderLayout.CENTER);

        // --- 3. BOTÃO GERAR (RODAPÉ) ---
        JButton btnGerar = new JButton("GERAR ORDEM DE SERVIÇO");
        btnGerar.setBackground(azulLogo);
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setFont(new Font("Arial", Font.BOLD, 16));
        btnGerar.setPreferredSize(new Dimension(0, 50));
        btnGerar.addActionListener(e -> acaoGerarPdf());
        painelMestre.add(btnGerar, BorderLayout.SOUTH);

        add(painelMestre);
    }

    private JPanel criarPainelDados() {
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(azulLogo), " Dados do Cliente "));
        
        p.add(new JLabel("Nº da OS:")); txtNumeroOs = new JTextField(); p.add(txtNumeroOs);
        p.add(new JLabel("Nome Completo:")); txtNome = new JTextField(); p.add(txtNome);
        p.add(new JLabel("CPF/CNPJ:")); txtCpf = new JTextField(); p.add(txtCpf);
        return p;
    }

    private JPanel criarPainelEquipamento() {
        JPanel p = new JPanel(new GridLayout(5, 2, 5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(azulLogo), " Equipamento & Defeito "));
        
        p.add(new JLabel("Produto:")); txtProduto = new JTextField(); p.add(txtProduto);
        p.add(new JLabel("Marca:")); txtMarca = new JTextField(); p.add(txtMarca);
        p.add(new JLabel("Modelo:")); txtModelo = new JTextField(); p.add(txtModelo);
        p.add(new JLabel("Nº de Série:")); txtSerie = new JTextField(); p.add(txtSerie);
        p.add(new JLabel("Valor R$:")); txtValor = new JTextField(); p.add(txtValor);
        return p;
    }

    private JPanel criarPainelDescricoes() {
        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.setBackground(Color.WHITE);
        
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBackground(Color.WHITE);
        p1.add(new JLabel("Condição Física:"), BorderLayout.NORTH);
        txtCondicao = new JTextArea();
        txtCondicao.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p1.add(new JScrollPane(txtCondicao), BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBackground(Color.WHITE);
        p2.add(new JLabel("Descrição do Defeito:"), BorderLayout.NORTH);
        txtDefeito = new JTextArea();
        txtDefeito.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p2.add(new JScrollPane(txtDefeito), BorderLayout.CENTER);

        p.add(p1); p.add(p2);
        return p;
    }

    private void acaoGerarPdf() {
        try {
            int numOs = Integer.parseInt(txtNumeroOs.getText());
            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            String nomeArquivo = "OS_" + String.format("%04d", numOs) + ".pdf";

            pdfService.gerarOrdemServico(numOs, nomeArquivo, txtNome.getText(), txtCpf.getText(),
                txtProduto.getText(), txtMarca.getText(), txtModelo.getText(),
                txtSerie.getText(), txtCondicao.getText(), txtDefeito.getText(), valor);

            JOptionPane.showMessageDialog(this, "OS Gerada com Sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new PrincipalInterface().setVisible(true));
    }
}