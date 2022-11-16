package com.prototype.splitwise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping(value = "ping")
    public ResponseEntity<Boolean> ping() {
        return ResponseEntity.ok(Boolean.TRUE);
    }
}
