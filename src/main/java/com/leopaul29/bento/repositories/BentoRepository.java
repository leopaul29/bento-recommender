package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Bento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BentoRepository extends JpaRepository<Bento, Long>, JpaSpecificationExecutor<Bento> {
}
