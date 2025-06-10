package com.example.appReceitas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.appReceitas.model.Cliente;
import com.example.appReceitas.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/cadastro")
    public String cadastrarCliente(
            @RequestBody Cliente cliente,
            @RequestParam String senha) {
        return clienteService.cadastrarClienteComEmailSenha(cliente, senha);
    }
        @PostMapping("/esqueci-senha")
    public String esqueciSenha(@RequestParam String email) {
        return clienteService.enviarLinkRedefinicaoSenha(email);
    }
}
