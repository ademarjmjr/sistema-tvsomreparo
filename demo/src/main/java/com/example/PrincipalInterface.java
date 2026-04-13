package com.example;

import com.example.service.PdfService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PrincipalInterface extends JFrame {

    private JTextField txtBusca, txtNumeroOs, txtNome, txtCpf,
            txtProduto, txtMarca, txtModelo, txtSerie, txtValor;

    private JTextArea txtCondicao, txtDefeito;

    private PdfService pdfService = new PdfService();

    private Color fundoSistema = new Color(245, 247, 250);

    // CONTROLE AUTOMÁTICO DA OS
    private static int proximoNumeroOs = 1;

    public PrincipalInterface() {

        setTitle("TVSOMREPARO - Gestão de Ordens de Serviço");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Font fonte = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fonte);
        UIManager.put("TextField.font", fonte);
        UIManager.put("TextArea.font", fonte);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));

        JPanel painelMestre = new JPanel(new BorderLayout(10, 10));
        painelMestre.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelMestre.setBackground(fundoSistema);

        // BUSCA
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBackground(fundoSistema);

        painelBusca.add(new JLabel("🔍 Pesquisar OS (Nº):"));
        txtBusca = new JTextField(10);
        painelBusca.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        painelBusca.add(btnBuscar);

        painelMestre.add(painelBusca, BorderLayout.NORTH);

        //CENTRO
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(fundoSistema);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 0.5;
        centro.add(criarPainelDados(), gbc);

        gbc.gridy = 1;
        centro.add(criarPainelEquipamento(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        gbc.gridheight = 2;
        centro.add(criarPainelDescricoes(), gbc);

        painelMestre.add(centro, BorderLayout.CENTER);

        // BOTÃO
        JButton btnGerar = new JButton("GERAR ORDEM DE SERVIÇO");
        btnGerar.setBackground(new Color(0, 120, 215));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setFocusPainted(false);
        btnGerar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnGerar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGerar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGerar.addActionListener(e -> acaoGerarPdf());

        painelMestre.add(btnGerar, BorderLayout.SOUTH);

        add(painelMestre);

        // GERA PRIMEIRA OS AUTOMATICAMENTE
        gerarNovoNumeroOs();
    }

    // GERA NOVO NÚMERO
    private void gerarNovoNumeroOs() {
        txtNumeroOs.setText(String.format("%04d", proximoNumeroOs));
        txtNumeroOs.setEditable(false); // bloqueia edição
    }

    // Card
    private Border criarBordaCard(String titulo) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(titulo),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );
    }

    private JPanel criarPainelDados() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(criarBordaCard("Dados do Cliente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumeroOs = new JTextField();
        adicionarCampo(p, gbc, 0, "Nº da OS:", txtNumeroOs);

        adicionarCampo(p, gbc, 1, "Nome Completo:", txtNome = new JTextField());
        adicionarCampo(p, gbc, 2, "CPF/CNPJ:", txtCpf = new JTextField());

        return p;
    }

    private JPanel criarPainelEquipamento() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(criarBordaCard("Equipamento & Defeito"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        adicionarCampo(p, gbc, 0, "Produto:", txtProduto = new JTextField());
        adicionarCampo(p, gbc, 1, "Marca:", txtMarca = new JTextField());
        adicionarCampo(p, gbc, 2, "Modelo:", txtModelo = new JTextField());
        adicionarCampo(p, gbc, 3, "Nº de Série:", txtSerie = new JTextField());
        adicionarCampo(p, gbc, 4, "Valor R$:", txtValor = new JTextField()); txtValor.setToolTipText("Opcional - pode ser informado depois");

        return p;
    }

    private JPanel criarPainelDescricoes() {
        JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
        p.setBackground(fundoSistema);

        p.add(criarAreaTexto("Condição Física:", txtCondicao = new JTextArea()));
        p.add(criarAreaTexto("Descrição do Defeito:", txtDefeito = new JTextArea()));

        return p;
    }

    private JPanel criarAreaTexto(String titulo, JTextArea area) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(criarBordaCard(titulo));

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 8, 8, 8));

        painel.add(new JScrollPane(area), BorderLayout.CENTER);
        return painel;
    }

    private void adicionarCampo(JPanel p, GridBagConstraints gbc, int y, String label, JTextField campo) {

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0;

        JLabel lbl = new JLabel(label);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 8, 5, 8)
        ));

        p.add(campo, gbc);
    }

    private void acaoGerarPdf() {
    try {
        int numOs = Integer.parseInt(txtNumeroOs.getText());

        // CORREÇÃO DO VALOR (AGORA PODE SER VAZIO)
        double valor = 0.0;
        String textoValor = txtValor.getText().trim();

        if (!textoValor.isEmpty()) {
            try {
                valor = Double.parseDouble(textoValor.replace(",", "."));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Valor inválido!");
                return;
            }
        }

        String nomeArquivo = "OS_" + String.format("%04d", numOs) + ".pdf";

        pdfService.gerarOrdemServico(
                numOs,
                nomeArquivo,
                txtNome.getText(),
                txtCpf.getText(),
                txtProduto.getText(),
                txtMarca.getText(),
                txtModelo.getText(),
                txtSerie.getText(),
                txtCondicao.getText(),
                txtDefeito.getText(),
                valor
        );

        JOptionPane.showMessageDialog(this, "OS Gerada com Sucesso!");

        // INCREMENTA PARA PRÓXIMA OS
        proximoNumeroOs++;
        gerarNovoNumeroOs();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new PrincipalInterface().setVisible(true));
    }
}