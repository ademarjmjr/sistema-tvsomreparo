package com.example;

import com.example.service.PdfService;
import java.io.File;

public class App {
    public static void main(String[] args) {
        // 1. Instancia o serviço de PDF que criamos
        PdfService pdfService = new PdfService();

        new GerenciamentoOSInterface().setVisible(true);
        // 2. Define o local onde o PDF será salvo (na pasta do projeto)
        String caminhoArquivo = "OS_Teste_TVSOMREPARO.pdf";

        // 3. Simula dados de uma OS real da sua assistência
        int numeroOs = 1;
        String cliente = "Cliente de Teste Porto Velho";
        String produto = "TV LED 50''"; // Poderia ser Som ou Inversor Solar
        String modelo = "UN50AU7700";
        double valor = 350.00;

        System.out.println("Iniciando geração da OS...");

        // 4. Chama o método para gerar o documento
        pdfService.gerarOrdemServico(numeroOs, caminhoArquivo, "Cliente Porto Velho", "000.000.000-00", "TV LED", "Samsung", "UN50AU7700", "SN123456", "Arranhado", "Não liga", 350.00);
        // 5. Verifica se o arquivo foi criado com sucesso
        File arquivo = new File(caminhoArquivo);
        if (arquivo.exists()) {
            System.out.println("========================================");
            System.out.println("SUCESSO! O arquivo foi gerado em:");
            System.out.println(arquivo.getAbsolutePath());
            System.out.println("========================================");
        } else {
            System.out.println("Erro: O arquivo não foi encontrado.");
        }
    }

}