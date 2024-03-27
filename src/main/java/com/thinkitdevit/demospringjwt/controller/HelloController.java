package com.thinkitdevit.demospringjwt.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("HELLO!");
    }


    @GetMapping("/say-hello")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> sayHello(@RequestParam(value="name") String name){
        return ResponseEntity.ok("Hello "+name);
    }

}
