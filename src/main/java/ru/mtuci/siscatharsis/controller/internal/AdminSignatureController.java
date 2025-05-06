package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.signature.SignatureCreateRequest;
import ru.mtuci.siscatharsis.dto.signature.SignaturePatchRequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/admin/signature")
@RequiredArgsConstructor
public class AdminSignatureController {
        private final SignatureService signatureService;
        private final ApiConstructor apiConstructor;

        @PostMapping("/")
        public ResponseEntity<?> addSignature(Authentication authentication, @Valid @RequestBody SignatureCreateRequest signatureCreateRequest) {
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

                return apiConstructor.success(signature);
        }

        @PostMapping("/{guid}")
        public ResponseEntity<?> markSignatureAsDelete(@PathVariable UUID guid) {
                Signature response = signatureService.markSignature(guid, Signature.Status.DELETED);

                return apiConstructor.success(response);
        }

        // TODO: implement
        @PatchMapping("/{guid}")
        public ResponseEntity<?> patchSignature(Authentication authentication, @PathVariable UUID guid, @Valid @RequestBody SignaturePatchRequest signaturePatchRequest) throws IllegalAccessException {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

                Signature response = signatureService.patch(guid, signaturePatchRequest, userId);

                return apiConstructor.success(response);
        }
}
