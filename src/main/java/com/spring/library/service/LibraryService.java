package com.spring.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.spring.library.model.Book;
import com.spring.library.model.BorrowingRecord;
import com.spring.library.model.Patron;
import com.spring.library.repository.BookRepository;
import com.spring.library.repository.BorrowingRecordRepository;
import com.spring.library.repository.PatronRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class LibraryService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Transactional
    public ResponseEntity<String> borrowBook(Long bookId, Long patronId) {
        // Step 1: Check if the provided book ID and patron ID are valid.
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<Patron> optionalPatron = patronRepository.findById(patronId);

        if (optionalBook.isEmpty() || optionalPatron.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book book = optionalBook.get();
        Patron patron = optionalPatron.get();

        // Step 2: Ensure that the book is available for borrowing.
        if (bookRepository.findAvailableCopiesByBookId(bookId) == 0) {
            return ResponseEntity.badRequest().body("Book is not available for borrowing");
        }

        // Step 3: Create a new borrowing record associating the book with the patron.
        BorrowingRecord borrowingRecord = new BorrowingRecord(book, patron);
        borrowingRecordRepository.save(borrowingRecord);

        // Step 5: Return a response indicating the success of the borrowing operation.

        return ResponseEntity.ok("Book borrowed successfully");
    }

    @Transactional
    public ResponseEntity<String> returnBook(Long bookId, Long patronId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<Patron> optionalPatron = patronRepository.findById(patronId);

        if (optionalBook.isPresent() && optionalPatron.isPresent()) {
            Book book = optionalBook.get();

            // Find and mark the borrowing record as returned
            Optional<BorrowingRecord> optionalBorrowingRecord = borrowingRecordRepository
                    .findOldestBorrowingRecord(bookId, patronId);
            System.out.println("optionalBorrowingRecord" + optionalBorrowingRecord);
            // borrowingRecordRepository.findByBookIdAndPatronIdAndReturnDateIsNull(bookId,
            // patronId);
            if (optionalBorrowingRecord.isPresent()) {
                BorrowingRecord borrowingRecord = optionalBorrowingRecord.get();
                borrowingRecord.setReturnDate(LocalDate.now());
                borrowingRecordRepository.save(borrowingRecord);
                return ResponseEntity.ok("Book returned successfully");
            } else {
                return ResponseEntity.badRequest().body("No active borrowing record found");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<Book>> getAllBooks() {

        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    @Cacheable(value = "books", key = "#id")
    public ResponseEntity<Book> getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Object> addBook(@Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @CachePut(value = "books", key = "#id")
    @Transactional
    public ResponseEntity<Object> updateBook(Long id, @Valid Book book, BindingResult bindingResult) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
            }

            book.setId(id);
            Book updatedBook = bookRepository.save(book);
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CacheEvict(value = "books", key = "#id")
    @Transactional
    public ResponseEntity<Void> deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<Patron>> getAllPatrons() {
        List<Patron> patrons = patronRepository.findAll();
        return ResponseEntity.ok(patrons);
    }

    public ResponseEntity<Patron> getPatronById(Long id) {
        Optional<Patron> optionalPatron = patronRepository.findById(id);
        return optionalPatron.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Object> addPatron(@Valid Patron patron, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }

        Patron savedPatron = patronRepository.save(patron);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatron);
    }

    @Transactional
    public ResponseEntity<Object> updatePatron(Long id, @Valid Patron patron, BindingResult bindingResult) {
        Optional<Patron> optionalPatron = patronRepository.findById(id);
        if (optionalPatron.isPresent()) {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
            }

            patron.setId(id);
            Patron updatedPatron = patronRepository.save(patron);
            return ResponseEntity.ok(updatedPatron);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deletePatron(Long id) {
        if (patronRepository.existsById(id)) {
            patronRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
