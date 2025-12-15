package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.entities.User;
import jakarta.validation.Valid;

public interface UserService {
    UserPreferenceDto getPreferencesByUserId(Long userId);
    User getById(Long userId);
    UserPreferenceDto savePreferencesByUserId(@Valid UserPreferenceDto userPreferenceDto);
}
