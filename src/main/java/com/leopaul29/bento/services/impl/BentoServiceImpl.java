package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.dtos.BentoFilterDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.BentoSpecification;
import com.leopaul29.bento.services.BentoService;
import com.leopaul29.bento.services.IngredientService;
import com.leopaul29.bento.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
    public BentoDto updateBento(Long id, BentoDto bentoDto) {
        Bento bentoToUpdate = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: "+id));

        Set<Ingredient> allIngredients = ingredientService.saveAndGetIngredientSet(bentoDto.getIngredients());
        Set<Tag> allTags = tagService.saveAndGetTagSet(bentoDto.getTags());
        Bento bento = bentoMapper.toEntity(bentoDto);
        bento.setId(bentoToUpdate.getId());
        bento.setIngredients(allIngredients);
        bento.setTags(allTags);
        Bento update = this.bentoRepository.save(bento);
        return bentoMapper.toDto(update);
    }

    @Override
    public void deleteBento(Long id) {
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: "+id));
        bentoRepository.delete(bento);
    }

    @Override
    public BentoDto getBentoById(Long id) throws EntityNotFoundException {
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: "+id));
        return bentoMapper.toDto(bento);
    }

    @Override
    public BentoDto getRandomBento() {
        List<Bento> bentoList = bentoRepository.findAll();
        if(bentoList.isEmpty()) return null;

        Random rand = new Random();
        int randomIndex = rand.nextInt(bentoList.size());
        return bentoMapper.toDto(bentoList.get(randomIndex));
    }

    @Override
    public List<BentoDto> getBentoWithFilter(BentoFilterDto filterDto) {
        Specification<Bento> spec = Specification.unrestricted();

        if(filterDto.getIngredientIds() != null && !filterDto.getIngredientIds().isEmpty()) {
            spec = spec.and(BentoSpecification.hasIngredients(filterDto.getIngredientIds()));
        }

        return bentoRepository.findAll(spec)
                .stream()
                .map(bentoMapper::toDto)
                .collect(Collectors.toList());
    }
}
