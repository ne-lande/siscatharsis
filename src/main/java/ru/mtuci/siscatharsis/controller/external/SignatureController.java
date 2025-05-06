package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.signature.SignatureFetchUUIDrequest;
import ru.mtuci.siscatharsis.model.Signature;
import ru.mtuci.siscatharsis.services.SignatureService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signature")
@RequiredArgsConstructor
public class SignatureController {
        private final SignatureService signatureService;
        private final ApiConstructor apiConstructor;

        @GetMapping("/all")
        public ResponseEntity<?> fetchAll() {
                List<Signature> response = signatureService.getAll();

                return apiConstructor.success(response);
        }

        @GetMapping("/diff/{dateTime}")
        public ResponseEntity<?> fetchDiff(@PathVariable Instant dateTime) {
                List<Signature> response = signatureService.getDiff(dateTime);

                return apiConstructor.success(response);
        }

        @PostMapping("/guid")
        public ResponseEntity<?> fetchByGuids(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                List<UUID> guidList = signatureFetchUUIDrequest.uuidList();

                List<Signature> response = signatureService.getByUUIDS(guidList);

                return apiConstructor.success(response);
        }

        @GetMapping(value = "/download", produces = MediaType.MULTIPART_MIXED_VALUE)
        public ResponseEntity<?> download(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                List<UUID> guidList = signatureFetchUUIDrequest.uuidList();

                List<Signature> signatureList = signatureService.getByUUIDS(guidList);

                return apiConstructor.success(signatureList);
        }
}
