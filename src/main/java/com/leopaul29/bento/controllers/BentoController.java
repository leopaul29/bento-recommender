package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.services.BentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bentos")
public class BentoController {

    @Autowired
    private BentoService bentoService;

    @GetMapping("/{id}")
    public ResponseEntity<BentoDto> getBento(@PathVariable("id") Long BentoId) {
        return ResponseEntity.ok(bentoService.getBentoById(BentoId));
    }

    @PostMapping
    public ResponseEntity<Bento> createBento(@RequestBody BentoDto bentoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bentoService.saveBento(bentoDto));
    }
}
