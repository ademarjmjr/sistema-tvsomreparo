package com.example.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import java.io.FileOutputStream;

public class PdfService {

    public void gerarOrdemServico(int numeroOs, String caminhoArquivo, String nome, String cpf, String produto, String marca, String modelo, String serie, String condicao, String defeito, double valor) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(caminhoArquivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // --- CABEÇALHO: LOGO À ESQUERDA | INFOS À DIREITA ---
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            headerTable.setBorder(Border.NO_BORDER);

            // Coluna da Esquerda: Logotipo
            Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
            try {
                // Ajustado para o seu arquivo específico
                Image logo = new Image(ImageDataFactory.create("demo/src/main/resources/logo_tvsomreparo.png"));
                logo.setWidth(120); // Aumentei um pouco para compensar os 100px e manter nítido
                logoCell.add(logo);
            } catch (Exception e) {
                logoCell.add(new Paragraph("TVSOMREPARO").setBold().setFontSize(18));
            }
            headerTable.addCell(logoCell);

            // Coluna da Direita: Informações da OS
            Cell infoCell = new Cell().setBorder(Border.NO_BORDER);
            infoCell.setTextAlignment(TextAlignment.CENTER);
            infoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            infoCell.add(new Paragraph("ELETRÔNICA TVSOMREPARO").setBold().setFontSize(18));
            infoCell.add(new Paragraph("ORDEM DE SEVIÇO:").setBold().setFontSize(16));
            infoCell.add(new Paragraph(String.format("Nº OS: %04d", numeroOs)).setBold().setFontSize(14).setFontColor(new DeviceRgb(0, 0, 255)));
            infoCell.add(new Paragraph("TÉCNICO RESPONSÁVEL: Ademar Junior.").setFontSize(10));
            infoCell.add(new Paragraph("CPF: 999.888.777.05").setFontSize(10));
            headerTable.addCell(infoCell);

            document.add(headerTable);
            
            // Linha divisória
            document.add(new Paragraph("______________________________________________________________________________")
                    .setBold().setMarginBottom(10));

            // --- TABELA DE DADOS ---
            Table dataTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            dataTable.addCell(new Cell().add(new Paragraph("Nome: " + nome)));
            dataTable.addCell(new Cell().add(new Paragraph("Produto: " + produto)));
            dataTable.addCell(new Cell().add(new Paragraph("CPF/CNPJ: " + cpf)));
            dataTable.addCell(new Cell().add(new Paragraph("Marca: " + marca)));
            dataTable.addCell(new Cell().add(new Paragraph("Modelo: " + modelo)));
            dataTable.addCell(new Cell().add(new Paragraph("Nº Série: " + serie)));
            dataTable.addCell(new Cell(1, 2).add(new Paragraph("Condição física do produto: " + condicao)));
            dataTable.addCell(new Cell(1, 2).add(new Paragraph("DESCRIÇÃO DO PROBLEMA: " + defeito).setMinHeight(50)));
            String valorTexto = (valor == 0) ? "ORÇAMENTO PENDENTE" : String.format("R$ %.2f", valor);

            dataTable.addCell(
                new Cell(1, 2)
                    .add(new Paragraph("VALOR SERVIÇO: " + valorTexto)
                    .setBold()
                    .setFontSize(12))
            );
            document.add(dataTable);

            // --- RODAPÉ ---
            // Cláusulas Legais (Texto menor conforme original)
            document.add(new Paragraph("\nOBSERVAÇÕES GERAIS:").setBold().setFontSize(11));
            document.add(new Paragraph("1. Preço sujeito a alteração até a conclusão dos serviços.").setFontSize(8));
            document.add(new Paragraph("2. Quanto tempo a loja pode guardar o produto?").setFontSize(8));
            document.add(new Paragraph("3. Conclusão, podemos concluir que é razoável dizer que o consumidor/cliente tem prazo\r\n" + //
                "de 30 dias para buscar o bem. Não indo recuperá-lo, a empresa não pode apropriar-se ou\r\n" + //
                "alienar o bem. Contudo, é válida multa, desde que estipulada em valor razoável. Após 30\r\n" + //
                "dias de espera: o valor da diária pela guarda do produto acima mencionado é de R$ 2,00\r\n" + //
                "ao dia.").setFontSize(8));
            document.add(new Paragraph("4. Após 180 dias o produto será sucateado/descartado.").setFontSize(10));
            document.add(new Paragraph("5. NOSSAS PEÇAS E SERVIÇO TEM GARANTIA DE ACORDO COM O COD. DE DEFESA\r\n" + //
                "DO CONSUMIDOR ART. 26 par.1\r\n").setFontSize(10));

            // Espaço para Assinaturas
            document.add(new Paragraph("\n\n_____________________________________          __________________________________").setMarginTop(20));
            document.add(new Paragraph("                       Cliente/Consumidor                                                                          Atendente").setFontSize(10));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}