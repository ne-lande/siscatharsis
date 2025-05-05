package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchDiff;
import ru.mtuci.siscatharsis.dto.external.sign.request.SignatureFetchUUIDrequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.CryptoService;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signature")
@RequiredArgsConstructor
public class SignatureController {
        private final SignatureService signatureService;
        private final UserService userService;
        private final CryptoService cryptoService;

        @GetMapping("/all")
        public ResponseEntity<?> fetchAll() {
                List<Signature> response = signatureService.getAll();

                return ApiMessage.Success(response);
        }

        @GetMapping("/diff")
        public ResponseEntity<?> fetchDiff(@Valid @RequestBody SignatureFetchDiff signatureFetchDiff) {
                Instant dateTime = signatureFetchDiff.getDateTime();

                List<Signature> response = signatureService.getDiff(dateTime);

                return ApiMessage.Success(response);
        }

        @PostMapping("/guid")
        public ResponseEntity<?> fetchByGuids(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                List<UUID> guidList = signatureFetchUUIDrequest.getUuidList();

                List<Signature> response = signatureService.getByUUIDS(guidList);

                return ApiMessage.Success(response);
        }

        @GetMapping(value = "/download", produces = MediaType.MULTIPART_MIXED_VALUE)
        public ResponseEntity<?> download(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) throws Exception {
                List<UUID> guidList = signatureFetchUUIDrequest.getUuidList();

                List<Signature> signatureList = signatureService.getByUUIDS(guidList);

                return ApiMessage.Success(signatureList);
        }
}
