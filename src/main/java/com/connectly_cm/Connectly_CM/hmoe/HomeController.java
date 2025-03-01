package com.connectly_cm.Connectly_CM.hmoe;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "index";  // Renders templates/index.html
    }
}
