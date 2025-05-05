package ru.mtuci.siscatharsis.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ApiMessage {

    // TODO: Extract ObjectMapper as bean to be global
    // TODO: Change emplicit behaviour (give the object, status of evaluation should be response code only)
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    public static ResponseEntity<String> BadRequest(String message) {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", "Bad Request");
        json.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json.toString());
    }

    public static ResponseEntity<String> Success(Object data) {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", "Success");
        json.set("data", objectMapper.valueToTree(data));
        return ResponseEntity.status(HttpStatus.OK).body(json.toString());
    }

    public static ResponseEntity<String> ServerError(String message) {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", "Internal Server Error");
        json.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

    public static ResponseEntity<String> Secret(String message) {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", "fun");
        json.put("message", message);
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(json.toString());
    }
}
