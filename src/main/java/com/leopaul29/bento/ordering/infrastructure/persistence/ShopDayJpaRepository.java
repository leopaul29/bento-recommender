package com.leopaul29.bento.ordering.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ShopDayJpaRepository extends JpaRepository<ShopDayEntity, LocalDate> {}
