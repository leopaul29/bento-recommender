package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameIn(List<String> names);
}
