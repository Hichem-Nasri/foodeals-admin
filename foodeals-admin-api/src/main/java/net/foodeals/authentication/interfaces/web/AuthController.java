package net.foodeals.authentication.interfaces.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world;";
    }
}
