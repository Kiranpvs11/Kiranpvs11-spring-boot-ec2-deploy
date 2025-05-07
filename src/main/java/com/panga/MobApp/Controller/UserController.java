package com.panga.MobApp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getUserDashboard() {
        return ResponseEntity.ok("{\"message\": \"Welcome to the User Dashboard!\"}");
    }
}
