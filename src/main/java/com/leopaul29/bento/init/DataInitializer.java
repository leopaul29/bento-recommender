package com.leopaul29.bento.init;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.IngredientRepository;
import com.leopaul29.bento.repositories.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BentoRepository bentoRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;

    public DataInitializer(BentoRepository bentoRepository,
                           IngredientRepository ingredientRepository,
                           TagRepository tagRepository) {
        this.bentoRepository = bentoRepository;
        this.ingredientRepository = ingredientRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("🌱 Bento data inserted");

        Ingredient tofu = ingredientRepository.save(new Ingredient(null, "tofu"));
        Ingredient rice = ingredientRepository.save(new Ingredient(null, "rice"));

        Tag vegan = tagRepository.save(new Tag(null, "vegan"));

        Bento bento = new Bento();
        bento.setName("Tofu Delight");
        bento.setDescription("Light tofu bento with rice");
        bento.setCalorie(350);
        bento.setIngredients(Set.of(tofu, rice));
        bento.setTags(Set.of(vegan));

        bentoRepository.save(bento);

        System.out.println("✅ Bento inserted: " + bento.getName());
    }
}
