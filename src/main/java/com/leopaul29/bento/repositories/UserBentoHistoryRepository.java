package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.UserBentoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Map;

public interface UserBentoHistoryRepository extends JpaRepository<UserBentoHistory, Long> {
    @Query("""
        SELECT h.bentoId, COUNT(h)
        FROM UserBentoHistory h
        WHERE h.userId = :userId
        GROUP BY h.bentoId
        ORDER BY COUNT(h) DESC
    """)
    Map<Long, Long> countByBentoIdForUser(Long userId);

    @Query("""
        SELECT h.bentoId, MAX(h.orderedAt)
        FROM UserBentoHistory h
        WHERE h.userId = :userId
        GROUP BY h.bentoId
    """)
    Map<Long, Date> lastOrderedByBento(Long userId);
}
