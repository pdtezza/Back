package com.example.appReceitas.service;

import com.example.appReceitas.model.Receita;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ReceitaService {

    private final Firestore firestore = FirestoreClient.getFirestore();

    public String salvarReceita(Receita receita) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("receitas").document().set(receita);
        return future.get().getUpdateTime().toString();
    }
}
