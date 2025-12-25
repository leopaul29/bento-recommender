package com.leopaul29.bento.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;

import java.util.Date;

@Table(name = "user_bento_history")
@Entity
@Builder
public class UserBentoHistory {
    Long userId;
    Long bentoId;
    Date orderedAt;
}
