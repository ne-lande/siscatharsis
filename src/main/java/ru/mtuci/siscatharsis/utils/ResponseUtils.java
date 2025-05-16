package ru.mtuci.siscatharsis.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.services.CryptoService;

import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ResponseUtils {

    private final ObjectMapper objectMapper;
    private final CryptoService cryptoService;

    public ResponseEntity<String> badRequest(String message) {
        return buildResponse("message", message, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> success(Object data) {
        return buildResponse("data", data, HttpStatus.OK);
    }

    public ResponseEntity<String> serverError(String message) {
        return buildResponse("message", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> accessDenied(String message) {
        return buildResponse("message", message, HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<String> successSigned(Object data) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.set("data", objectMapper.valueToTree(data));

        String signature = Base64.getEncoder().encodeToString(cryptoService.signWithCurrent(data.toString().getBytes()));
        json.put("signature", signature);

        return ResponseEntity.status(HttpStatus.OK).body(json.toString());
    }

    private ResponseEntity<String> buildResponse(String key, Object value, HttpStatus status) {
        ObjectNode json = objectMapper.createObjectNode();
        json.set(key, objectMapper.valueToTree(value));
        return ResponseEntity.status(status).body(json.toString());
    }
}