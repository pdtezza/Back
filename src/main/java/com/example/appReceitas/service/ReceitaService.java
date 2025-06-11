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

    private static final String COLLECTION = "receitas";

    public String salvarReceita(Receita receita) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document();
        receita.setId(docRef.getId());
        ApiFuture<WriteResult> future = docRef.set(receita);
        future.get();
        return "Receita salva!";
    }

    public Receita buscarReceitaPorId(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
        return doc.toObject(Receita.class);
    }

    public List<Receita> listarReceitas() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Receita> receitas = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            receitas.add(doc.toObject(Receita.class));
        }
        return receitas;
    }

    public String atualizarReceita(String id, Receita receita, String autorId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document(id);
        Receita atual = docRef.get().get().toObject(Receita.class);
        if (atual == null) return "Receita não encontrada!";
        if (!atual.getAutorId().equals(autorId)) return "Só o autor pode editar!";
        receita.setId(id);
        receita.setAutorId(autorId);
        docRef.set(receita);
        return "Receita atualizada!";
    }

    public String deletarReceita(String id, String autorId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document(id);
        Receita atual = docRef.get().get().toObject(Receita.class);
        if (atual == null) return "Receita não encontrada!";
        if (!atual.getAutorId().equals(autorId)) return "Só o autor pode deletar!";
        docRef.delete();
        return "Receita deletada!";
    }

    public List<QueryDocumentSnapshot> listarReceitasPublicas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION)
                                           .whereEqualTo("privado", false)
                                           .get();
        return query.get().getDocuments();
    }
}
