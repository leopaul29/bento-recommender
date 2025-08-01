package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BentoServiceImpl implements BentoService{

    @Autowired
    private BentoRepository bentoRepository;
    @Autowired
    private BentoMapper bentoMapper;

    @Override
    public Bento saveBento(BentoDto bentoDto) {
        Bento bento = bentoMapper.toEntity(bentoDto);
        return this.bentoRepository.save(bento);
    }

    @Override
    public BentoDto getBentoById(Long id) {
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Bento not found"));
        return bentoMapper.toDto(bento);
    }

    @Override
    public List<Bento> findBentosByTags(Set<Tag> tags) {
        return List.of();
    }

    @Override
    public List<Bento> findBentosByIngredients(Set<Ingredient> ingredients) {
        return List.of();
    }

    @Override
    public Bento getRandomBento() {
        return null;
    }
}
