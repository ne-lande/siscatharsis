package ru.mtuci.siscatharsis.utils;

import java.security.*;
import java.util.Base64;

public class CryptoUtil {
    public static SignatureInstance sign(String input) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        // Hash the input and update the signature object
        signature.update(input.getBytes());

        // Sign the data
        byte[] signedBytes = signature.sign();

        return new SignatureInstance(
            Base64.getEncoder().encodeToString(signedBytes),
            Base64.getEncoder().encodeToString(publicKey.getEncoded())
        );
    }
}
