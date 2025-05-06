package ru.mtuci.siscatharsis.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.services.CryptoService;

import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class KeyPairRotation {
        private final CryptoService cryptoService;

        @Scheduled(cron = "0 0 1 1 *")
        public void rotateKeyPairs() throws NoSuchAlgorithmException {
                cryptoService.generateNewKeypair();
        }

        @EventListener(ApplicationReadyEvent.class)
        public void checkForKeys() {
                try {
                        Crypto crypto = cryptoService.getCurrentKeypair();
                } catch (RuntimeException e) {
                        System.out.println("Looks like no keys are present");
                        try {
                                cryptoService.generateNewKeypair();
                        } catch (NoSuchAlgorithmException e2) {
                                System.out.println(e2);
                        }
                }
        }
}
