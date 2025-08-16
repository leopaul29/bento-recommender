package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.dtos.BentoFilterDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BentoService {
    // Search
    BentoDto getBentoById(Long id) throws EntityNotFoundException;
    BentoDto getRandomBento();
    Page<BentoDto> getBentoWithFilter(BentoFilterDto filterDto, Pageable pageable);

    // CRUD
    BentoDto saveBento(BentoDto bentoDto);
    BentoDto updateBento(Long id, BentoDto bentoDto);
    void deleteBento(Long id);
}
