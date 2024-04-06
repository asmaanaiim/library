package com.spring.library.repository;

import com.spring.library.model.Book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Define custom query methods if needed
    // Example:
    // List<Book> findByTitle(String title);
    List<Book> findAll(); // Retrieve a list of all books
    Book findById(long id); // Retrieve details of a specific book by ID
      @Query("SELECT (b.availableCopies - COUNT(br)) FROM Book b " +
           "LEFT JOIN BorrowingRecord br ON b.id = br.book.id AND br.returnDate IS NULL " +
           "WHERE b.id = :bookId " +
           "GROUP BY b.id")
    Long findAvailableCopiesByBookId(@Param("bookId") Long bookId);
}
