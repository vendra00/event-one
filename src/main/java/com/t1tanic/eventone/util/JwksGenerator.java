package com.t1tanic.eventone.util;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

// config/JwksGenerator.java
@Component
public final class JwksGenerator {
    private static KeyPair keyPair;
    public static KeyPair generateRsa() {
        if (keyPair != null) return keyPair;
        try {
            var kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            keyPair = kpg.generateKeyPair();
            return keyPair;
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    public static KeyPair getCurrentKeyPair() { return keyPair; }
}

