package ru.mtuci.siscatharsis.tasks;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.services.signature.SignatureService;

@Component
@RequiredArgsConstructor
public class SignatureCheck {
        private final SignatureService signatureService;

        @SneakyThrows
        @Scheduled(cron = "0 0 1 * *")
        public void checkSignatures() {
                signatureService.getAll()
                        .forEach(signatureService::checkDigitalSignature);
        }
}
