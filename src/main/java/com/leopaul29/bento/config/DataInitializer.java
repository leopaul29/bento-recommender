package com.leopaul29.bento.config;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.IngredientRepository;
import com.leopaul29.bento.repositories.TagRepository;
import com.leopaul29.bento.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    private final BentoRepository bentoRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public DataInitializer(BentoRepository bentoRepository,
                           IngredientRepository ingredientRepository,
                           TagRepository tagRepository,
                           UserRepository userRepository) {
        this.bentoRepository = bentoRepository;
        this.ingredientRepository = ingredientRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    /**
     * runs after Spring context initialization, so when DB is ready.
     */
    @PostConstruct
    public void initData() {
        if (bentoRepository.count() > 0) {
            return; // Avoid duplicate data
        }
        System.out.println("🌱 Start data insertion");

        // --- Ingredients ---
        Ingredient rice = ingredientRepository.save(new Ingredient(null, "rice"));
        Ingredient salmon = ingredientRepository.save(new Ingredient(null, "salmon"));
        Ingredient tofu = ingredientRepository.save(new Ingredient(null, "tofu"));
        Ingredient tomato = ingredientRepository.save(new Ingredient(null, "tomato"));
        Ingredient beef = ingredientRepository.save(new Ingredient(null, "beef"));
        System.out.println("✅ Ingredient data inserted");

        // --- Tags ---
        Tag vegan = tagRepository.save(new Tag(null, "vegan"));
        Tag spicy = tagRepository.save(new Tag(null, "spicy"));
        Tag japanese = tagRepository.save(new Tag(null, "japanese"));
        Tag highProtein = tagRepository.save(new Tag(null, "high-protein"));
        System.out.println("✅ Tag data inserted");

        // --- Bentos ---
        Bento bento1 = new Bento();
        bento1.setName("Tofu Delight");
        bento1.setDescription("Light tofu bento with rice");
        bento1.setCalorie(350);
        bento1.setIngredients(Set.of(rice, tofu, tomato));
        bento1.setTags(Set.of(vegan, japanese));
        bentoRepository.save(bento1);

        Bento bento2 = new Bento();
        bento2.setName("Salmon Special");
        bento2.setDescription("Grilled salmon with rice and veggies");
        bento2.setCalorie(500);
        bento2.setIngredients(Set.of(rice, salmon, tomato));
        bento2.setTags(Set.of(japanese, highProtein));
        bentoRepository.save(bento2);

        Bento bento3 = new Bento();
        bento3.setName("Spicy Beef Bento");
        bento3.setDescription("Beef stir-fry with spicy sauce");
        bento3.setCalorie(600);
        bento3.setIngredients(Set.of(rice, beef, tomato));
        bento3.setTags(Set.of(spicy, highProtein));
        bentoRepository.save(bento3);
        System.out.println("✅ Bento data inserted");

        User user = new User();
        user.setUsername("Tom");
        user.setPassword("password");
        user.setLikedTags(Set.of(vegan, japanese));
        user.setDislikedIngredients(Set.of(beef));
        userRepository.save(user);
        System.out.println("✅ User inserted: " + user.getId());

        System.out.println("🍱 Dummy data inserted");
    }
}
