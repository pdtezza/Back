package com.example.appReceitas.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class FirebaseTokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                request.setAttribute("firebaseUid", uid); // ESSENCIAL!!
                System.out.println("DEBUG FirebaseTokenFilter uid=" + uid);
            } catch (Exception e) {
                // Se token inválido, pode lançar exception ou ignorar
                System.out.println("Token inválido: " + e.getMessage());
            }
        }
        chain.doFilter(req, res);
    }
}
