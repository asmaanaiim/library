package com.spring.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.spring.library.model.Book;
import com.spring.library.service.LibraryService;

@SpringBootTest
public class BookServiceTest {

        @Autowired
        private LibraryService libraryService;

        @Test
        public void testCRUDOperations() {
                // Create
                Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald", "1925", "9780743273564", 10);
                BindingResult bindingResult = mock(BindingResult.class);
                ResponseEntity<Object> response = libraryService.addBook(book, bindingResult);
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertNotNull(response.getBody());
                Book createdBook = (Book) response.getBody();
                assertNotNull(createdBook.getId());

                Long bookId = createdBook.getId();

                // Get All
                ResponseEntity<List<Book>> getAllResponse = libraryService.getAllBooks();
                assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
                List<Book> allBooks = getAllResponse.getBody();
                assertTrue(allBooks.size() > 0);

                // Get by ID
                ResponseEntity<Book> getByIdResponse = libraryService.getBookById(bookId);
                assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
                assertNotNull(getByIdResponse.getBody());
                Book retrievedBook = getByIdResponse.getBody();
                assertEquals(bookId, retrievedBook.getId());

                // Update
                retrievedBook.setTitle("Updated Title");
                ResponseEntity<Object> updateResponse = libraryService.updateBook(bookId, retrievedBook, bindingResult);
                assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
                assertNotNull(updateResponse.getBody());
                Book updatedBook = (Book) updateResponse.getBody();
                assertEquals("Updated Title", updatedBook.getTitle());

                // Delete
                ResponseEntity<Void> deleteResponse = libraryService.deleteBook(bookId);
                assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

                // Verify deletion
                ResponseEntity<Book> verifyDeletedResponse = libraryService.getBookById(bookId);
                assertEquals(HttpStatus.NOT_FOUND, verifyDeletedResponse.getStatusCode());
        }
}