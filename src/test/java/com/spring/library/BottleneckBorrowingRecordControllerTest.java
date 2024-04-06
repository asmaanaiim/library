package com.spring.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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
import com.spring.library.model.Book;
import com.spring.library.model.Patron;

@SpringBootTest
@AutoConfigureMockMvc
public class BottleneckBorrowingRecordControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testBorrowBooksWithLimitedCopies() throws Exception {
                // Create a book with limited copies
                MvcResult bookResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .contentType("application/json")
                                .content(
                                                "{\"title\":\"To Kill a Mockingbird\",\"author\":\"Harper Lee\",\"publicationYear\":\"1960\",\"isbn\":\"9780061120084\",\"availableCopies\":3}")
                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.CREATED.value(), bookResult.getResponse().getStatus());
                String bookContent = bookResult.getResponse().getContentAsString();
                assertNotNull(bookContent);

                ObjectMapper objectMapper = new ObjectMapper();
                Book book = objectMapper.readValue(bookContent, Book.class);
                assertNotNull(book.getId());
                // Create 4 patrons
                List<Patron> patrons = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                        MvcResult patronResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                                        .contentType("application/json")
                                        .content("{\"name\":\"Patron " + (i + 1)
                                                        + "\",\"phoneNumber\":\"123456789\",\"email\":\"patron"
                                                        + (i + 1) + "@example.com\",\"address\":\"Address " + (i + 1)
                                                        + "\"}")
                                        .accept("application/json"))
                                        .andReturn();
                        assertEquals(HttpStatus.CREATED.value(), patronResult.getResponse().getStatus());
                        String patronContent = patronResult.getResponse().getContentAsString();
                        assertNotNull(patronContent);
                        Patron createdPatron = objectMapper.readValue(patronContent, Patron.class);
                        assertNotNull(createdPatron.getId());
                        patrons.add(createdPatron);
                }
                // Let 3 patrons borrow the book
                for (int i = 0; i < 3; i++) {
                        MvcResult borrowingResult = mockMvc
                                        .perform(MockMvcRequestBuilders
                                                        .post("/api/borrow/" + book.getId() + "/patron/"
                                                                        + patrons.get(i).getId())
                                                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                        .accept("application/json"))
                                        .andReturn();
                        assertEquals(HttpStatus.OK.value(), borrowingResult.getResponse().getStatus());
                        String borrowingContent = borrowingResult.getResponse().getContentAsString();
                        assertEquals("Book borrowed successfully", borrowingContent);
                }
                // Try to borrow the book with the 4th patron
                MvcResult borrowingResult = mockMvc
                                .perform(
                                                MockMvcRequestBuilders
                                                                .post("/api/borrow/" + book.getId() + "/patron/"
                                                                                + patrons.get(3).getId())
                                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.BAD_REQUEST.value(), borrowingResult.getResponse().getStatus());
                String borrowingContent = borrowingResult.getResponse().getContentAsString();
                assertEquals("Book is not available for borrowing", borrowingContent);

                // Return the book with the 1st patron
                MvcResult returnResult = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .put("/api/return/" + book.getId() + "/patron/"
                                                                + patrons.get(0).getId())
                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.OK.value(), returnResult.getResponse().getStatus());

                // Try to borrow the book again with the 4th patron
                borrowingResult = mockMvc
                                .perform(
                                                MockMvcRequestBuilders
                                                                .post("/api/borrow/" + book.getId() + "/patron/"
                                                                                + patrons.get(3).getId())
                                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.OK.value(), borrowingResult.getResponse().getStatus());
                borrowingContent = borrowingResult.getResponse().getContentAsString();
                assertEquals("Book borrowed successfully", borrowingContent);
        }
}
