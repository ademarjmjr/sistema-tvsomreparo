package com.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.FileOutputStream;

public class PdfService {

    public void gerarOrdemServico(String caminhoArquivo, String nome, String cpf, String produto, String marca, String modelo, String serie, String condicao, String defeito, double valor) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(caminhoArquivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Cabeçalho Principal
            document.add(new Paragraph("TVSOMREPARO").setBold().setFontSize(22));
            document.add(new Paragraph("ASSISTÊNCIA TÉCNICA EM TV E SOM").setFontSize(10));
            document.add(new Paragraph("TÉCNICO RESPONSÁVEL: Ademar Junior. CPF: 999.888.777.05").setFontSize(10));
            
            // Linha tracejada separadora
            DashedLine line = new DashedLine(1f);
            document.add(new LineSeparator(line));

            // Tabela de Informações (Grade de 2 colunas)
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            
            table.addCell(new Cell().add(new Paragraph("Nome: " + nome)));
            table.addCell(new Cell().add(new Paragraph("Produto: " + produto)));
            
            table.addCell(new Cell().add(new Paragraph("CPF/CNPJ: " + cpf)));
            table.addCell(new Cell().add(new Paragraph("Marca: " + marca)));
            
            table.addCell(new Cell().add(new Paragraph("Modelo: " + modelo)));
            table.addCell(new Cell().add(new Paragraph("Nº Série: " + serie)));
            
            table.addCell(new Cell(1, 2).add(new Paragraph("Condição Geral: " + condicao)));
            table.addCell(new Cell(1, 2).add(new Paragraph("DESCRIÇÃO DO PROBLEMA: " + defeito)));
            table.addCell(new Cell(1, 2).add(new Paragraph("VALOR SERVIÇO: R$ " + valor).setBold()));

            document.add(table);

            // Cláusulas Legais (Texto menor conforme original)
            document.add(new Paragraph("\nCONDIÇÕES GERAIS:").setBold().setFontSize(11));
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