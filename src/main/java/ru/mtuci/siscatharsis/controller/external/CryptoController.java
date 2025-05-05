package ru.mtuci.siscatharsis.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CryptoController {

        private final CryptoService cryptoService;

        @GetMapping("/public-key/get")
        public ResponseEntity<?> fetchPublicKey() {
                Crypto crypto = cryptoService.getCurrentKeypair();

                String publicKeyBase64 = Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded());

                return ApiMessage.Success(publicKeyBase64);
        }

        @GetMapping("/key-pair/generate")
        public ResponseEntity<?> generateKeyPair() throws NoSuchAlgorithmException {
                Crypto crypto = cryptoService.generateNewKeypair();

                return ApiMessage.Success(crypto.getPublicKey());
        }
}
