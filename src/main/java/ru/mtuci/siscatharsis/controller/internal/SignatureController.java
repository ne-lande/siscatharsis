package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureCreateRequest;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureDeleteRequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.util.UUID;

@RestController
@RequestMapping("/admin/signature")
public class SignatureController {
        private final SignatureService signatureService;
        private final UserService userService;

        @Autowired
        SignatureController(SignatureService signatureService, UserService userService) {
                this.signatureService = signatureService;
                this.userService = userService;
        }

        @PostMapping("/add")
        public ResponseEntity<?> addSignature(Authentication authentication, @Valid @RequestBody SignatureCreateRequest signatureCreateRequest) {
                String username = authentication.getName();
                Long userId = userService.findByLogin(username).getId();

                Signature response = signatureService.create(signatureCreateRequest, userId);

                return ApiMessage.Success(response);
        }

        @PostMapping("/delete")
        public ResponseEntity<?> markSignatureAsDelete(@Valid @RequestBody SignatureDeleteRequest signatureDeleteRequest) {
                UUID guid = signatureDeleteRequest.getGuid();

                Signature response = signatureService.markSignature(guid, Signature.Status.DELETED);

                return ApiMessage.Success(response);
        }
}
