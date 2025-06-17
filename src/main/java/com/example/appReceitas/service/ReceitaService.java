package com.example.appReceitas.service;

import com.example.appReceitas.model.Receita;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ReceitaService {

    public String salvarReceita(Receita receita) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document();
        receita.setId(docRef.getId());
        docRef.set(receita);
        return "Receita criada com sucesso!";
    }

    public List<Receita> listarReceitas() throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<Receita> receitas = new ArrayList<>();
        List<QueryDocumentSnapshot> documentos = db.collection("receitas").get().get().getDocuments();

        for (DocumentSnapshot doc : documentos) {
            Receita receita = doc.toObject(Receita.class);
            if (receita != null) {
                receita.setId(doc.getId());
                receitas.add(receita);
            }
        }
        return receitas;
    }

    public Receita buscarReceitaPorId(String id) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("receitas").document(id).get().get();
        Receita receita = doc.toObject(Receita.class);
        if (receita != null) {
            receita.setId(doc.getId());
        }
        return receita;
    }

    public String atualizarReceita(String id, Receita receita, String autorId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document(id);

        // Atualiza os campos editáveis
        docRef.update(
                "titulo", receita.getTitulo(),
                "ingredientes", receita.getIngredientes(),
                "modoPreparo", receita.getModoPreparo(),
                "dicas", receita.getDicas(),
                "privado", receita.isPrivado()
        );

        return "Receita atualizada com sucesso!";
    }

    public String deletarReceita(String id, String autorId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("receitas").document(id).delete();
        return "Receita deletada com sucesso!";
    }

    public Receita darLike(String id) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document(id);
        docRef.update("likes", FieldValue.increment(1));
        return buscarReceitaPorId(id);
    }

    public Receita removerLike(String id) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document(id);
        docRef.update("likes", FieldValue.increment(-1));
        return buscarReceitaPorId(id);
    }

    public Receita adicionarView(String id) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document(id);
        docRef.update("visualizacoes", FieldValue.increment(1));
        return buscarReceitaPorId(id);
    }

    // ✅ POPULARES COM FILTRO DE PRIVADAS
    public List<Receita> listarReceitasPopulares() throws Exception {
    Firestore db = FirestoreClient.getFirestore();
    List<Receita> receitas = new ArrayList<>();

    List<QueryDocumentSnapshot> documentos = db.collection("receitas")
            .whereEqualTo("privado", false)  // ✅ Só públicas
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

    // ✅ LISTA TODAS AS RECEITAS PÚBLICAS (se você usar em algum lugar)
    public List<QueryDocumentSnapshot> listarReceitasPublicas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("receitas")
                .whereEqualTo("privado", false)
                .get()
                .get()
                .getDocuments();
    }

    // ✅ PESQUISA COM FILTRO DE PRIVADAS (caso chame daqui)
    public List<Receita> pesquisarReceitasPublicas(String titulo) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<Receita> resultado = new ArrayList<>();

        List<QueryDocumentSnapshot> documentos = db.collection("receitas")
                .whereEqualTo("privado", false)
                .get()
                .get()
                .getDocuments();

        for (DocumentSnapshot doc : documentos) {
            Receita receita = doc.toObject(Receita.class);
            if (receita != null && receita.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                receita.setId(doc.getId());
                resultado.add(receita);
            }
        }
        return resultado;
    }
}
