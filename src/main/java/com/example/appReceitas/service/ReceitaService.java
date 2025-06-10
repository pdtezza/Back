package com.example.appReceitas.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.appReceitas.model.Receita;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ReceitaService {

    private static final String COLLECTION = "receitas";

    // Salva uma receita no Firestore
    public String salvarReceita(Receita receita) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document();
        ApiFuture<WriteResult> future = docRef.set(receita);
        return "Receita salva com sucesso em: " + future.get().getUpdateTime();
    }

    // Exemplo: método para buscar uma receita por ID
    public Receita buscarReceitaPorId(String id) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document(id);
        return docRef.get().get().toObject(Receita.class);
    }

    // Exemplo: método para listar todas as receitas (básico)
    // (Você pode adaptar para paginação se quiser)
    /*
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
    */
}
