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
import com.spring.library.model.Book;
import com.spring.library.model.Patron;

@SpringBootTest
@AutoConfigureMockMvc
public class NormalBorrowingRecordControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testCreateAndReturnBorrowingRecord() throws Exception {
                // Create a book
                Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald", "1925", "9780743273564", 10);
                MvcResult bookResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .contentType("application/json")
                                .content(
                                                "{\"title\":\"The Great Gatsby\",\"author\":\"F. Scott Fitzgerald\",\"publicationYear\":\"1925\",\"isbn\":\"9780743273564\",\"availableCopies\":10}")
                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.CREATED.value(), bookResult.getResponse().getStatus());
                String bookContent = bookResult.getResponse().getContentAsString();
                assertNotNull(bookContent);
                // Parse the response to get the created book
                ObjectMapper objectMapper = new ObjectMapper();
                Book createdBook = objectMapper.readValue(bookContent, Book.class);
                assertNotNull(createdBook.getId());

                // Create a patron
                Patron patron = new Patron("John Doe", "123456789", "john@example.com", "123 Main St");
                MvcResult patronResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .contentType("application/json")
                                .content(
                                                "{\"name\":\"John Doe\",\"phoneNumber\":\"123456789\",\"email\":\"john@example.com\",\"address\":\"123 Main St\"}")
                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.CREATED.value(), patronResult.getResponse().getStatus());
                String patronContent = patronResult.getResponse().getContentAsString();
                assertNotNull(patronContent);
                // Parse the response to get the created book
                Patron createdPatron = objectMapper.readValue(patronContent, Patron.class);
                assertNotNull(createdPatron.getId());

                // Create borrowing record
                MvcResult borrowingRecordResult = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .post("/api/borrow/" + createdBook.getId() + "/patron/"
                                                                + createdPatron.getId())
                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.OK.value(), borrowingRecordResult.getResponse().getStatus());
                String borrowingRecordContent = borrowingRecordResult.getResponse().getContentAsString();
                assertNotNull(borrowingRecordContent);

                // Return borrowing record
                MvcResult returnResult = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .put("/api/return/" + createdBook.getId() + "/patron/"
                                                                + createdPatron.getId())
                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.OK.value(), returnResult.getResponse().getStatus());

                // Return borrowing record again
                returnResult = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .put("/api/return/" + createdBook.getId() + "/patron/"
                                                                + createdPatron.getId())
                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.BAD_REQUEST.value(), returnResult.getResponse().getStatus());

                // Borrow a not existing book
                borrowingRecordResult = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .post("/api/borrow/98500020/patron/"
                                                                + createdPatron.getId())
                                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                                .accept("application/json"))
                                .andReturn();
                assertEquals(HttpStatus.NOT_FOUND.value(), borrowingRecordResult.getResponse().getStatus());

        }
}
