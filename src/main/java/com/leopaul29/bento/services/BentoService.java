package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface BentoService {
    BentoDto saveBento(BentoDto bento);
    BentoDto getBentoById(Long id) throws EntityNotFoundException;
    List<BentoDto> getAllBentos();
    List<Bento> findBentosByTags(Set<Tag> tags);
    List<Bento> findBentosByIngredients(Set<Ingredient> ingredients);
    BentoDto getRandomBento();
    List<BentoDto> getRecommendedForUserId(Long userId);
}
