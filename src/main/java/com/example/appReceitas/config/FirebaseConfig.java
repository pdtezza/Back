package com.example.appReceitas.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            String firebaseConfig = System.getenv("FIREBASE_CONFIG_JSON");

            if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                throw new RuntimeException("FIREBASE_CONFIG_JSON não configurado!");
            }

            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado com sucesso!");
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao inicializar Firebase: " + e.getMessage());
        }
    }
}
