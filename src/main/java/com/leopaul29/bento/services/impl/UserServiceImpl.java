package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.mappers.UserMapper;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserPreferenceDto getPreferencesByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User id: " + id));
        return userMapper.toUserPreferenceDto(user);
    }
}
