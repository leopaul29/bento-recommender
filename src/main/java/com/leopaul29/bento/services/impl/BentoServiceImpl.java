package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.dtos.BentoFilterDto;
import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.mappers.BentoMapper;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.specification.BentoSpecification;
import com.leopaul29.bento.services.BentoService;
import com.leopaul29.bento.services.IngredientService;
import com.leopaul29.bento.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
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
    public BentoDto updateBento(Long id, BentoDto bentoDto) {
        Bento bentoToUpdate = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: " + id));

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
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: " + id));
        bentoRepository.delete(bento);
    }

    @Override
    public BentoDto getBentoById(Long id) throws EntityNotFoundException {
        Bento bento = this.bentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid Bento id: " + id));
        return bentoMapper.toDto(bento);
    }

    @Override
    public BentoDto getRandomBento() {
        List<Bento> bentoList = bentoRepository.findAll();
        if (bentoList.isEmpty()) return null;

        Random rand = new Random();
        int randomIndex = rand.nextInt(bentoList.size());
        return bentoMapper.toDto(bentoList.get(randomIndex));
    }

    @Override
    public Page<BentoDto> getBentoWithFilter(BentoFilterDto filterDto, Pageable pageable) {
        Specification<Bento> spec = buildSpecification(filterDto);
        Page<Bento> bentoPage = bentoRepository.findAll(spec, pageable);

        return bentoPage.map(bentoMapper::toDto);
    }

    private Specification<Bento> buildSpecification(BentoFilterDto filter) {
        Specification<Bento> spec = Specification.unrestricted();
        if (filter != null && filter.hasAnyFilter()) {
            if (filter.hasIngredientFilter()) {
                spec = spec.and(BentoSpecification.hasIngredients(filter.getIngredientIds()));
            }

            if (filter.hasTagFilter()) {
                spec = spec.and(BentoSpecification.hasTags(filter.getTagIds()));
            }

            if (filter.hasExcludeIngredientFilter()) {
                spec = spec.and(BentoSpecification.excludeIngredients(filter.getExcludeIngredientIds()));
            }

            if (filter.hasExcludeTagFilter()) {
                spec = spec.and(BentoSpecification.excludeTags(filter.getExcludeTagIds()));
            }
        }
        return spec;
    }
}