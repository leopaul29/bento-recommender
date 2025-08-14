package com.leopaul29.bento.mappers;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserPreferenceDto toUserPreferenceDto(User user);
    User toEntity(UserPreferenceDto toUserPreferenceDto);
}
