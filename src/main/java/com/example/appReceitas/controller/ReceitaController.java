package com.example.appReceitas.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.appReceitas.model.Cliente;
import com.example.appReceitas.model.Receita;
import com.example.appReceitas.service.ReceitaService;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

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

    @GetMapping("/publicas")
    public List<QueryDocumentSnapshot> buscarPublicas() throws ExecutionException, InterruptedException {
        return receitaService.listarReceitasPublicas();
    }

    @PostMapping("/{id}/like")
    public Receita darLike(
            @PathVariable String id,
            @RequestParam String clienteId // <-- agora pega o id da query
    ) throws Exception {
        // 1. Incrementa likes da receita
        Receita receitaAtualizada = receitaService.darLike(id);

        // 2. Adiciona receita à lista de favoritos do usuário (protege contra null)
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);

        DocumentSnapshot snapshot = docRef.get().get();
        if (snapshot.get("receitasFavoritas") == null) {
            docRef.update("receitasFavoritas", new ArrayList<String>());
        }
        docRef.update("receitasFavoritas", FieldValue.arrayUnion(id));
        return receitaAtualizada;
    }

    @DeleteMapping("/{id}/like")
    public Receita removerLike(
            @PathVariable String id,
            @RequestParam String clienteId
    ) throws Exception {
        // 1. Decrementa likes da receita
        Receita receitaAtualizada = receitaService.removerLike(id);

        // 2. Remove receita da lista de favoritos do usuário
        Firestore db = FirestoreClient.getFirestore();
        db.collection("clientes").document(clienteId)
            .update("receitasFavoritas", FieldValue.arrayRemove(id));
        return receitaAtualizada;
    }

    @GetMapping("/favoritos")
    public List<String> getFavoritos(@RequestParam String clienteId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);
        Cliente cliente = docRef.get().get().toObject(Cliente.class);

        if (cliente != null && cliente.getReceitasFavoritas() != null) {
            return cliente.getReceitasFavoritas();
        }
        return new ArrayList<>();
    }
    @PostMapping("/{id}/view")
    public Receita adicionarView(@PathVariable String id) throws Exception {
        return receitaService.adicionarView(id);
    }
    @GetMapping("/populares")
    public List<Receita> listarPopulares() throws Exception {
        return receitaService.listarReceitasPopulares();
    }

}
