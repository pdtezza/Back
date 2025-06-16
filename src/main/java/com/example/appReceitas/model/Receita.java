package com.example.appReceitas.model;

import java.util.List;

import lombok.Data;

@Data
public class Receita {
    private String id;
    private String titulo;
    private List<String> ingredientes;
    private List<String> modoPreparo;
    private List<String> dicas;
    private String usuarioId;
    private String usuarioNome;
    private String fotoUrl;
    private boolean privado;
    private String autorId;

    private int likes;         
    private int visualizacoes; 
}
