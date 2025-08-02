package com.leopaul29.bento.mappers;

import com.leopaul29.bento.dtos.TagDto;
import com.leopaul29.bento.entities.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
    Tag toEntity(TagDto tagDto);
    List<TagDto> toDtoList(List<Tag> tagList);
}