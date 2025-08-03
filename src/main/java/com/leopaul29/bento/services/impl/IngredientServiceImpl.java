package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.IngredientDto;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.mappers.IngredientMapper;
import com.leopaul29.bento.repositories.IngredientRepository;
import com.leopaul29.bento.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IngredientServiceImpl implements IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private IngredientMapper ingredientMapper;

    @Override
    public Set<Ingredient> saveAndGetIngredientSet(List<IngredientDto> ingredientDtoList) {
        List<String> names = ingredientDtoList.stream().map(IngredientDto::getName).distinct().toList();
        List<Ingredient> existing = ingredientRepository.findByNameIn(names);

        Set<String> existingNames = existing.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet());

        List<Ingredient> toCreate = ingredientDtoList
                .stream()
                .filter(dto -> !existingNames.contains(dto.getName()))
                .map(ingredientMapper::toEntity)
                .toList();

        List<Ingredient> created = ingredientRepository.saveAll(toCreate);

        return Stream.concat(existing.stream(), created.stream())
                .collect(Collectors.toSet());
    }
}
