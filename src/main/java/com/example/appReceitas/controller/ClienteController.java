package com.example.appReceitas.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.appReceitas.model.Cliente;
import com.example.appReceitas.model.Receita;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.storage.Blob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    @Autowired
    private JavaMailSender mailSender;

    // 1. Ver perfil do cliente logado
    @PostMapping("/cadastrar")
public String cadastrarCliente(@RequestBody Cliente novoCliente) throws Exception {
    // 1. Cadastra usuário no Auth (Firebase)
    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
            .setEmail(novoCliente.getEmail())
            .setPassword(novoCliente.getSenha())  
            .setDisplayName(novoCliente.getNome());

    UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

    // 2. Cria documento no Firestore
    Firestore db = FirestoreClient.getFirestore();
    // Não salve a senha no Firestore!
    novoCliente.setSenha(null);
    novoCliente.setId(userRecord.getUid());

    if (novoCliente.getReceitasFavoritas() == null) {
        novoCliente.setReceitasFavoritas(new ArrayList<>());
    }
    
    db.collection("clientes").document(userRecord.getUid()).set(novoCliente);


    return "Cliente cadastrado com sucesso! UID: " + userRecord.getUid();
    } 
      
    @GetMapping("/{usuarioId}")
    public Cliente getClientePorId(@PathVariable String usuarioId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(usuarioId);
        Cliente cliente = docRef.get().get().toObject(Cliente.class);
        if (cliente != null) {
            cliente.setId(docRef.getId());
            return cliente;
        } else {
            throw new Exception("Cliente não encontrado");
        }
    }
   
    @GetMapping("/perfil")
    public Cliente getPerfil(HttpServletRequest request) throws Exception {
        String clienteId = (String) request.getAttribute("firebaseUid");
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);
        Cliente cliente = docRef.get().get().toObject(Cliente.class);
        if (cliente == null) throw new Exception("Cliente não encontrado!");
        return cliente;
    }

    // 2. Editar nome ou email
    @PutMapping("/perfil")
    public String editarPerfil(@RequestBody Cliente dados, HttpServletRequest request) throws Exception {
        String clienteId = (String) request.getAttribute("firebaseUid");
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);

        // Apenas atualiza nome e email, se enviados
        if (dados.getNome() != null) {
            docRef.update("nome", dados.getNome());
        }
        if (dados.getEmail() != null) {
            docRef.update("email", dados.getEmail());
            // (Opcional) Atualize também no Firebase Auth:
            // FirebaseAuth.getInstance().updateUser(new UserRecord.UpdateRequest(clienteId).setEmail(dados.getEmail()));
        }
        return "Perfil atualizado!";
    }

    // 3. Listar receitas favoritas do cliente
    @GetMapping("/favoritos")
    public List<String> getFavoritos(HttpServletRequest request) throws Exception {
        String clienteId = (String) request.getAttribute("firebaseUid");
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);
        Cliente cliente = docRef.get().get().toObject(Cliente.class);
        if (cliente != null && cliente.getReceitasFavoritas() != null) {
            return cliente.getReceitasFavoritas();
        }
        return new ArrayList<>();
    }

    @DeleteMapping("/perfil")
    public String deletarPerfil(HttpServletRequest request) throws Exception {
        String clienteId = (String) request.getAttribute("firebaseUid");
        // Remove do Firestore
        Firestore db = FirestoreClient.getFirestore();
        db.collection("clientes").document(clienteId).delete();
        // Remove do Auth
        FirebaseAuth.getInstance().deleteUser(clienteId);
        return "Conta excluída com sucesso!";
    }


    @PostMapping("/esqueci-senha")
    public String esqueciSenha(@RequestParam String email) {
        try {
            String link = com.google.firebase.auth.FirebaseAuth.getInstance()
                .generatePasswordResetLink(email);

            // Monta o e-mail
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Redefinição de Senha - App de Receitas");
            message.setText("Olá!\n\nClique no link abaixo para redefinir sua senha:\n" + link);

            // Envia o e-mail
            mailSender.send(message);

            return "E-mail de redefinição de senha enviado!";
        } catch (Exception e) {
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }

    @PostMapping("/upload-foto")
    public String uploadFotoPerfil(@RequestParam("foto") MultipartFile foto, HttpServletRequest request) throws Exception {
        String clienteId = (String) request.getAttribute("firebaseUid");

        // Nome do arquivo: perfil_CLIENTEID.ext
        String extensao = org.apache.commons.io.FilenameUtils.getExtension(foto.getOriginalFilename());
        String nomeArquivo = "perfil_" + clienteId + "." + extensao;

        // Upload para Firebase Storage
        Blob blob = StorageClient.getInstance().bucket().create(nomeArquivo, foto.getBytes(), foto.getContentType());

        // Gerar URL pública (ajuste as regras de Storage para permitir leitura pública do perfil)
        String url = "https://firebasestorage.googleapis.com/v0/b/" +
                StorageClient.getInstance().bucket().getName() +
                "/o/" + java.net.URLEncoder.encode(nomeArquivo, "UTF-8") + "?alt=media";

        // Salva a URL no perfil do cliente
        Firestore db = FirestoreClient.getFirestore();
        db.collection("clientes").document(clienteId).update("fotoPerfil", url);

        return "Foto de perfil enviada com sucesso! URL: " + url;
    }

    @GetMapping("/favoritas")
    public List<Receita> getReceitasFavoritas(@RequestParam String clienteId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("clientes").document(clienteId);
        Cliente cliente = docRef.get().get().toObject(Cliente.class);

        List<Receita> favoritas = new ArrayList<>();
        if (cliente != null && cliente.getReceitasFavoritas() != null) {
            for (String receitaId : cliente.getReceitasFavoritas()) {
                DocumentReference receitaRef = db.collection("receitas").document(receitaId);
                DocumentSnapshot snap = receitaRef.get().get();
                Receita receita = snap.toObject(Receita.class);
                if (receita != null) {
                    receita.setId(snap.getId()); // Garante que o campo id está certo
                    favoritas.add(receita);
                }
            }
        }
        return favoritas;
    }
    @GetMapping("/por-email")
    public Cliente getClientePorEmail(@RequestParam String email) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documentos = db.collection("clientes")
            .whereEqualTo("email", email)
            .get()
            .get()
            .getDocuments();

        if (!documentos.isEmpty()) {
            DocumentSnapshot doc = documentos.get(0);
            Cliente cliente = doc.toObject(Cliente.class);
            if (cliente != null) {
                cliente.setId(doc.getId());  // <<< ESSENCIAL: devolve o ID do documento (usuarioId)
                return cliente;
            }
        }
        throw new Exception("Usuário não encontrado com esse email.");
    }

}
