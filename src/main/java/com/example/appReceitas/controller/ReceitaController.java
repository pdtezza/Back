package com.example.appReceitas.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.appReceitas.model.Cliente;
import com.example.appReceitas.model.Receita;
import com.example.appReceitas.service.ReceitaService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaService receitaService;

    @PostMapping
    public String criarReceita(@RequestBody Receita receita, HttpServletRequest request) throws Exception {
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

    // ✅ PESQUISAR (filtrando apenas públicas)
    @GetMapping("/pesquisar")
    public List<Receita> pesquisarReceitas(@RequestParam String titulo) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("receitas")
                .whereEqualTo("privado", false)  // <<<< Garantindo que só venha pública
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Receita> resultado = new ArrayList<>();

        for (DocumentSnapshot doc : documents) {
            Receita receita = doc.toObject(Receita.class);
            if (receita != null && receita.getTitulo() != null &&
                    receita.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                receita.setId(doc.getId());
                resultado.add(receita);
            }
        }
        return resultado;
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
            @RequestParam String clienteId
    ) throws Exception {
        Receita receitaAtualizada = receitaService.darLike(id);
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
        Receita receitaAtualizada = receitaService.removerLike(id);
        Firestore db = FirestoreClient.getFirestore();
        db.collection("clientes").document(clienteId)
                .update("receitasFavoritas", FieldValue.arrayRemove(id));
        return receitaAtualizada;
    }

    @GetMapping("/favoritos")
    public List<String> getFavoritos(@RequestParam String clienteId) throws Exception {
        if (clienteId == null || clienteId.isEmpty()) {
            throw new IllegalArgumentException("Parâmetro clienteId é obrigatório!");
        }
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

    // ✅ POPULARES COM FILTRO DE PRIVADAS
    @GetMapping("/populares")
    public List<Receita> listarPopulares() throws Exception {
        return receitaService.listarReceitasPopulares();
    }

    @GetMapping("/autor/{usuarioId}")
    public List<Receita> getReceitasPorAutor(@PathVariable String usuarioId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<Receita> receitas = new ArrayList<>();
        List<QueryDocumentSnapshot> documentos = db.collection("receitas")
                .whereEqualTo("autorId", usuarioId)
                .get()
                .get()
                .getDocuments();

        for (DocumentSnapshot doc : documentos) {
            Receita receita = doc.toObject(Receita.class);
            if (receita != null) {
                receita.setId(doc.getId());
                receitas.add(receita);
            }
        }
        return receitas;
    }

    @GetMapping("/minhasPrivadas")
    public List<Receita> getMinhasPrivadas(@RequestParam String clienteId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<Receita> receitasPrivadas = new ArrayList<>();

        List<QueryDocumentSnapshot> documentos = db.collection("receitas")
                .whereEqualTo("autorId", clienteId)
                .whereEqualTo("privado", true)
                .get()
                .get()
                .getDocuments();

        for (DocumentSnapshot doc : documentos) {
            Receita receita = doc.toObject(Receita.class);
            if (receita != null) {
                receita.setId(doc.getId());
                receitasPrivadas.add(receita);
            }
        }
        return receitasPrivadas;
    }
}
