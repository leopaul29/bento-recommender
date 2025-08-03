package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
