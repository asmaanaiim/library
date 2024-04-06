package com.spring.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.library.model.Book;
import com.spring.library.model.Patron;
import com.spring.library.service.LibraryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;
    // Book management endpoints

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return libraryService.getBookById(id);

    }

    @PostMapping("/books")
    public ResponseEntity<Object> addBook(@Valid @RequestBody Book book, BindingResult bindingResult) {

        return libraryService.addBook(book, bindingResult);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @Valid @RequestBody Book book,
            BindingResult bindingResult) {

        return libraryService.updateBook(id, book, bindingResult);

    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        return libraryService.deleteBook(id);
    }
    // Patron management endpoints

    @GetMapping("/patrons")
    public ResponseEntity<List<Patron>> getAllPatrons() {
        return libraryService.getAllPatrons();
    }

    @GetMapping("/patrons/{id}")
    public ResponseEntity<Patron> getPatronById(@PathVariable Long id) {
        return libraryService.getPatronById(id);
    }

    @PostMapping("/patrons")
    public ResponseEntity<Object> addPatron(@Valid @RequestBody Patron patron, BindingResult bindingResult) {
        return libraryService.addPatron(patron, bindingResult);
    }

    @PutMapping("/patrons/{id}")
    public ResponseEntity<Object> updatePatron(@PathVariable Long id, @Valid @RequestBody Patron patron,
            BindingResult bindingResult) {
        return libraryService.updatePatron(id, patron, bindingResult);
    }

    @DeleteMapping("/patrons/{id}")
    public ResponseEntity<Void> deletePatron(@PathVariable Long id) {
        return libraryService.deletePatron(id);
    }

    // Borrowing endpoints
    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        return libraryService.borrowBook(bookId, patronId);
    }

    // Return endpoints
    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        return libraryService.returnBook(bookId, patronId);
    }
}
