package com.example.appReceitas.model;

import lombok.Data;

@Data
public class Receita {
    private String titulo;
    private String descricao;
    private String imagemUrl;
    private String autorId;
}
