package com.leopaul29.bento.controllers;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.repositories.BentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BentoControllerErrorTest {

    private final static String BASE_URL = "/api/bentos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BentoRepository bentoRepository;

    @BeforeEach
    void setup() {
        bentoRepository.deleteAll();
        Bento bento = new Bento();
        bento.setName("Test Bento");
        bento.setDescription("Dummy bento");
        bento.setCalorie(400);
        bentoRepository.save(bento);
    }

    @Test
    void testGetBentoByIdNotFound() throws Exception {
        long id = 42L;

        mockMvc.perform(get(BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBentoWithEmptyIngredientsAndTags() throws Exception {
        String json = """
        {
            "name": "Tofu Delight",
            "description": "Light tofu bento with rice",
            "calorie": 350,
            "ingredients": [],
            "tags": []
        }
        """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotAcceptable());
    }
}
