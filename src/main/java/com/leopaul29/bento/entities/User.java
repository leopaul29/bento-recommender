package com.leopaul29.bento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToMany
    @JoinTable(
            name = "user_disliked_ingredient",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> dislikedIngredients;
    @ManyToMany
    @JoinTable(
            name = "user_liked_tag",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> likedTags;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", dislikedIngredients=" + dislikedIngredients +
                ", likedTags=" + likedTags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(dislikedIngredients, user.dislikedIngredients) && Objects.equals(likedTags, user.likedTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dislikedIngredients, likedTags);
    }
}
