package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.dtos.BentoFilterDto;
import com.leopaul29.bento.services.BentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bentos")
public class BentoController {

    private final BentoService bentoService;

    public BentoController(BentoService bentoService) {
        this.bentoService = bentoService;
    }

    /*
        # Tous les bentos:         GET /api/bento
        # Avec filtres:            GET /api/bento?ingredientIds=1,2&tagIds=3,4
        # Avec pagination:         GET /api/bento?ingredientIds=1,2&page=0&size=10&sort=name,asc
        # Filtres d'exclusion:     GET /api/bento?excludeIngredientIds=5&excludeTagIds=6,7
     */
    //https://spec.openapis.org/oas/v3.1.0#example-object
//    @ApiResponse(value = {
//            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(
//                    mediaType = "application/json",
//                    examples = {
//                            @ExampleObject(name = "getBentoWithFilter", externalValue = "res")
//                    }))
//    })

    /*
    {
  "content": [
    {
      "id": 1,
      "name": "Bento Saumon",
      "ingredients": [...],
      "tags": [...]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "numberOfElements": 20
}
     */
    // GET ?ingredientIds=1,..,10&?tagIds=1,..,10&?excludeIngredientIds=1,..,10&?excludeTagIds=1,..,10
    @GetMapping
    public ResponseEntity<Page<BentoDto>> getBentoWithFilter(@Valid BentoFilterDto filterDto,
                                                             @PageableDefault(
                                                                     size = 20, sort = "id", direction = Sort.Direction.ASC)
                                                             Pageable pageable) {
        Page<BentoDto> bentos = bentoService.getBentoWithFilter(filterDto, pageable);
        return ResponseEntity.ok(bentos);
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
