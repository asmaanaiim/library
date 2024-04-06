package com.spring.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.library.model.Patron;

@SpringBootTest
@AutoConfigureMockMvc
public class PatronControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreatePatronWithInvalidPhoneNumber() throws Exception {
        Patron patron = new Patron("John Doe", "1234567890", "john@example.com", "123 Main St");

        ObjectMapper objectMapper = new ObjectMapper();
        String patronJson = objectMapper.writeValueAsString(patron);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .content(patronJson)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        String errorMessage = result.getResponse().getContentAsString();
        assertEquals("Phone number must be 9 digits", errorMessage);
    }

    @Test
    public void testCreatePatronWithInvalidEmail() throws Exception {
        Patron patron = new Patron("John Doe", "123456789", "bl-bl", "123 Main St");

        ObjectMapper objectMapper = new ObjectMapper();
        String patronJson = objectMapper.writeValueAsString(patron);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .content(patronJson)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        String errorMessage = result.getResponse().getContentAsString();
        assertEquals("Invalid email format", errorMessage);
    }

    @Test
    public void testCreatePatronWithInvalidName() throws Exception {
        Patron patron = new Patron("", "123456789", "john@example.com", "123 Main St");

        ObjectMapper objectMapper = new ObjectMapper();
        String patronJson = objectMapper.writeValueAsString(patron);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .content(patronJson)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        String errorMessage = result.getResponse().getContentAsString();
        assertEquals("Name is required", errorMessage);
    }

    @Test
    public void testUpdatePatronWithInvalidEmail() throws Exception {
        Patron patron = new Patron("John Doe", "123456789", "john@example.com", "123 Main St");

        ObjectMapper objectMapper = new ObjectMapper();
        String patronJson = objectMapper.writeValueAsString(patron);

        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .content(patronJson)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), postResult.getResponse().getStatus());
        String postResponseContent = postResult.getResponse().getContentAsString();
        assertNotNull(postResponseContent);
        Patron createdPatron = objectMapper.readValue(postResponseContent, Patron.class);
        assertNotNull(createdPatron.getId());

        createdPatron.setEmail("invalid-email");

        String updatedPatronJson = objectMapper.writeValueAsString(createdPatron);

        MvcResult putResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/patrons/" + createdPatron.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .content(updatedPatronJson)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), putResult.getResponse().getStatus());
        String errorMessage = putResult.getResponse().getContentAsString();
        assertEquals("Invalid email format", errorMessage);
    }
}
