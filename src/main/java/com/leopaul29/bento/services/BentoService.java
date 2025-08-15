package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.dtos.BentoFilterDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface BentoService {
    // Search
    BentoDto getBentoById(Long id) throws EntityNotFoundException;
    BentoDto getRandomBento();
    List<BentoDto> getBentoWithFilter(BentoFilterDto filterDto);

    // CRUD
    BentoDto saveBento(BentoDto bentoDto);
    BentoDto updateBento(Long id, BentoDto bentoDto);
    void deleteBento(Long id);
}
