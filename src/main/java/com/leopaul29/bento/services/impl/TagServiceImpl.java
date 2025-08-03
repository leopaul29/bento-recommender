package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.TagDto;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.mappers.TagMapper;
import com.leopaul29.bento.repositories.TagRepository;
import com.leopaul29.bento.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public Set<Tag> saveAndGetTagSet(List<TagDto> tagDtoList) {
        List<String> names = tagDtoList.stream().map(tagDto -> tagDto.getName().toLowerCase()).distinct().toList();
        List<Tag> existing = tagRepository.findByNameIn(names);

        Set<String> existingNames = existing.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> toCreate = tagDtoList
                .stream()
                .filter(dto -> !existingNames.contains(dto.getName()))
                .map(tagMapper::toEntity)
                .toList();

        List<Tag> created = tagRepository.saveAll(toCreate);

        return Stream.concat(existing.stream(), created.stream())
                .collect(Collectors.toSet());
    }
}
