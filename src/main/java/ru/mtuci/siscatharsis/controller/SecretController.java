package ru.mtuci.siscatharsis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
public class SecretController {

    @Autowired
    public SecretController() {}

    @GetMapping("/secret")
    public String secret() {
        //console.log("I AM HERE");
        return "This is secret endpoint which tells that server is working fine. ALAS: ALIIIIIVEEEEE";
        //return ApiMessage.Secret("This is secret endpoint which tells that server is working fine. ALAS: ALIIIIIVEEEEE");
    }
}
