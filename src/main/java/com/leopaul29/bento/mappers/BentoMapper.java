package com.leopaul29.bento.mappers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BentoMapper {
    BentoDto toDto(Bento bento);
    Bento toEntity(BentoDto bentoDto);
}