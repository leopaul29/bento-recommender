package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.TagDto;
import com.leopaul29.bento.entities.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {
    Set<Tag> saveAndGetTagSet(List<TagDto> TagDtoList);

}
