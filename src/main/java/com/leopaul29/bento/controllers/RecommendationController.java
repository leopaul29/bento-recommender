package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.services.UserService;
import com.leopaul29.bento.services.recommendation.RecommendationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserService userService;
    private final BentoRepository bentoRepository;
    private final BentoMapper bentoMapper;

    public RecommendationController(RecommendationService recommendationService,
                                    UserService userService,
                                    BentoRepository bentoRepository,
                                    BentoMapper bentoMapper) {
        this.recommendationService = recommendationService;
        this.userService = userService;
        this.bentoRepository = bentoRepository;
        this.bentoMapper = bentoMapper;
    }

    // GET /{id}
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<BentoDto>> getAllRecommendedBentos(@PathVariable("userId") Long userId) {
//        return ResponseEntity.ok(recommendationService.getRecommendedByUserId(userId));
//    }

    /**
     * recommend bento for user
     * API URL samples:
     * GET /api/recommendations?userId=1
     * GET /api/recommendations?userId=1&strategy=WeightedRecommendationStrategy
     * @param userId userId
     * @param strategy strategy name
     * @return List<Bento> bentoList
     */
    @RequestMapping("/")
    public List<BentoDto> recommend(
            @RequestParam Long userId,
            @RequestParam(required = false) String strategy
    ) {
        User user = userService.getById(userId);
        List<Bento> all = bentoRepository.findAll();

        List<Bento> result = recommendationService
                .recommend(strategy, user, all);

        return result.stream()
                .map(bentoMapper::toDto)
                .toList();
    }
}
