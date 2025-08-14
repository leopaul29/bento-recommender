package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.services.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BentoRepository bentoRepository;
    @Autowired
    private BentoMapper bentoMapper;

    @Override
    public List<BentoDto> getRecommendedByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User id: " + userId));

        Predicate<Bento> excludeDisliked = bento ->
                Collections.disjoint(
                        bento.getIngredients(),
                        user.getDislikedIngredients()
                );

        Predicate<Bento> matchLikedTags = bento ->
                !Collections.disjoint(
                        bento.getTags(),
                        user.getLikedTags()
                );

        return bentoRepository.findAll().stream()
                .filter(excludeDisliked)
                .filter(matchLikedTags)
                .map(bentoMapper::toDto)
                .toList();
    }
}
