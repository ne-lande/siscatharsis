package ru.mtuci.siscatharsis.controller.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.signature.SignatureCreateRequest;
import ru.mtuci.siscatharsis.dto.signature.SignaturePatchRequest;
import ru.mtuci.siscatharsis.model.signature.Signature;
import ru.mtuci.siscatharsis.model.signature.SignatureAudit;
import ru.mtuci.siscatharsis.model.signature.SignatureHistory;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.signature.SignatureAuditService;
import ru.mtuci.siscatharsis.services.signature.SignatureHistoryService;
import ru.mtuci.siscatharsis.services.signature.SignatureService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/signature")
@RequiredArgsConstructor
public class AdminSignatureController {
        private final SignatureService signatureService;
        private final SignatureAuditService signatureAuditService;
        private final SignatureHistoryService signatureHistoryService;
        private final ResponseUtils responseUtils;

        @PostMapping("/")
        public ResponseEntity<?> addSignature(Authentication authentication, @Valid @RequestBody SignatureCreateRequest signatureCreateRequest) throws Exception {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

                Signature signature = Signature.builder()
                        .threatName(signatureCreateRequest.threatName())
                        .firstBytes(signatureCreateRequest.firstBytes())
                        .remainderHash(signatureCreateRequest.remainderHash())
                        .remainderLength(signatureCreateRequest.remainderLength())
                        .fileType(signatureCreateRequest.fileType())
                        .offsetStart(signatureCreateRequest.offsetStart())
                        .offsetEnd(signatureCreateRequest.offsetEnd())
                        .updatedAt(Instant.now())
                        .build();

                signatureService.create(signature, userId);

                return responseUtils.success(signature);
        }

        @DeleteMapping("/{guid}")
        public ResponseEntity<?> markSignatureAsDelete(Authentication authentication, @PathVariable UUID guid) {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

                Signature response = signatureService.deleteSignature(guid, userId);

                return responseUtils.success(response);
        }

        // TODO: implement
        @PatchMapping("/{guid}")
        public ResponseEntity<?> patchSignature(Authentication authentication, @PathVariable UUID guid, @Valid @RequestBody SignaturePatchRequest signaturePatchRequest) throws Exception {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

                Signature response = signatureService.patch(guid, signaturePatchRequest, userId);

                return responseUtils.success(response);
        }

        @GetMapping("/audit/{guid}")
        public ResponseEntity<?> getAuditFor(@PathVariable UUID guid) {
                Signature signature = signatureService.requireById(guid);

                List<SignatureAudit> response = signatureAuditService.getFor(signature);

                return responseUtils.success(response);
        }

        @GetMapping("/history/{guid}")
        public ResponseEntity<?> getHistoryFor(@PathVariable UUID guid) {
                Signature signature = signatureService.requireById(guid);

                List<SignatureHistory> response = signatureHistoryService.getFor(signature);

                return responseUtils.success(response);
        }
}
