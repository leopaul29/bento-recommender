package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import jakarta.validation.Valid;

public interface UserService {
    UserPreferenceDto getPreferencesByUserId(Long userId);
//    User getUserById(Long userId);
    UserPreferenceDto savePreferencesByUserId(@Valid UserPreferenceDto userPreferenceDto);
}
