package com.example.appReceitas.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.appReceitas.model.Receita;
import com.example.appReceitas.service.ReceitaService;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaService receitaService;

    @PostMapping
    public String criarReceita(@RequestBody Receita receita) {
        try {
            return receitaService.salvarReceita(receita);
        } catch (InterruptedException | ExecutionException e) {
            return "Erro ao salvar receita: " + e.getMessage();
        }
    }
}
