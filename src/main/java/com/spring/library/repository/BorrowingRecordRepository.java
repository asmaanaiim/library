package com.spring.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.library.model.BorrowingRecord;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
    // Define custom query methods if needed
    List<BorrowingRecord> findByBookIdAndPatronId(long bookId, long patronId); // Method to find borrowing record by bookId and patronId
    @Query("SELECT br " + 
        "FROM BorrowingRecord br " + 
        "WHERE br.book.id = :bookId " + 
        "AND br.patron.id = :patronId " + 
        "AND br.returnDate IS NULL " + 
        "ORDER BY br.borrowDate ASC LIMIT 1")
    Optional<BorrowingRecord> findOldestBorrowingRecord(@Param("bookId") Long bookId, @Param("patronId") Long patronId);

    


}
