package com.example.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.security.Principal;
import java.util.Map;

@SpringBootApplication
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }

}
@Controller
@ResponseBody
class MessageController {


    @GetMapping("/message")
    Map<String, String> message(Principal principal) {
        return Map.of("message", "Hello, " + principal.getName() + "!");
    }

}