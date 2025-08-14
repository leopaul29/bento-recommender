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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BentoControllerTest {

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
    void testGetBentoById() throws Exception {
        Long id = bentoRepository.findAll().get(0).getId();

        mockMvc.perform(get("/bentos/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Bento"))
                .andExpect(jsonPath("$.calorie").value(400));
    }
}
