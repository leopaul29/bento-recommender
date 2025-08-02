package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
