package com.example.appReceitas.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-config.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            System.out.println("Firebase inicializado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao inicializar Firebase: " + e.getMessage());
        }
    }
}
