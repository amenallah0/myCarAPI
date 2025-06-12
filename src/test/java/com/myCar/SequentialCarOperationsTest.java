package com.myCar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;

import com.myCar.domain.User;
import com.myCar.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Rollback(false)
@WithMockUser(roles = "ADMIN")
public class SequentialCarOperationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdCarId;
    private static Long testUserId;

    @Test
    @Order(1)
    public void testCreateUser() throws Exception {
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        String userJson = String.format("""
            {
                "username": "testuser",
                "email": "%s",
                "password": "password123",
                "role": "ROLE_USER"
            }
            """, email);

        MvcResult result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        testUserId = jsonNode.get("id").asLong();
        
        // Vérifier que l'ID n'est pas null
        Assertions.assertNotNull(testUserId, "L'ID de l'utilisateur ne devrait pas être null");
    }

    @Test
    @Order(2)
    public void testCreateCar() throws Exception {
        // Vérifier que l'ID de l'utilisateur est disponible
        Assertions.assertNotNull(testUserId, "L'ID de l'utilisateur doit être disponible avant de créer une voiture");
        
        String carJson = """
            {
                "make": "Toyota",
                "model": "Camry",
                "color": "Black",
                "year": 2023,
                "powerRating": 180,
                "numberOfDoors": 4,
                "fuelTankCapacity": 60,
                "maximumSpeed": 200,
                "mileage": 0,
                "options": "Full options",
                "price": 25000.00
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/cars/user/" + testUserId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        createdCarId = jsonNode.get("id").asLong();
        
        // Vérifier que l'ID de la voiture n'est pas null
        Assertions.assertNotNull(createdCarId, "L'ID de la voiture ne devrait pas être null");
    }

    @Test
    @Order(3)
    public void testGetCreatedCar() throws Exception {
        Assertions.assertNotNull(createdCarId, "L'ID de la voiture doit être disponible avant de la récupérer");
        
        mockMvc.perform(get("/api/cars/" + createdCarId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"));
    }

    @Test
    @Order(4)
    public void testUpdateCarAvailability() throws Exception {
        Assertions.assertNotNull(createdCarId, "L'ID de la voiture doit être disponible avant de mettre à jour sa disponibilité");
        
        mockMvc.perform(put("/api/cars/" + createdCarId + "/availability")
                .param("available", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @Order(5)
    public void testDeleteCar() throws Exception {
        Assertions.assertNotNull(createdCarId, "L'ID de la voiture doit être disponible avant de la supprimer");
        
        mockMvc.perform(delete("/api/cars/" + createdCarId))
                .andExpect(status().isNoContent());

        // Verify the car is deleted
        mockMvc.perform(get("/api/cars/" + createdCarId))
                .andExpect(status().isNotFound());
    }
} 