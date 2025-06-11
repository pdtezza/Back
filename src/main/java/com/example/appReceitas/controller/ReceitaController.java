package com.example.appReceitas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.appReceitas.model.Receita;
import com.example.appReceitas.service.ReceitaService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaService receitaService;

    @PostMapping
    public String criarReceita(@RequestBody Receita receita, HttpServletRequest request) throws Exception {
        String autorId = (String) request.getAttribute("firebaseUid");
        receita.setAutorId(autorId);
        return receitaService.salvarReceita(receita);
    }

    @GetMapping
    public List<Receita> listarReceitas() throws Exception {
        return receitaService.listarReceitas();
    }

    @GetMapping("/{id}")
    public Receita buscarReceita(@PathVariable String id) throws Exception {
        return receitaService.buscarReceitaPorId(id);
    }

    @PutMapping("/{id}")
    public String atualizarReceita(@PathVariable String id, @RequestBody Receita receita, HttpServletRequest request) throws Exception {
        String autorId = (String) request.getAttribute("firebaseUid");
        return receitaService.atualizarReceita(id, receita, autorId);
    }

    @DeleteMapping("/{id}")
    public String deletarReceita(@PathVariable String id, HttpServletRequest request) throws Exception {
        String autorId = (String) request.getAttribute("firebaseUid");
        return receitaService.deletarReceita(id, autorId);
    }
}
