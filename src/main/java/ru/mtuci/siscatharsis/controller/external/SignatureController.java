package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import ru.mtuci.siscatharsis.dto.signature.SignatureFetchUUIDrequest;
import ru.mtuci.siscatharsis.model.signature.Signature;
import ru.mtuci.siscatharsis.services.signature.SignatureService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;
import ru.mtuci.siscatharsis.utils.MediaUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/signature")
@RequiredArgsConstructor
public class SignatureController {
        private final SignatureService signatureService;
        private final ResponseUtils responseUtils;

        @GetMapping("/all")
        public ResponseEntity<?> fetchAll() {
                List<Signature> response = signatureService.getAll();

                return responseUtils.success(response);
        }

        @GetMapping("/diff/{dateTime}")
        public ResponseEntity<?> fetchDiff(@PathVariable Instant dateTime) {
                List<Signature> response = signatureService.getDiff(dateTime);

                return responseUtils.success(response);
        }

        @PostMapping("/guid")
        public ResponseEntity<?> fetchByGuids(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) {
                List<UUID> guidList = signatureFetchUUIDrequest.uuidList();

                List<Signature> response = signatureService.getByUUIDS(guidList);

                return responseUtils.success(response);
        }

        @GetMapping(value = "/download", produces = MediaType.MULTIPART_MIXED_VALUE)
        public ResponseEntity<?> download(@Valid @RequestBody SignatureFetchUUIDrequest signatureFetchUUIDrequest) throws IOException {
                List<UUID> guidList = signatureFetchUUIDrequest.uuidList();

                List<Signature> signatureList = signatureService.getByUUIDS(guidList);

                LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
                parts.add("manifest", new HttpEntity<>(formManifest(signatureList), createHeaders("manifest.bin")));
                parts.add("data", new HttpEntity<>(formBody(signatureList), createHeaders("data.bin")));

                return ResponseEntity.ok().contentType(MediaType.parseMediaType("multipart/mixed")).body(parts);
        }

        private ByteArrayResource formManifest(List<Signature> signatures) throws IOException {
                ByteArrayOutputStream manifest = new ByteArrayOutputStream();

                manifest.write(MediaUtil.intToByte(signatures.size()));

                for (Signature signature : signatures) {
                        manifest.write(MediaUtil.uuidToByte(signature.getId()));
                        manifest.write(signature.getDigitalSignature());
                }

                return new ByteArrayResource(manifest.toByteArray()) {
                        @Override
                        public String getFilename() {
                                return "manifest.bin";
                        }
                };
        }

        private ByteArrayResource formBody(List<Signature> signatures) throws IOException {
                // threat name, first bytes, remainderHash, remainderLength, filetype, offset start and end
                ByteArrayOutputStream data = new ByteArrayOutputStream();

                for (Signature signature : signatures) {
                        data.write(MediaUtil.stringToByte(signature.getThreatName()));
                        data.write(signature.getFirstBytes());
                        data.write(signature.getRemainderHash());
                        data.write(MediaUtil.intToByte(signature.getRemainderLength()));
                        data.write(MediaUtil.stringToByte(signature.getFileType()));
                        data.write(MediaUtil.intToByte(signature.getOffsetStart()));
                        data.write(MediaUtil.intToByte(signature.getOffsetEnd()));
                }

                return new ByteArrayResource(data.toByteArray()) {
                        @Override
                        public String getFilename() {
                                return "data.bin";
                        }
                };
        }

        private HttpHeaders createHeaders(String filename) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return headers;
        }
}
