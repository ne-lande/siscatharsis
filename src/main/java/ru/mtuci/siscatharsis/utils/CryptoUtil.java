package ru.mtuci.siscatharsis.utils;

import java.security.*;

public class CryptoUtil {

    public static KeyPair generateNewKeypair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        return keyPairGen.generateKeyPair();
    }

    public static byte[] sign(PrivateKey privateKey, byte[] byteArray) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(byteArray);
        return signature.sign();
    }
}
