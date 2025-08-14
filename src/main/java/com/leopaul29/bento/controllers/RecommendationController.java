package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.services.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // GET /{id}
    @GetMapping("/{userId}")
    public ResponseEntity<List<BentoDto>> getAllRecommendedBentos(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(recommendationService.getRecommendedByUserId(userId));
    }
}
