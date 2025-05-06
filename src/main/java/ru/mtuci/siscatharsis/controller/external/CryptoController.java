package ru.mtuci.siscatharsis.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class CryptoController {

        private final CryptoService cryptoService;
        private final ApiConstructor apiConstructor;

        @GetMapping("/public-key/get")
        public ResponseEntity<?> fetchPublicKey() {
                Crypto crypto = cryptoService.getCurrentKeypair();

                String publicKeyBase64 = Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded());

                return apiConstructor.success(publicKeyBase64);
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @GetMapping("/key-pair/generate")
        public ResponseEntity<?> generateKeyPair() throws NoSuchAlgorithmException {
                Crypto crypto = cryptoService.generateNewKeypair();

                return apiConstructor.success(crypto.getPublicKey());
        }
}
