package com.panga.MobApp.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String hello() {
        return "✅ Spring Boot is running on EC2";
    }
}
