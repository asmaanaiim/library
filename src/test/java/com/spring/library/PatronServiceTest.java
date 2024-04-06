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

import com.spring.library.model.Patron;
import com.spring.library.service.LibraryService;

@SpringBootTest
public class PatronServiceTest {

    @Autowired
    private LibraryService libraryService;

    @Test
    public void testCRUDOperations() {
        // Create
        Patron patron = new Patron("John Doe", "123456789", "john@example.com", "123 Main St");

        BindingResult bindingResult = mock(BindingResult.class);

        ResponseEntity<Object> response = libraryService.addPatron(patron, bindingResult);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Patron createdPatron = (Patron) response.getBody();
        assertNotNull(createdPatron.getId());

        Long patronId = createdPatron.getId();

        // Get All
        ResponseEntity<List<Patron>> getAllResponse = libraryService.getAllPatrons();
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        List<Patron> allPatrons = getAllResponse.getBody();
        assertTrue(allPatrons.size() > 0);

        // Get by ID
        ResponseEntity<Patron> getByIdResponse = libraryService.getPatronById(patronId);
        assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
        assertNotNull(getByIdResponse.getBody());
        Patron retrievedPatron = getByIdResponse.getBody();
        assertEquals(patronId, retrievedPatron.getId());

        // Update
        retrievedPatron.setName("Jane Doe");
        ResponseEntity<Object> updateResponse = libraryService.updatePatron(patronId, retrievedPatron, bindingResult);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        Patron updatedPatron = (Patron) updateResponse.getBody();
        assertEquals("Jane Doe", updatedPatron.getName());

        // Delete
        ResponseEntity<Void> deleteResponse = libraryService.deletePatron(patronId);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify deletion
        ResponseEntity<Patron> verifyDeletedResponse = libraryService.getPatronById(patronId);
        assertEquals(HttpStatus.NOT_FOUND, verifyDeletedResponse.getStatusCode());
    }
}
