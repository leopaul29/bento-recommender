package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.entities.User;

public interface UserService {
    UserPreferenceDto getPreferencesByUserId(Long userId);
    User getUserById(Long userId);
}
