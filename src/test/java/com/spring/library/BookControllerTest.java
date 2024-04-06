package com.spring.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import com.spring.library.ObtainAccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.library.model.Book;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testcreatebook_fail() throws Exception {

                // String token = ObtainAccessToken.obtainToken("asmaa@email.com", "123456");
                // Create a Book with empty title
                Book bookWithEmptyTitle = new Book();
                bookWithEmptyTitle.setAuthor("F. Scott Fitzgerald");
                bookWithEmptyTitle.setPublicationYear("1925");
                bookWithEmptyTitle.setIsbn("9780743273564");
                bookWithEmptyTitle.setAvailableCopies(10);

                // Convert Book object to JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                String bookJson = objectMapper.writeValueAsString(bookWithEmptyTitle);

                // Perform POST request to add Book with empty title
                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(bookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify response is BadRequest and contains expected error message
                assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
                assertNotNull(mvcResult.getResponse().getContentAsString());
                String errorMessage = mvcResult.getResponse().getContentAsString();
                assertEquals("Title is required", errorMessage);

                // Test author validation constraint
                Book bookWithEmptyAuthor = new Book();
                bookWithEmptyAuthor.setTitle("The Great Gatsby");
                bookWithEmptyAuthor.setPublicationYear("1925");
                bookWithEmptyAuthor.setIsbn("9780743273564");
                bookWithEmptyAuthor.setAvailableCopies(10);

                String bookJsonWithEmptyAuthor = objectMapper.writeValueAsString(bookWithEmptyAuthor);

                mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(bookJsonWithEmptyAuthor)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
                assertNotNull(mvcResult.getResponse().getContentAsString());
                errorMessage = mvcResult.getResponse().getContentAsString();
                assertEquals("Author is required", errorMessage);
                // Test publicationYear validation constraint with invalid format
                Book bookWithInvalidPublicationYear = new Book();
                bookWithInvalidPublicationYear.setTitle("To Kill a Mockingbird");
                bookWithInvalidPublicationYear.setAuthor("Harper Lee");
                bookWithInvalidPublicationYear.setPublicationYear("19XX"); // Invalid format
                bookWithInvalidPublicationYear.setIsbn("9780446310789");
                bookWithInvalidPublicationYear.setAvailableCopies(5);

                String bookJsonWithInvalidPublicationYear = objectMapper
                                .writeValueAsString(bookWithInvalidPublicationYear);

                mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(bookJsonWithInvalidPublicationYear)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
                assertNotNull(mvcResult.getResponse().getContentAsString());
                errorMessage = mvcResult.getResponse().getContentAsString();
                assertEquals("Publication year must be in YYYY format", errorMessage);
                // Create a Book with valid data
                Book bookWithValidData = new Book();
                bookWithValidData.setTitle("1984");
                bookWithValidData.setAuthor("George Orwell");
                bookWithValidData.setPublicationYear("1949");
                bookWithValidData.setIsbn("9780451524935");
                bookWithValidData.setAvailableCopies(10);

                // Convert Book object to JSON string
                objectMapper = new ObjectMapper();
                bookJson = objectMapper.writeValueAsString(bookWithValidData);

                // Perform POST request to add Book with valid data
                mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(bookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify response is Created and contains the created Book
                assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
                assertNotNull(mvcResult.getResponse().getContentAsString());
                Book getBook = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Book.class);
                assertNotNull(getBook.getId());

        }

        @Test
        public void testUpdateBookWithInvalidData() throws Exception {
                // Create a Book
                Book book = new Book();
                book.setTitle("Pride and Prejudice");
                book.setAuthor("Jane Austen");
                book.setPublicationYear("1813");
                book.setIsbn("9780141439518");
                book.setAvailableCopies(8);

                // Convert Book object to JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                String bookJson = objectMapper.writeValueAsString(book);

                // Perform POST request to add the Book
                MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(bookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify that book creation was successful
                assertEquals(HttpStatus.CREATED.value(), postResult.getResponse().getStatus());
                String postResponseContent = postResult.getResponse().getContentAsString();
                assertNotNull(postResponseContent);
                Book createdBook = objectMapper.readValue(postResponseContent, Book.class);
                assertNotNull(createdBook.getId());

                // Update the Book with an invalid year
                createdBook.setPublicationYear("11111");
                String updatedBookJson = objectMapper.writeValueAsString(createdBook);

                // Perform PUT request to update the Book with an invalid year
                MvcResult putResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/books/" + createdBook.getId())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(updatedBookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify that the update failed due to an invalid year
                assertEquals(HttpStatus.BAD_REQUEST.value(), putResult.getResponse().getStatus());
                String putResponseContent = putResult.getResponse().getContentAsString();
                assertNotNull(putResponseContent);
                assertEquals("Publication year must be in YYYY format", putResponseContent);

                // Update the Book with an blank title
                book.setPublicationYear("1813");
                book.setTitle("");
                updatedBookJson = objectMapper.writeValueAsString(book);

                // Perform PUT request to update the Book with an empty title
                putResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/books/" + createdBook.getId())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(updatedBookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify that the update failed due to an empty title
                assertEquals(HttpStatus.BAD_REQUEST.value(), putResult.getResponse().getStatus());
                putResponseContent = putResult.getResponse().getContentAsString();
                assertNotNull(putResponseContent);
                assertEquals("Title is required", putResponseContent);

                // Update the Book with an blank author
                book.setTitle("Pride and Prejudice");
                book.setAuthor("");
                updatedBookJson = objectMapper.writeValueAsString(book);

                // Perform PUT request to update the Book with an empty author
                putResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/books/" + createdBook.getId())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(updatedBookJson)
                                .contentType("application/json")
                                .accept("application/json"))
                                .andReturn();

                // Verify that the update failed due to an empty author
                assertEquals(HttpStatus.BAD_REQUEST.value(), putResult.getResponse().getStatus());
                putResponseContent = putResult.getResponse().getContentAsString();
                assertNotNull(putResponseContent);
                assertEquals("Author is required", putResponseContent);
                // Delete the book
                MvcResult deleteresult;
                if (createdBook.getId() != null) {
                        deleteresult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/" + createdBook
                                        .getId()).with(SecurityMockMvcRequestPostProcessors.jwt())).andReturn();
                        assertEquals(HttpStatus.NO_CONTENT.value(), deleteresult.getResponse().getStatus());

                }
                // Try to Redelete it and get not found
                deleteresult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/" + createdBook
                                .getId()).with(SecurityMockMvcRequestPostProcessors.jwt())).andReturn();
                assertEquals(HttpStatus.NOT_FOUND.value(), deleteresult.getResponse().getStatus());

                // Try to Redelete it and get the book and do not find it
                MvcResult getresult = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/" + createdBook
                                .getId()).with(SecurityMockMvcRequestPostProcessors.jwt())).andReturn();
                assertEquals(HttpStatus.NOT_FOUND.value(), getresult.getResponse().getStatus());

        }

}
