package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.dtos.BentoDto;
import com.leopaul29.bento.services.BentoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BentoServiceTest {
    private final static String BASE_URL = "/api/bentos";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BentoService bentoService;

    private BentoDto tmpbento;

    @BeforeEach
    void setup() {
        tmpbento = new BentoDto();
        tmpbento.setName("Test Bento");
        tmpbento.setDescription("Dummy bento");
        tmpbento.setCalorie(400);
    }

    @Test
    void shouldReturnBentoWhenGetById() throws Exception {
        long id = 0L;
        when(bentoService.getBentoById(id)).thenReturn(tmpbento);

        mockMvc.perform(get(BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Bento"))
                .andExpect(jsonPath("$.calorie").value(400));
    }

    @Test
    void shouldReturn404WhenGetByIdNotFound() throws Exception {
        long id = 0L;
        when(bentoService.getBentoById(id)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenGetByIdWithInvalidID() throws Exception {
        String invalidID = "abc"; // not a number

        mockMvc.perform(get(BASE_URL + "/" + invalidID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // bentoService should never be called
        verifyNoInteractions(bentoService);
    }
}
