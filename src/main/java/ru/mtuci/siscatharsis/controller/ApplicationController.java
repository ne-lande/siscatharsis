package ru.mtuci.siscatharsis.controller;

import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.model.ExampleModel;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @GetMapping("/hello")
    public String getHello(@RequestParam(defaultValue = "", required = false) String name)
    {
        if (name.isEmpty()) {
            return "Hello, world!";
        }
        return "Hello, " + name + "!";
    }

    @PostMapping("/hello")
    public ExampleModel postHello(@RequestBody ExampleModel postPayload) {
        int helloAmount = postPayload.getHelloAmount();

        postPayload.setHelloAmount(helloAmount + 1);

        if (postPayload.getName() == null) {
            postPayload.setName("anon");
        }

        return postPayload;
    }
}
