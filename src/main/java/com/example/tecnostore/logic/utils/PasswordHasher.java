package com.example.tecnostore.logic.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    
    /*
     * SECCIÓN CRÍTICA DE SEGURIDAD:
     * - Aquí se calcula el 'hash' de la contraseña.
     * - Riesgo: usar solo SHA-256 + Base64 no es lo ideal para contraseñas porque
     *   es rápido de calcular y eso facilita ataques por fuerza bruta.
     * - Recomendación (no técnica): usar una librería especializada como BCrypt o Argon2
     *   que protege mejor las contraseñas y maneja 'salt' automáticamente.
     */
    
    public static String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }
}