package com.leopaul29.bento.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToMany
    @JoinTable(
            name = "user_disliked_ingredient",
            joinColumns = @JoinColumn(name = "user_pref_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> dislikedIngredients;
    @ManyToMany
    @JoinTable(
            name = "user_liked_tag",
            joinColumns = @JoinColumn(name = "user_pref_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> likedTags;
}
