package ru.mtuci.siscatharsis.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import ru.mtuci.siscatharsis.services.CryptoService;

@Component
@RequiredArgsConstructor
public class ApiConstructor {

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

    public ResponseEntity<String> successSigned(Object data) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.set("data", objectMapper.valueToTree(data));

        String signature = cryptoService.signWithCurrent(json.get("data").toString());
        json.put("signature", signature);

        return ResponseEntity.status(HttpStatus.OK).body(json.toString());
    }

    private ResponseEntity<String> buildResponse(String key, Object value, HttpStatus status) {
        ObjectNode json = objectMapper.createObjectNode();
        json.set(key, objectMapper.valueToTree(value));
        return ResponseEntity.status(status).body(json.toString());
    }
}