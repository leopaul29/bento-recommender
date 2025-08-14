package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;

import java.util.List;

public interface RecommendationService {
    List<BentoDto> getRecommendedByUserId(Long userId);
}
