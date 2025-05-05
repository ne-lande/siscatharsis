package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureCreateRequest;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureDeleteRequest;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignaturePatchRequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/admin/signature")
@RequiredArgsConstructor
public class AdminSignatureController {
        private final SignatureService signatureService;

        @PostMapping("/")
        public ResponseEntity<?> addSignature(Authentication authentication, @Valid @RequestBody SignatureCreateRequest signatureCreateRequest) {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

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

                signatureService.create(signature, userId);

                return ApiMessage.Success(signature);
        }

        @PostMapping("/{guid}")
        public ResponseEntity<?> markSignatureAsDelete(@PathVariable UUID guid) {
                Signature response = signatureService.markSignature(guid, Signature.Status.DELETED);

                return ApiMessage.Success(response);
        }

        // TODO: implement
        @PatchMapping("/{guid}")
        public ResponseEntity<?> patchSignature(Authentication authentication, @PathVariable UUID guid, @Valid @RequestBody SignaturePatchRequest signaturePatchRequest) throws IllegalAccessException {
                User user = (User) authentication.getPrincipal();
                Long userId = user.getId();

                Signature response = signatureService.patch(guid, signaturePatchRequest, userId);

                return ApiMessage.Success(response);
        }
}
