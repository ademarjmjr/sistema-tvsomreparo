package com.example.model;

public class OrdemServico {
    private int id;
    private String dataEntrada;
    private String cliente;
    private String cpfCnpj;
    private String tecnico = "Ademar Junior"; // Padrão conforme seu documento
    private String produto; // Ex: TV, Inversor, Som
    private String marca;
    private String modelo;
    private String numSerie;
    private String condicao;
    private String defeito;
    private double valor;

    // Gere os Getters e Setters (O VS Code faz isso clicando com o botão direito)
}
