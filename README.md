# Library Management System Documentation

## Introduction

The Library Management System is a web application designed to help libraries manage their resources efficiently. It provides features for managing books, patrons, borrowing, returning, and more.
This project provides a REST API that serves as a backend for such systems.
## Features

- **Book Management**: Allows users to add, update, delete, and search for books in the library inventory.
- **Patron Management**: Enables the management of patron information, including registration and membership details.
- **Borrowing and Returning**: Facilitate borrowing and returning of books by patrons.
- **Caching**: Utilizes Spring's caching mechanisms to cache frequently accessed data, such as book details, improving system performance by reducing database load.
- **JWT Security**: Implements JSON Web Token (JWT) security to authenticate and authorize users accessing the system, ensuring data privacy and security.
- **Aspect-Oriented Logging**: Implement logging using Aspect-Oriented Programming (AOP) to log method calls, exceptions etc.
- **Transactional**: Treats complex methods as transactions, ensuring that their results are rolled back from the database in case of any exceptions, maintaining data integrity.

# Project Structure
* **src/main/java**: Contains the source code for the application.
* **com.spring.library.controller**: Contains controllers for handling HTTP requests.
* **com.spring.library.model**: Defines the data models used in the application.
* **com.spring.library.repository**: Contains repositories for accessing data from the database.
* **com.spring.library.service**: Implements the business logic of the application.
* **src/test/java**: Contains unit tests for the application.


## Installation

1. Clone the repository to your local machine.
2. Ensure you have Java Development Kit (JDK) installed.
3. Install Apache Maven for dependency management.
4. Configure the `application.properties` file with the necessary MySQL database credentials (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`).
5. Run the application using Maven: `mvn spring-boot:run`.
6. Access the application at `http://localhost:8080`.

## Usage and Authorization

1. Once the application is running, access the provided endpoints using tools like Postman or cURL.
2. View the [Postman collection](https://solar-equinox-10639.postman.co/workspace/My-Workspace~b48aa230-b9b6-4351-89d5-300772555557/collection/26629596-39ce9aff-0ba7-42a1-9a3a-6e71f52b63f5?action=share&creator=26629596) containing requests to test all API endpoints.
3. Since new users cannot be added to the system directly, mock the login process by sending a POST request to the `/rest/auth/login` endpoint with the following body:

url: http://localhost:8080/rest/auth/login
methods: POST
body :  {
    "email": "admin@example.com",
    "password": "123456"
}
Once authenticated, use the received token as the value for the `Authorization` header in subsequent requests.
Authorization: Bearer <Token>
4. Register patrons, add books to the inventory, and manage borrowing and returning of books as needed.

## Testing
The project includes several unit and integration tests to ensure its functionality:

Under **src/test/java** there are 6 Testing Classes:
1. **BookControllerTest** : this class is responsible for mocking and testing all the book end points:
- the first Test testcreatebook_fail ensure the functionality of all the validation constraints added to book entity and of the compatibility of generated error messages at the end a book with valid data will be created.
- the second test testUpdateBookWithInvalidData : successfully create a book then try to alter this book with invalid data, and assert the response of this end point, then delete the book, and make sure it's no longer here, when the second attempt to delete generate 404 Not Found.
2. **BookServiceTest**: this class test the book service for a reasonnable behaviour: 
- create a book
- get all books
- get this specific book
- update the book 
- delete this book
- try to get this book by id and expect 404 Not Found.
3. **PatronControllerTest**: this class mock the controller for patron transactions and make sure validations are respected for Patron entity:
- Name is required
- Phone number must be 9 digits
- Invalid email format should be valid
and same for updating a patron
5. **PatronServiceTest**: this class is testing the service regarding a chain of patron requests: 
- create a patron
- get all patrons
- get this specific patron
- update the patron 
- delete this patron
- try to get this patron by id and expect 404 Not Found.
6. **NormalBorrowingRecordControllerTest**: Tests the borrowing and returning of books under normal circumstances.
- create a book with available number of copies > 0
- create a patron
- let this patron borrow the created book
- return it successfully
7. **BottleneckBorrowingRecordControllerTest**: Tests the behavior of the system under certain edge cases, such as limited book availability.
- create a book with 3 available copies
- create 4 patrons
- 3 of these patron borrow this book
- the fourth patron will get : Book is not available for borrowing
- when one of these 3 patrons return a book the 4 patron will be able to borrow this book.
8. **running tests** : run these tests with this command : `mvn test`.
