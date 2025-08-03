package com.leopaul29.bento.init;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.IngredientRepository;
import com.leopaul29.bento.repositories.TagRepository;
import com.leopaul29.bento.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

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

    @Override
    public void run(String... args) {
        System.out.println("🌱 Bento data inserted");

        var beef = ingredientRepository.save(new Ingredient(null, "beef"));
        var rice = ingredientRepository.save(new Ingredient(null, "rice"));
        var tofu = ingredientRepository.save(new Ingredient(null, "tofu"));

        var spicy = tagRepository.save(new Tag(null, "spicy"));
        var vegan = tagRepository.save(new Tag(null, "vegan"));
        var japanese = tagRepository.save(new Tag(null, "japanese"));

        Bento bento = new Bento();
        bento.setName("Tofu Delight");
        bento.setDescription("Light tofu bento with rice");
        bento.setCalorie(350);
        bento.setIngredients(Set.of(tofu, rice));
        bento.setTags(Set.of(vegan));

        bentoRepository.save(bento);
        System.out.println("✅ Bento inserted: " + bento.getName());
        
        User user = new User();
        user.setLikedTags(Set.of(vegan, japanese));
        user.setDislikedIngredients(Set.of(beef));
        userRepository.save(user);
        System.out.println("✅ User inserted: " + user.getId());
    }
}
