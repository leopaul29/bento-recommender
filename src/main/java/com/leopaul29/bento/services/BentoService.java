package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;

import java.util.List;
import java.util.Set;

public interface BentoService {
    public Bento saveBento(BentoDto bento);
    public BentoDto getBentoById(Long id);
    public List<Bento> findBentosByTags(Set<Tag> tags);
    public List<Bento> findBentosByIngredients(Set<Ingredient> ingredients);
    public Bento getRandomBento();
}
