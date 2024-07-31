package com.ulysseprod.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class demoController {

    @GetMapping("/democontroller")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello From secured endpoint");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> Admin(){
        return ResponseEntity.ok("Hello This is the Admin");
    }
}
