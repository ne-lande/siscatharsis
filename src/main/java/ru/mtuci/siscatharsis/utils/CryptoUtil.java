package ru.mtuci.siscatharsis.utils;

import java.security.*;
import java.util.Base64;

public class CryptoUtil {

    public static KeyPair generateNewKeypair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        return keyPairGen.generateKeyPair();
    }

    public static String sign(PrivateKey privateKey, String input) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(input.getBytes());
        byte[] signedBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signedBytes);
    }
}
