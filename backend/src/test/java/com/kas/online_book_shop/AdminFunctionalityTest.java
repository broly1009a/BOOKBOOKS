package com.kas.online_book_shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kas.online_book_shop.controller.LanguageController;
import com.kas.online_book_shop.controller.PublisherController;
import com.kas.online_book_shop.model.Language;
import com.kas.online_book_shop.model.Publisher;
import com.kas.online_book_shop.service.LanguageService;
import com.kas.online_book_shop.service.PublisherService;

/**
 * Unit Test cho Admin - Cường
 * Chức năng: Language/Publisher
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Functionality Test - Cường")
public class AdminFunctionalityTest {

    @Mock
    private LanguageService languageService;

    @Mock
    private PublisherService publisherService;

    @InjectMocks
    private LanguageController languageController;

    @InjectMocks
    private PublisherController publisherController;

    private Language testLanguage;
    private Publisher testPublisher;

    @BeforeEach
    void setUp() {
        // Setup Language
        testLanguage = new Language();
        testLanguage.setId(1L);
        testLanguage.setName("Vietnamese");

        // Setup Publisher
        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");
        testPublisher.setWebsite("https://publisher.example.com");
    }

    // ==================== LANGUAGE TESTS ====================

    @Test
    @DisplayName("Test 1: Get All Languages - Lấy danh sách tất cả ngôn ngữ")
    void testGetAllLanguages() {
        // Given
        Language language2 = new Language();
        language2.setId(2L);
        language2.setName("English");

        List<Language> languages = Arrays.asList(testLanguage, language2);
        when(languageService.getAllLanguages()).thenReturn(languages);

        // When
        ResponseEntity<List<Language>> response = languageController.getLanguages();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Vietnamese", response.getBody().get(0).getName());
        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals("English", response.getBody().get(1).getName());
        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    @DisplayName("Test 2: Get Language by ID - Lấy ngôn ngữ theo ID")
    void testGetLanguageById() {
        // Given
        when(languageService.getLanguageById(anyLong())).thenReturn(testLanguage);

        // When
        ResponseEntity<Language> response = languageController.getLanguageById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Vietnamese", response.getBody().getName());
        verify(languageService, times(1)).getLanguageById(1L);
    }

    @Test
    @DisplayName("Test 3: Add Language - Thêm ngôn ngữ mới thành công")
    void testAddLanguageSuccess() {
        // Given
        when(languageService.saveLanguage(any(Language.class))).thenReturn(testLanguage);

        // When
        ResponseEntity<Language> response = languageController.saveLanguage(testLanguage);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Vietnamese", response.getBody().getName());
        verify(languageService, times(1)).saveLanguage(any(Language.class));
    }

    @Test
    @DisplayName("Test 4: Update Language - Cập nhật ngôn ngữ thành công")
    void testUpdateLanguageSuccess() {
        // Given
        testLanguage.setName("Tiếng Việt");
        when(languageService.updateLanguage(any(Language.class))).thenReturn(testLanguage);

        // When
        ResponseEntity<Language> response = languageController.updateLanguage(testLanguage);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tiếng Việt", response.getBody().getName());
        verify(languageService, times(1)).updateLanguage(any(Language.class));
    }

    @Test
    @DisplayName("Test 5: Delete Language - Xóa ngôn ngữ thành công")
    void testDeleteLanguageSuccess() {
        // Given
        doNothing().when(languageService).deleteLanguage(anyLong());

        // When
        ResponseEntity<Void> response = languageController.deleteLanguage(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(languageService, times(1)).deleteLanguage(1L);
    }

    @Test
    @DisplayName("Test 6: Get Language by ID - Ngôn ngữ không tồn tại")
    void testGetLanguageByIdNotFound() {
        // Given
        when(languageService.getLanguageById(999L)).thenReturn(null);

        // When
        ResponseEntity<Language> response = languageController.getLanguageById(999L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(languageService, times(1)).getLanguageById(999L);
    }

    @Test
    @DisplayName("Test 7: Get All Languages - Danh sách rỗng")
    void testGetAllLanguagesEmpty() {
        // Given
        when(languageService.getAllLanguages()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Language>> response = languageController.getLanguages();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    @DisplayName("Test 8: Add Multiple Languages - Thêm nhiều ngôn ngữ")
    void testAddMultipleLanguages() {
        // Given
        Language language2 = new Language();
        language2.setId(2L);
        language2.setName("English");

        when(languageService.saveLanguage(any(Language.class)))
                .thenReturn(testLanguage)
                .thenReturn(language2);

        // When - Add first language
        ResponseEntity<Language> response1 = languageController.saveLanguage(testLanguage);
        // Add second language
        ResponseEntity<Language> response2 = languageController.saveLanguage(language2);

        // Then
        assertNotNull(response1);
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals("Vietnamese", Objects.requireNonNull(response1.getBody()).getName());
        
        assertNotNull(response2);
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());
        assertEquals("English", Objects.requireNonNull(response2.getBody()).getName());
        
        verify(languageService, times(2)).saveLanguage(any(Language.class));
    }

    // ==================== PUBLISHER TESTS ====================

    @Test
    @DisplayName("Test 9: Get All Publishers - Lấy danh sách tất cả nhà xuất bản")
    void testGetAllPublishers() {
        // Given
        Publisher publisher2 = new Publisher();
        publisher2.setId(2L);
        publisher2.setName("Second Publisher");
        publisher2.setWebsite("https://second-publisher.com");

        List<Publisher> publishers = Arrays.asList(testPublisher, publisher2);
        when(publisherService.getAllPublishers()).thenReturn(publishers);

        // When
        ResponseEntity<List<Publisher>> response = publisherController.getPublishers();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Test Publisher", response.getBody().get(0).getName());
        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals("Second Publisher", response.getBody().get(1).getName());
        verify(publisherService, times(1)).getAllPublishers();
    }

    @Test
    @DisplayName("Test 10: Get Publisher by ID - Lấy nhà xuất bản theo ID")
    void testGetPublisherById() {
        // Given
        when(publisherService.getPublisherById(anyLong())).thenReturn(testPublisher);

        // When
        ResponseEntity<Publisher> response = publisherController.getPublisherById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Publisher", response.getBody().getName());
        assertEquals("https://publisher.example.com", response.getBody().getWebsite());
        verify(publisherService, times(1)).getPublisherById(1L);
    }

    @Test
    @DisplayName("Test 11: Add Publisher - Thêm nhà xuất bản mới thành công")
    void testAddPublisherSuccess() {
        // Given
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When
        ResponseEntity<Publisher> response = publisherController.savePublisher(testPublisher);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Publisher", response.getBody().getName());
        assertEquals("https://publisher.example.com", response.getBody().getWebsite());
        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 12: Update Publisher - Cập nhật nhà xuất bản thành công")
    void testUpdatePublisherSuccess() {
        // Given
        testPublisher.setName("Updated Publisher Name");
        testPublisher.setWebsite("https://updated-publisher.com");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When
        ResponseEntity<Publisher> response = publisherController.updatePublisher(testPublisher);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Publisher Name", response.getBody().getName());
        assertEquals("https://updated-publisher.com", response.getBody().getWebsite());
        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 13: Delete Publisher - Xóa nhà xuất bản thành công")
    void testDeletePublisherSuccess() {
        // Given
        doNothing().when(publisherService).deletePublisher(anyLong());

        // When
        ResponseEntity<Void> response = publisherController.deletePublisher(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(publisherService, times(1)).deletePublisher(1L);
    }

    @Test
    @DisplayName("Test 14: Get Publisher by ID - Nhà xuất bản không tồn tại")
    void testGetPublisherByIdNotFound() {
        // Given
        when(publisherService.getPublisherById(999L)).thenReturn(null);

        // When
        ResponseEntity<Publisher> response = publisherController.getPublisherById(999L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(publisherService, times(1)).getPublisherById(999L);
    }

    @Test
    @DisplayName("Test 15: Get All Publishers - Danh sách rỗng")
    void testGetAllPublishersEmpty() {
        // Given
        when(publisherService.getAllPublishers()).thenReturn(null);

        // When
        ResponseEntity<List<Publisher>> response = publisherController.getPublishers();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(publisherService, times(1)).getAllPublishers();
    }

    @Test
    @DisplayName("Test 16: Update Publisher Website - Cập nhật website")
    void testUpdatePublisherWebsite() {
        // Given
        testPublisher.setWebsite("https://new-publisher-site.com");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When
        ResponseEntity<Publisher> response = publisherController.updatePublisher(testPublisher);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://new-publisher-site.com", response.getBody().getWebsite());
        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 17: Add Publisher - Thêm nhà xuất bản với thông tin đầy đủ")
    void testAddPublisherWithFullInfo() {
        // Given
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When
        ResponseEntity<Publisher> response = publisherController.savePublisher(testPublisher);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getName());
        assertNotNull(response.getBody().getWebsite());
        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 18: Language CRUD Operations - Kiểm tra chuỗi thao tác CRUD")
    void testLanguageCRUDOperations() {
        // Create
        when(languageService.saveLanguage(any(Language.class))).thenReturn(testLanguage);
        ResponseEntity<Language> createResponse = languageController.saveLanguage(testLanguage);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Read
        when(languageService.getLanguageById(1L)).thenReturn(testLanguage);
        ResponseEntity<Language> readResponse = languageController.getLanguageById(1L);
        assertEquals(HttpStatus.OK, readResponse.getStatusCode());

        // Update
        testLanguage.setName("Updated Language");
        when(languageService.updateLanguage(any(Language.class))).thenReturn(testLanguage);
        ResponseEntity<Language> updateResponse = languageController.updateLanguage(testLanguage);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // Delete
        doNothing().when(languageService).deleteLanguage(1L);
        ResponseEntity<Void> deleteResponse = languageController.deleteLanguage(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify all operations
        verify(languageService, times(1)).saveLanguage(any(Language.class));
        verify(languageService, times(1)).getLanguageById(1L);
        verify(languageService, times(1)).updateLanguage(any(Language.class));
        verify(languageService, times(1)).deleteLanguage(1L);
    }

    @Test
    @DisplayName("Test 19: Publisher CRUD Operations - Kiểm tra chuỗi thao tác CRUD")
    void testPublisherCRUDOperations() {
        // Create
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);
        ResponseEntity<Publisher> createResponse = publisherController.savePublisher(testPublisher);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Read
        when(publisherService.getPublisherById(1L)).thenReturn(testPublisher);
        ResponseEntity<Publisher> readResponse = publisherController.getPublisherById(1L);
        assertEquals(HttpStatus.OK, readResponse.getStatusCode());

        // Update
        testPublisher.setName("Updated Publisher");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);
        ResponseEntity<Publisher> updateResponse = publisherController.updatePublisher(testPublisher);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // Delete
        doNothing().when(publisherService).deletePublisher(1L);
        ResponseEntity<Void> deleteResponse = publisherController.deletePublisher(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify all operations
        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
        verify(publisherService, times(1)).getPublisherById(1L);
        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
        verify(publisherService, times(1)).deletePublisher(1L);
    }

    @Test
    @DisplayName("Test 20: Delete Non-existent Publisher - Xóa nhà xuất bản không tồn tại")
    void testDeleteNonExistentPublisher() {
        // Given
        doNothing().when(publisherService).deletePublisher(999L);

        // When
        ResponseEntity<Void> response = publisherController.deletePublisher(999L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(publisherService, times(1)).deletePublisher(999L);
    }
}
