package com.leopaul29.bento.mappers;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.entities.Bento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BentoMapper {
    BentoDto toDto(Bento bento);
    Bento toEntity(BentoDto bentoDto);
    List<BentoDto> toDtoList(List<Bento> bentoList);
}