package ru.mtuci.siscatharsis.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
public class CryptoController {

        private final CryptoService cryptoService;
        private final ResponseUtils responseUtils;

        @GetMapping("/public-key/get")
        public ResponseEntity<?> fetchPublicKey() {
                Crypto crypto = cryptoService.getCurrentKeypair();

                String publicKeyBase64 = Base64.getEncoder().encodeToString(crypto.getPublicKey().getEncoded());

                return responseUtils.success(publicKeyBase64);
        }

        @SecurityRequirement(name = "bearerAuth")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @GetMapping("/key-pair/generate")
        public ResponseEntity<?> generateKeyPair() throws NoSuchAlgorithmException {
                Crypto crypto = cryptoService.generateNewKeypair();

                return responseUtils.success(crypto.getPublicKey());
        }
}
