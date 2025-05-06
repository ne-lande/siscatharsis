package ru.mtuci.siscatharsis.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.services.SignatureService;

@Component
@RequiredArgsConstructor
public class SignatureCheck {
        private final SignatureService signatureService;

        @Scheduled(cron = "0 0 1 * *")
        public void checkSignatures() {
                signatureService.getAll()
                        .forEach(signatureService::checkDigitalSignature);
        }
}
