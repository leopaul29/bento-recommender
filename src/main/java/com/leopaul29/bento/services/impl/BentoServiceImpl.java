package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.services.BentoService;
import com.leopaul29.bento.services.IngredientService;
import com.leopaul29.bento.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BentoServiceImpl implements BentoService {

    @Autowired
    private BentoRepository bentoRepository;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BentoMapper bentoMapper;

    @Override
    public BentoDto saveBento(BentoDto bentoDto) {
        Set<Ingredient> allIngredients = ingredientService.saveAndGetIngredientSet(bentoDto.getIngredients());
        Set<Tag> allTags = tagService.saveAndGetTagSet(bentoDto.getTags());

        Bento bento = bentoMapper.toEntity(bentoDto);
        bento.setIngredients(allIngredients);
        bento.setTags(allTags);
        Bento saved = this.bentoRepository.save(bento);
        return bentoMapper.toDto(saved);
    }

    @Override
    public BentoDto getBentoById(Long id) throws EntityNotFoundException {
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: "+id));
        return bentoMapper.toDto(bento);
    }

    @Override
    public List<BentoDto> getAllBentos() {
        return bentoMapper.toDtoList(bentoRepository.findAll());
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
