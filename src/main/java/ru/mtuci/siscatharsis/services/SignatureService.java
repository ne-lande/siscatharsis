package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureCreateRequest;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchDiff;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchUUIDrequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.model.SignatureAudit;
import ru.mtuci.siscatharsis.model.SignatureHistory;
import ru.mtuci.siscatharsis.repositories.SignatureAuditRepository;
import ru.mtuci.siscatharsis.repositories.SignatureHistoryRepository;
import ru.mtuci.siscatharsis.repositories.SignatureRepository;
import ru.mtuci.siscatharsis.utils.CryptoUtil;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class SignatureService {
        private final CryptoService cryptoService;
        private final SignatureRepository signatureRepository;
        private final SignatureAuditRepository signatureAuditRepository;
        private final SignatureHistoryRepository signatureHistoryRepository;

        @Autowired
        public SignatureService(CryptoService cryptoService, SignatureRepository signatureRepository, SignatureAuditRepository signatureAuditRepository, SignatureHistoryRepository signatureHistoryRepository) {
                this.cryptoService = cryptoService;
                this.signatureRepository = signatureRepository;
                this.signatureAuditRepository = signatureAuditRepository;
                this.signatureHistoryRepository = signatureHistoryRepository;
        }

        public void checkDigitalSignature(Signature signature) {
                String digitalSignature = signature.getDigitalSignature();

                Signature.Status evaluatedStatus = Signature.Status.CORRUPTED;;
                String evaluatedDigitalSignature;
                try {
                        evaluatedDigitalSignature = cryptoService.signWithCurrent(signature.toString());
                        if (evaluatedDigitalSignature.equals(digitalSignature)) {
                                evaluatedStatus = Signature.Status.ACTUAL;
                        }
                } catch (Exception e) {
                        evaluatedStatus = Signature.Status.CORRUPTED;
                }

                signature.setStatus(evaluatedStatus);
                signatureRepository.save(signature);
        }

        private Signature appendDigitalSignature(Signature signature) {
                String digitalSignature;
                try {
                        digitalSignature = cryptoService.signWithCurrent(signature.toString());
                } catch (Exception e) {
                        digitalSignature = "fail";
                }

                signature.setDigitalSignature(digitalSignature);

                return signature;
        }

        public Signature create(SignatureCreateRequest signatureCreateRequest, Long issuerId) {
                Signature signature = Signature.builder()
                        .threatName(signatureCreateRequest.getThreatName())
                        .firstBytes(signatureCreateRequest.getFirstBytes())
                        .remainderHash(signatureCreateRequest.getRemainderHash())
                        .remainderLength(signatureCreateRequest.getRemainderLength())
                        .fileType(signatureCreateRequest.getFileType())
                        .offsetStart(signatureCreateRequest.getOffsetStart())
                        .offsetEnd(signatureCreateRequest.getOffsetEnd())
                        .updatedAt(Instant.now())
                        .build();

                signature = appendDigitalSignature(signature);

                signatureRepository.save(signature);

                HashMap<String, Object> newFields = new HashMap<>();
                for (Field field : signature.getClass().getDeclaredFields()) {
                        try {
                                field.setAccessible(true);
                                newFields.put(field.getName(), field.get(signature));
                        } catch (IllegalAccessException e) {
                                // Пропускаем поля, которых нет в entity
                        }
                }

                SignatureAudit audit = SignatureAudit.builder()
                        .signatureId(signature.getId())
                        .changedBy(issuerId)
                        .changeType(SignatureAudit.ChangeType.UPDATED)
                        .changedAt(Instant.now())
                        .fieldsChanged(newFields.toString())
                        .build();

                signatureAuditRepository.save(audit);

                return signature;
        }

        public Signature patch(UUID guid, Signature newSignature, Long issuerId) {
                Signature signature = signatureRepository.findById(guid).orElseThrow(
                        () -> new RuntimeException("sss")
                );

                SignatureHistory history = SignatureHistory.fromSignature(signature);
                signatureHistoryRepository.save(history);

                Class<?> entityClass = signature.getClass();
                Class<?> updatesClass = newSignature.getClass();

                HashMap<String, Object> updatedFields = new HashMap<>();

                for (Field field : updatesClass.getDeclaredFields()) {
                        try {
                                field.setAccessible(true);
                                Object value = field.get(newSignature);
                                if (value != null) {
                                        String fieldName = field.getName();
                                        Field entityField = entityClass.getDeclaredField(fieldName);
                                        entityField.setAccessible(true);
                                        entityField.set(signature, value);

                                        updatedFields.put(fieldName, value);
                                }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                                // Пропускаем поля, которых нет в entity
                        }
                }

                signature = appendDigitalSignature(signature);
                signatureRepository.save(signature);

                SignatureAudit audit = SignatureAudit.builder()
                        .signatureId(signature.getId())
                        .changedBy(issuerId)
                        .changeType(SignatureAudit.ChangeType.UPDATED)
                        .changedAt(Instant.now())
                        .fieldsChanged(updatedFields.toString())
                        .build();

                signatureAuditRepository.save(audit);

                return signature;
        }

        public List<Signature> getAll() {
                return signatureRepository.findAll();
        }

        public List<Signature> getDiff(SignatureFetchDiff signatureFetchDiff) {
                return signatureRepository.findAllByUpdatedAtAfter(signatureFetchDiff.getDateTime());
        }

        public List<Signature> getByUUIDS(SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                Iterable<UUID> iter = signatureFetchUUIDrequest.getUuidList();
                return signatureRepository.findAllById(iter);
        }

        public Signature markSignature(UUID guid, Signature.Status status) {
                Signature signature = signatureRepository.findById(guid).orElseThrow(
                        () -> new RuntimeException("sex")
                );

                signature.setStatus(status);

                signatureRepository.save(signature);
                return signature;
        }
}
