package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.mappers.UserMapper;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.services.IngredientService;
import com.leopaul29.bento.services.TagService;
import com.leopaul29.bento.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private TagService tagService;

    @Override
    public UserPreferenceDto getPreferencesByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User id: " + userId));
        return userMapper.toUserPreferenceDto(user);
    }

    @Override
    public UserPreferenceDto savePreferencesByUserId(UserPreferenceDto userPreferenceDto) {
        Set<Ingredient> allIngredients = ingredientService.saveAndGetIngredientSet(userPreferenceDto.getDislikedIngredients());
        Set<Tag> allTags = tagService.saveAndGetTagSet(userPreferenceDto.getLikedTags());

        User user = userMapper.toEntity(userPreferenceDto);
        user.setDislikedIngredients(allIngredients);
        user.setLikedTags(allTags);
        User saved = userRepository.save(user);
        return userMapper.toUserPreferenceDto(saved);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User id: " + userId));
    }
}
