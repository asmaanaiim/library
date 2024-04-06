package com.spring.library.model;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Author is required")
    private String author;
    @NotBlank(message = "Publication year is required")
    @Pattern(regexp = "\\d{4}", message = "Publication year must be in YYYY format")
    private String publicationYear;
    private String isbn;
    private int availableCopies;    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowingRecord> borrowingRecords = new HashSet<>();

    // Constructors, Getters, and Setters
  // Default constructor
  public Book() {
}

// Parameterized constructor
public Book(String title, String author, String publicationYear, String isbn, int availableCopies) {
    this.title = title;
    this.author = author;
    this.publicationYear = publicationYear;
    this.isbn = isbn;
    this.availableCopies = availableCopies;
}

// Getters and setters for all fields

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getTitle() {
    return title;
}

public void setTitle(String title) {
    this.title = title;
}

public String getAuthor() {
    return author;
}

public void setAuthor(String author) {
    this.author = author;
}

public String getPublicationYear() {
    return publicationYear;
}

public void setPublicationYear(String publicationYear) {
    this.publicationYear = publicationYear;
}

public String getIsbn() {
    return isbn;
}

public void setIsbn(String isbn) {
    this.isbn = isbn;
}
public int getAvailableCopies() {
    return availableCopies;
}

public void setAvailableCopies(int availableCopies) {
    this.availableCopies = availableCopies;
}
 // toString() method to print the object
 @Override
 public String toString() {
     return "Book{" +
             "id=" + id +
             ", title='" + title + '\'' +
             ", author='" + author + '\'' +
             ", publicationYear=" + publicationYear +
             ", ISBN='" + isbn + '\'' +
             ", availableCopies=" + availableCopies +
             '}';
 }
}
