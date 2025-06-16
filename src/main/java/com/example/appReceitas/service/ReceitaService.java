package com.example.appReceitas.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.appReceitas.model.Receita;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ReceitaService {

    private static final String COLLECTION = "receitas";

    public String salvarReceita(Receita receita) throws Exception {
    Firestore db = FirestoreClient.getFirestore();

    // Se não tem id, é nova receita: gera id único
    if (receita.getId() == null || receita.getId().isEmpty()) {
        DocumentReference docRef = db.collection(COLLECTION).document(); // gera id
        receita.setId(docRef.getId());
        docRef.set(receita);
        return receita.getId();
    } else {
        db.collection(COLLECTION).document(receita.getId()).set(receita);
        return receita.getId();
    }
}

    public Receita buscarReceitaPorId(String id) throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
    Receita receita = doc.toObject(Receita.class);
    if (receita != null) {
        receita.setId(doc.getId());  // <--- ESSENCIAL!!!
    }
    return receita;
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
    public Receita darLike(String id) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("receitas").document(id);

        // Incrementa o campo likes diretamente
        docRef.update("likes", FieldValue.increment(1));
        // Busca a receita já atualizada para retornar
        Receita receitaAtualizada = docRef.get().get().toObject(Receita.class);
        return receitaAtualizada;
    }

    public Receita adicionarView(String id) throws Exception {
        Receita receita = buscarReceitaPorId(id);
        receita.setVisualizacoes(receita.getVisualizacoes() + 1);
        salvarReceita(receita); // Atualize no Firestore
        return receita;
    }
    public List<Receita> listarReceitasPopulares() throws Exception {
    List<Receita> todas = listarReceitas(); // já traz todas do banco
    todas.sort(Comparator.comparingInt(Receita::getLikes)
        .thenComparingInt(Receita::getVisualizacoes)
        .reversed());
    return todas;
    }
    public Receita removerLike(String id) throws Exception {
    Receita receita = buscarReceitaPorId(id);
    int likes = receita.getLikes();
    if (likes > 0) {
        receita.setLikes(likes - 1);
        salvarReceita(receita); // Salva a atualização no Firestore
    }
    return receita;
    }

}
