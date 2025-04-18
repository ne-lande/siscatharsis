package ru.mtuci.siscatharsis.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.services.SignatureService;

import java.util.List;

@Component
public class SignatureCheck {
        private final CryptoService cryptoService;
        private final SignatureService signatureService;

        @Autowired
        public SignatureCheck(SignatureService signatureService, CryptoService cryptoService) {
                this.signatureService = signatureService;
                this.cryptoService = cryptoService;
        }

        @Scheduled(cron = "0 0 1 * *")
        public void checkSignatures() {
                signatureService.getAll()
                        .stream()
                        .forEach(signatureService::checkDigitalSignature);
        }
}
