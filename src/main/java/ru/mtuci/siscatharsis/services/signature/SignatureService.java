package ru.mtuci.siscatharsis.services.signature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.signature.SignaturePatchRequest;
import ru.mtuci.siscatharsis.model.signature.Signature;
import ru.mtuci.siscatharsis.model.signature.SignatureAudit;
import ru.mtuci.siscatharsis.repositories.signature.SignatureRepository;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.utils.MediaUtil;
import ru.mtuci.siscatharsis.utils.exceptions.EntityNotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO: Выделить мердж свойств класса-объекта в хелпер метод?
// TODO: Универсальный метод для создания вхождения в таблицу аудита
// TODO: Выделить аудит и историю в отдельные сервисы
@Service
@RequiredArgsConstructor
public class SignatureService {
        private final CryptoService cryptoService;
        private final SignatureAuditService signatureAuditService;
        private final SignatureHistoryService signatureHistoryService;
        private final SignatureRepository signatureRepository;

        public void checkDigitalSignature(Signature signature) {
                try {
                        byte[] digitalSignature = signature.getDigitalSignature();

                        byte[] evaluatedDigitalSignature = cryptoService.signWithCurrent(signature.getBodyForSigning());

                        signatureHistoryService.create(signature);

                        if (Arrays.equals(evaluatedDigitalSignature, digitalSignature)) {
                                signature.setStatus(Signature.Status.ACTUAL);
                                signatureAuditService.create(signature.getId(), null, SignatureAudit.ChangeType.ACTUAL, List.of("status"));
                        } else {
                                signature.setStatus(Signature.Status.CORRUPTED);
                                signatureAuditService.create(signature.getId(), null, SignatureAudit.ChangeType.CORRUPT, List.of("status"));
                        }

                        signatureRepository.save(signature);
                } catch (Exception ignored) {

                }
        }

        private void appendDigitalSignature(Signature signature) throws Exception {
                byte[] digitalSignature = cryptoService.signWithCurrent(signature.getBodyForSigning());

                signature.setDigitalSignature(digitalSignature);

                signature.setStatus(Signature.Status.ACTUAL);
        }

        public Signature requireById(UUID id) {
                return signatureRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Signature not found")
                );
        }

        @SuppressWarnings("UnusedReturnValue")
        public Signature create(Signature signature, Long issuerId) throws Exception {

                appendDigitalSignature(signature);

                signatureRepository.save(signature);
                signatureAuditService.create(signature.getId(), issuerId, SignatureAudit.ChangeType.CREATE, null);

                return signature;
        }

        public Signature patch(UUID guid, SignaturePatchRequest newSignature, Long issuerId) throws Exception {
                Signature signature = requireById(guid);

                signatureHistoryService.create(signature);

                Map<String, Object> updatedFields = MediaUtil.merge(signature, newSignature);
                appendDigitalSignature(signature);

                signatureRepository.save(signature);

                signatureAuditService.create(signature.getId(), issuerId, SignatureAudit.ChangeType.UPDATE, updatedFields.keySet().stream().toList());

                return signature;
        }

        public List<Signature> getAll() {
                return signatureRepository.findAll();
        }

        public List<Signature> getDiff(Instant timestamp) {
                return signatureRepository.findAllByUpdatedAtAfter(timestamp);
        }

        public List<Signature> getByUUIDS(Iterable<UUID> iterable) {
                return signatureRepository.findAllById(iterable);
        }

        public Signature deleteSignature(UUID guid, Long issuerId) {
                Signature signature = requireById(guid);

                signatureHistoryService.create(signature);

                signature.setStatus(Signature.Status.DELETED);
                signatureRepository.save(signature);

                signatureAuditService.create(signature.getId(), issuerId, SignatureAudit.ChangeType.DELETE, List.of("status"));

                return signature;
        }
}
