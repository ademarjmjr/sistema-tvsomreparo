package com.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.service.PdfService;
import javax.swing.text.MaskFormatter;
//import java.nio.file.Files;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;

public class PrincipalInterface extends JFrame {

    private final String URL = "jdbc:mysql://localhost:3306/tvsom_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private final String USER = "root";
    private final String PASS = "1234";

    private JTextField txtBusca, txtNumeroOs, txtNome, txtCpf,
            txtProduto, txtMarca, txtModelo, txtSerie, txtValor;

    private JTextArea txtCondicao, txtDefeito;

    private PdfService pdfService = new PdfService();

    private Color fundoSistema = new Color(245, 247, 250);

    // CONTROLE AUTOMÁTICO DA OS
    private static int proximoNumeroOs = 1;
    private final String ARQUIVO_CONTADOR = "ultimo_numero_os.txt";

    public PrincipalInterface() {

        setTitle("TVSOMREPARO - Gestão de Ordens de Serviço");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //criarTelaInicial();
        //setVisible(true);

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

        painelBusca.add(new JLabel(" Pesquisar OS (Nº):"));
        txtBusca = new JTextField(10);
        painelBusca.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");

        btnBuscar.addActionListener(e -> {
            String busca = txtBusca.getText().trim();
            if (!busca.isEmpty()) {
                try {
                    // Formata o número (ex: 2 vira OS_0002.pdf)
                    int num = Integer.parseInt(busca);
                    String nomeArquivo = String.format("OS_%04d.pdf", num);
                    java.io.File arquivo = new java.io.File(nomeArquivo);

                    if (arquivo.exists()) {
                        java.awt.Desktop.getDesktop().open(arquivo);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "OS não encontrada: " + nomeArquivo);
                    }
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Digite um número válido.");
                }
            }
        });
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
        this.getRootPane().setDefaultButton(btnBuscar);

        btnGerar.addActionListener(e -> acaoGerarPdf());

        painelMestre.add(btnGerar, BorderLayout.SOUTH);

        add(painelMestre);

        // GERA PRIMEIRA OS AUTOMATICAMENTE
        gerarNovoNumeroOs();

        configurarSomenteLetras(txtNome);
        configurarCPFFormatado(txtCpf);
        //configurarValidacaoVisualCPF(txtCpf);    

        configurarPuloComEnter(txtNome);
        configurarPuloComEnter(txtCpf);     
        configurarPuloComEnter(txtProduto);
        configurarPuloComEnter(txtMarca);
        configurarPuloComEnter(txtModelo);
        configurarPuloComEnter(txtSerie);
        configurarPuloComEnter(txtValor);
    }
    
    private void configurarSomenteLetras(javax.swing.JTextField campo) {
        ((javax.swing.text.AbstractDocument) campo.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) 
                    throws javax.swing.text.BadLocationException {

                // Permite vazio também
                if (text.isEmpty() || text.matches("[a-zA-Z\\s\\p{L}]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    
    private void configurarCPFFormatado(javax.swing.JTextField campo) {
        ((javax.swing.text.AbstractDocument) campo.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                    throws javax.swing.text.BadLocationException {

                String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());

                // Monta o novo texto como se fosse inserir normalmente
                String novoTexto = textoAtual.substring(0, offset) + text + textoAtual.substring(offset + length);

                // Remove tudo que não é número
                String numeros = novoTexto.replaceAll("[^0-9]", "");

                // Limita a 11 dígitos
                if (numeros.length() > 11) {
                    numeros = numeros.substring(0, 11);
                }

                // Aplica a máscara
                StringBuilder formatado = new StringBuilder();

                for (int i = 0; i < numeros.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatado.append(".");
                    } else if (i == 9) {
                        formatado.append("-");
                    }
                    formatado.append(numeros.charAt(i));
                }

                // Substitui tudo no campo
                fb.replace(0, fb.getDocument().getLength(), formatado.toString(), attrs);
            }
        });
    }

    private boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        // Deve ter 11 dígitos
        if (cpf.length() != 11) return false;

        // Elimina CPFs inválidos conhecidos (todos iguais)
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            int peso = 10;

            // 1º dígito
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            int dig1 = 11 - (soma % 11);
            if (dig1 > 9) dig1 = 0;

            // 2º dígito
            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            int dig2 = 11 - (soma % 11);
            if (dig2 > 9) dig2 = 0;

            // Verifica se bate
            return dig1 == (cpf.charAt(9) - '0') &&
                dig2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }
    /*     
    private void configurarValidacaoVisualCPF(javax.swing.JTextField campo) {
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String cpf = campo.getText();

                if (cpf.isEmpty()) {
                    campo.setBackground(java.awt.Color.WHITE);
                    return;
                }

                if (validarCPF(cpf)) {
                    campo.setBackground(new java.awt.Color(198, 239, 206)); // verde claro
                } else {
                    campo.setBackground(new java.awt.Color(255, 199, 206)); // vermelho claro
                }
            }
        });
    }
    */
    // GERA NOVO NÚMERO
    private void gerarNovoNumeroOs() {
        proximoNumeroOs = carregarUltimoNumero();
        salvarProximoNumero(proximoNumeroOs);
        txtNumeroOs.setText(String.format("%04d", proximoNumeroOs));
        txtNumeroOs.setEditable(false); // bloqueia edição
    }
    // CARREGA O ÚLTIMO NÚMERO DE OS GERADO (SE EXISTIR)
    private int carregarUltimoNumero() {
        try {
            if (Files.exists(Paths.get(ARQUIVO_CONTADOR))) {
                String conteudo = Files.readString(Paths.get(ARQUIVO_CONTADOR)).trim();
                return Integer.parseInt(conteudo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler contador, iniciando em 1.");
        }
        return 1;
    }

    private void salvarProximoNumero(int numero) {
        try {
            Files.writeString(Paths.get(ARQUIVO_CONTADOR), String.valueOf(numero));
        } catch (IOException e) {
            System.err.println("Erro ao salvar contador.");
        }
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
        // Validação campos obrigatórios
        if (txtNome.getText().trim().isEmpty() || txtProduto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Erro: O Nome do Cliente e o Produto são obrigatórios!", 
                "Campos Vazios", 
                JOptionPane.WARNING_MESSAGE);

            txtNome.requestFocus();
            return;
        }

        // VALIDAÇÃO DO CPF (AQUI É O LUGAR CERTO)
        /* 
        String cpf = txtCpf.getText().trim();

        if (!cpf.isEmpty() && !validarCPF(cpf)) {
            JOptionPane.showMessageDialog(
                this,
                "CPF inválido!",
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            txtCpf.requestFocus();
            return; // BLOQUEIA antes de gerar PDF
        }*/

        // SQL atualizado com os novos campos
        String sql = "INSERT INTO ordens_servico (id_os, cliente_nome, cliente_cpf, aparelho_modelo, aparelho_marca, aparelho_serie, status_os, valor_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // 1. Número da OS
            stmt.setInt(1, Integer.parseInt(txtNumeroOs.getText()));
            
            // 2. Cliente
            stmt.setString(2, txtNome.getText());
            stmt.setString(3, txtCpf.getText());
            
            // 3. Aparelho (Produto/Modelo/Marca/Série)
            // Aqui você pode concatenar o Produto com o Modelo se preferir, ou usar apenas o Modelo
            stmt.setString(4, txtModelo.getText()); 
            stmt.setString(5, txtMarca.getText());
            stmt.setString(6, txtSerie.getText());
            
            // 4. Status Inicial
            stmt.setString(7, "Espera de orçamento");

            // 5. Tratamento do Valor (Se vazio, vira 0.0)
            String valorTexto = txtValor.getText().replace(",", ".").trim();
            double valor = valorTexto.isEmpty() ? 0.0 : Double.parseDouble(valorTexto);
            stmt.setDouble(8, valor);
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "OS Gerada com Sucesso!");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar no banco: " + e.getMessage());
        }
        
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
            // Dentro do botão Gerar PDF, após criar o PDF com sucesso:
            try (Connection conn = ConexaoBanco.getConexao()) {
                String sqlInsert = "INSERT INTO ordens_servico (id_os, status_os) VALUES (?, 'Espera de orçamento')";
                PreparedStatement st = conn.prepareStatement(sqlInsert);
                st.setInt(1, Integer.parseInt(txtNumeroOs.getText())); // Pega o número da OS que você digitou
                st.executeUpdate();
            } catch (Exception e) {
                // Se já existir, ele só ignora ou você pode tratar o erro
                System.out.println("Nota: Registro inicial da OS já existia ou erro de conexão.");
            }

            // INCREMENTA PARA PRÓXIMA OS
            //proximoNumeroOs++;
            salvarProximoNumero(numOs + 1);
            gerarNovoNumeroOs();

            limparCampos(); 
            txtNumeroOs.setText(String.valueOf(numOs + 1)); // Atualiza para o novo número
            txtNome.requestFocus();limparCampos();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
   
        private void limparCampos() {
            txtNome.setText("");
            txtCpf.setText("");

            txtProduto.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtSerie.setText("");
            txtCondicao.setText("");
            txtDefeito.setText("");
            txtValor.setText("");
        }

        private void configurarPuloComEnter(javax.swing.JTextField campo) {
            campo.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        campo.transferFocus(); // Pula para o próximo componente na ordem
                    }
                }
            });
        }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new PrincipalInterface().setVisible(true));
    }
        
}