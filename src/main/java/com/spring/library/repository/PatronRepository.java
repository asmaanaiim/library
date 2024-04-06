package com.spring.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.library.model.Patron;

@Repository
public interface PatronRepository extends JpaRepository<Patron, Long> {
    List<Patron> findAll(); // Retrieve a list of all patrons
    Patron findById(long id); // Retrieve details of a specific patron by ID
}
