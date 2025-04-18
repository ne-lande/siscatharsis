package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchDiff;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchUUIDrequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/signature")
public class PublicSignatureController {
        private final SignatureService signatureService;
        private final UserService userService;

        @Autowired
        PublicSignatureController(SignatureService signatureService, UserService userService) {
                this.signatureService = signatureService;
                this.userService = userService;
        }

        @GetMapping("/all")
        public ResponseEntity<?> fetchAll() {
                List<Signature> response = signatureService.getAll();

                return ApiMessage.Success(response);
        }

        @GetMapping("/diff")
        public ResponseEntity<?> fetchDiff(@Valid @RequestBody SignatureFetchDiff signatureFetchDiff) {
                List<Signature> response = signatureService.getDiff(signatureFetchDiff);

                return ApiMessage.Success(response);
        }

        @PostMapping("/guid")
        public ResponseEntity<?> fetchByGuids(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                List<Signature> response = signatureService.getByUUIDS(signatureFetchUUIDrequest);

                return ApiMessage.Success(response);
        }
}
