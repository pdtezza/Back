package com.example.appReceitas.service;

import org.springframework.stereotype.Service;

import com.example.appReceitas.model.Cliente;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ClienteService {

    private static final String COLLECTION = "clientes";
    private final Firestore db = FirestoreClient.getFirestore();

    public String cadastrarClienteComEmailSenha(Cliente cliente, String senha) {
        try {
            CreateRequest request = new CreateRequest()
                    .setEmail(cliente.getEmail())
                    .setPassword(senha)
                    .setDisplayName(cliente.getNome());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return "Cliente criado com sucesso: " + userRecord.getUid();
        } catch (FirebaseAuthException e) {
            return "Erro ao cadastrar cliente: " + e.getMessage();
        }
    }

    public String enviarLinkRedefinicaoSenha(String email) {
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(email);
            return "Link enviado: " + link;
        } catch (FirebaseAuthException e) {
            return "Erro ao enviar link: " + e.getMessage();
        }
    }
}
