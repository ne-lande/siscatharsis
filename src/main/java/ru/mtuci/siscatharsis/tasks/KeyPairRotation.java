package ru.mtuci.siscatharsis.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.utils.exceptions.EntityNotFoundException;

import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
@Slf4j
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
                } catch (EntityNotFoundException e) {
                        log.info("Looks like no keys are present");
                        generateNewKeypairSafely();
                }
        }

        private void generateNewKeypairSafely() {
                try {
                        cryptoService.generateNewKeypair();
                } catch (NoSuchAlgorithmException e) {
                        log.error("Assumption, wrong JDK: ", e);
                }
        }
}
