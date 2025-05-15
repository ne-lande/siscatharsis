package ru.mtuci.siscatharsis.services.signature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.signature.Signature;
import ru.mtuci.siscatharsis.model.signature.SignatureHistory;
import ru.mtuci.siscatharsis.repositories.signature.SignatureHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureHistoryService {
        private final SignatureHistoryRepository signatureHistoryRepository;

        public List<SignatureHistory> getFor(Signature signature) {
                return signatureHistoryRepository.findBySignatureId(signature.getId());
        }

        @SuppressWarnings("UnusedReturnValue")
        public SignatureHistory create(Signature signature) {
                SignatureHistory signatureHistory = SignatureHistory.builder()
                        .signatureId(signature.getId())
                        .threatName(signature.getThreatName())
                        .firstBytes(signature.getFirstBytes())
                        .remainderHash(signature.getRemainderHash())
                        .remainderLength(signature.getRemainderLength())
                        .fileType(signature.getFileType())
                        .offsetStart(signature.getOffsetStart())
                        .offsetEnd(signature.getOffsetEnd())
                        .digitalSignature(signature.getDigitalSignature())
                        .status(signature.getStatus())
                        .updatedAt(signature.getUpdatedAt())
                        .build();

                signatureHistoryRepository.save(signatureHistory);

                return signatureHistory;
        }
}
