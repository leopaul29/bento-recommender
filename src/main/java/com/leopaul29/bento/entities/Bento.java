package com.leopaul29.bento.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "bentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Bento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int calorie;

    @ManyToMany
    @JoinTable(
            name = "bento_ingredients",
            joinColumns = @JoinColumn(name = "bento_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients;
    @ManyToMany
    @JoinTable(
            name = "bento_tags",
            joinColumns = @JoinColumn(name = "bento_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @Override
    public String toString() {
        return "Bento{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", calorie=" + calorie +
                ", ingredients=" + ingredients +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bento bento = (Bento) o;
        return calorie == bento.calorie && Objects.equals(id, bento.id) && Objects.equals(name, bento.name) && Objects.equals(description, bento.description) && Objects.equals(ingredients, bento.ingredients) && Objects.equals(tags, bento.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, calorie, ingredients, tags);
    }
}
