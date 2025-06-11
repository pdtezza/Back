package com.example.appReceitas.model;

import java.util.List;

import lombok.Data;

@Data
public class Cliente {
    private String id;
    private String nome;
    private String cpf;
    private String email;
    private List<String> receitasFavoritas; 
    private String fotoPerfil; 
}
