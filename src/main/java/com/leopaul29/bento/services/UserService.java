package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.UserPreferenceDto;

public interface UserService {
    UserPreferenceDto getPreferencesByUserId(Long userId);
}
