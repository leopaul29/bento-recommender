package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface BentoService {
    // Search
    List<BentoDto> getAllBentos();
    BentoDto getBentoById(Long id) throws EntityNotFoundException;
    BentoDto getRandomBento();
    List<Bento> findBentosByTags(Set<Tag> tags);
    List<Bento> findBentosByIngredients(Set<Ingredient> ingredients);

    // CRUD
    BentoDto saveBento(BentoDto bentoDto);
    BentoDto updateBento(Long id, BentoDto bentoDto);
    void deleteBento(Long id);
}
