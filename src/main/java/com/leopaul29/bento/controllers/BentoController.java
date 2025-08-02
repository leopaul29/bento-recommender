package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.services.BentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bentos")
public class BentoController {

    private final BentoService bentoService;

    public BentoController(BentoService bentoService) {
        this.bentoService = bentoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BentoDto> getBento(@PathVariable("id") Long bentoId) {
        return ResponseEntity.ok(bentoService.getBentoById(bentoId));
    }

    @PostMapping
    public ResponseEntity<BentoDto> createBento(@Valid @RequestBody BentoDto bentoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bentoService.saveBento(bentoDto));
    }
}
