package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignaturePatchRequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.model.SignatureAudit;
import ru.mtuci.siscatharsis.model.SignatureHistory;
import ru.mtuci.siscatharsis.repositories.SignatureAuditRepository;
import ru.mtuci.siscatharsis.repositories.SignatureHistoryRepository;
import ru.mtuci.siscatharsis.repositories.SignatureRepository;
import ru.mtuci.siscatharsis.utils.ObjectUtils;

import java.time.Instant;
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
        private final SignatureRepository signatureRepository;
        private final SignatureAuditRepository signatureAuditRepository;
        private final SignatureHistoryRepository signatureHistoryRepository;

        public void checkDigitalSignature(Signature signature) {
                String digitalSignature = signature.getDigitalSignature();

                Signature.Status evaluatedStatus;
                try {
                        String evaluatedDigitalSignature = cryptoService.signWithCurrent(signature.toString());
                        if (evaluatedDigitalSignature.equals(digitalSignature)) {
                                evaluatedStatus = Signature.Status.ACTUAL;
                        } else {
                                evaluatedStatus = Signature.Status.CORRUPTED;
                        }
                } catch (Exception e) {
                        evaluatedStatus = Signature.Status.CORRUPTED;
                }

                signature.setStatus(evaluatedStatus);
                signatureRepository.save(signature);
        }

        private void appendDigitalSignature(Signature signature) {
                String digitalSignature;
                try {
                        digitalSignature = cryptoService.signWithCurrent(signature.toString());
                } catch (Exception e) {
                        digitalSignature = null;
                }

                signature.setDigitalSignature(digitalSignature);
                signature.setStatus(Signature.Status.ACTUAL);
        }

        public Signature requireById(UUID id) {
                return signatureRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("Signature not found")
                );
        }
        public Signature create(Signature signature, Long issuerId) {

                appendDigitalSignature(signature);

                signatureRepository.save(signature);

                Map<String, Object> newFields = ObjectUtils.image(new Signature(), signature);

                createAuditRecord(signature.getId(), issuerId, SignatureAudit.ChangeType.CREATED, newFields);

                return signature;
        }

        public Signature patch(UUID guid, SignaturePatchRequest newSignature, Long issuerId) throws IllegalAccessException {
                Signature signature = requireById(guid);

                SignatureHistory history = SignatureHistory.fromSignature(signature);
                signatureHistoryRepository.save(history);

                Map<String, Object> updatedFields = ObjectUtils.merge(signature, newSignature);

                appendDigitalSignature(signature);
                signatureRepository.save(signature);

                createAuditRecord(signature.getId(), issuerId, SignatureAudit.ChangeType.UPDATED, updatedFields);

                return signature;
        }

        private void createAuditRecord(UUID signatureId, Long issuerId, SignatureAudit.ChangeType changeType, Map<String, Object> changedFields) {
                SignatureAudit audit = SignatureAudit.builder()
                        .signatureId(signatureId)
                        .changedBy(issuerId)
                        .changeType(changeType)
                        .changedAt(Instant.now())
                        .fieldsChanged(changedFields.toString())
                        .build();

                signatureAuditRepository.save(audit);
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

        public Signature markSignature(UUID guid, Signature.Status status) {
                Signature signature = requireById(guid);

                signature.setStatus(status);

                signatureRepository.save(signature);
                return signature;
        }
}
