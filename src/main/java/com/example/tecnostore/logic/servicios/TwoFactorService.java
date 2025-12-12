package com.example.tecnostore.logic.servicios;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class TwoFactorService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public GoogleAuthenticatorKey generarSecretKey() {
        return gAuth.createCredentials();
    }

    public String getSecret(GoogleAuthenticatorKey key) {
        return key.getKey();
    }

    public String generarOtpAuthUrl(String usuario, String appName, String secret) {
        // Formato estándar otpauth:
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&digits=6",
                appName, usuario, secret, appName
        );
    }
    
    public boolean validarCodigo(String secret, int codigo) {
        return gAuth.authorize(secret, codigo);
    }

    // Alias para compatibilidad con controladores existentes
    public boolean verificarCodigo(String secret, int codigo) {
        return validarCodigo(secret, codigo);
    }
}
