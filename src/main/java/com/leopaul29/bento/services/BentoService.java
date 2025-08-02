package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface BentoService {
    public BentoDto saveBento(BentoDto bento);
    public BentoDto getBentoById(Long id) throws EntityNotFoundException;
    public List<BentoDto> getAllBentos();
    public List<Bento> findBentosByTags(Set<Tag> tags);
    public List<Bento> findBentosByIngredients(Set<Ingredient> ingredients);
    public Bento getRandomBento();
}
