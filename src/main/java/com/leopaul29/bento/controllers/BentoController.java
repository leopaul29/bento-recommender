package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.services.BentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bentos")
public class BentoController {

    private final BentoService bentoService;

    public BentoController(BentoService bentoService) {
        this.bentoService = bentoService;
    }

    // GET
    @Operation(summary = "Get all bentos", description = "登録済みの全Bento情報を取得するAPI。")
    @GetMapping()
    public ResponseEntity<List<BentoDto>> getAllBentos() {
        return ResponseEntity.ok(bentoService.getAllBentos());
    }

    // GET /{id}
    @Operation(summary = "Gets bento by ID", description = "Bento must exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = BentoDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Bento not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResponse.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<BentoDto> getBentoById(@PathVariable("id") Long bentoId) {
        return ResponseEntity.ok(bentoService.getBentoById(bentoId));
    }

    // GET /rand
    @GetMapping("/rand")
    public ResponseEntity<BentoDto> getRandomBento() {
        BentoDto randomBento = bentoService.getRandomBento();
        if(randomBento == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(randomBento);
    }

    // POST
    @PostMapping
    public ResponseEntity<BentoDto> createBento(@Valid @RequestBody BentoDto bentoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bentoService.saveBento(bentoDto));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<BentoDto> updateBento(@PathVariable("id") Long bentoId, @Valid @RequestBody BentoDto bentoDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.bentoService.updateBento(bentoId, bentoDto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> updateBento(@PathVariable("id") Long bentoId) {
        this.bentoService.deleteBento(bentoId);
        return new ResponseEntity<String>("Bento deleted: "+bentoId, HttpStatus.ACCEPTED);
    }
}
