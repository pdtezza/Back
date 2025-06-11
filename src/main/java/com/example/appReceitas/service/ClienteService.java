package com.example.appReceitas.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.appReceitas.model.Cliente;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ClienteService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String COLLECTION = "clientes";

    // Cadastro no Firebase Auth
    public String cadastrarClienteComEmailSenha(Cliente cliente, String senha) {
        try {
            CreateRequest request = new CreateRequest()
                    .setEmail(cliente.getEmail())
                    .setPassword(senha)
                    .setDisplayName(cliente.getNome());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Salva também no Firestore após criar no Auth
            cliente.setId(userRecord.getUid());
            salvarClienteNoFirestore(cliente);

            return "Cliente criado com sucesso: " + userRecord.getUid();
        } catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
            return "Erro ao cadastrar cliente: " + e.getMessage();
        }
    }

    // Salva o cliente no Firestore
    public void salvarClienteNoFirestore(Cliente cliente) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document(cliente.getId());
        ApiFuture<WriteResult> future = docRef.set(cliente);
        future.get(); // Aguarda salvar
    }

    // Envio de link de redefinição de senha por e-mail
    public String enviarLinkRedefinicaoSenha(String email) {
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(email);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Redefinição de senha - App Receitas");
            message.setText("Clique no link para redefinir sua senha:\n\n" + link);

            mailSender.send(message);

            return "Link de redefinição enviado para: " + email;
        } catch (FirebaseAuthException e) {
            return "Erro do Firebase: " + e.getMessage();
        } catch (MailException e) {
            return "Erro ao enviar e-mail: " + e.getMessage();
        } catch (Exception e) {
            return "Erro inesperado: " + e.getMessage();
        }
    }
    public String favoritarReceita(String clienteId, String receitaId) throws Exception {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("clientes").document(clienteId);
    docRef.update("receitasFavoritas", FieldValue.arrayUnion(receitaId));
    return "Receita favoritada!";
    }

    public String desfavoritarReceita(String clienteId, String receitaId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);
        docRef.update("receitasFavoritas", FieldValue.arrayRemove(receitaId));
        return "Receita removida dos favoritos!";
    }

}
