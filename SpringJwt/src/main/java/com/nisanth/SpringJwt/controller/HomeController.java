package com.nisanth.SpringJwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController
{
    @GetMapping("/demo")
    public ResponseEntity<String> demo()
    {
        return ResponseEntity.ok("Hello from secret url");
    }

    @GetMapping("admin_only")
    public ResponseEntity<String> adminOnly()
    {
        return ResponseEntity.ok("hello from admin end point ");
    }
}
