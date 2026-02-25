package com.kas.online_book_shop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kas.online_book_shop.model.Language;
import com.kas.online_book_shop.model.Publisher;
import com.kas.online_book_shop.service.LanguageService;
import com.kas.online_book_shop.service.PublisherService;

/**
 * Unit Test cho Admin - Cường
 * Chức năng: Language/Publisher
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Admin Functionality Test - Cường")
public class AdminFunctionalityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private PublisherService publisherService;

    private Language testLanguage;
    private Publisher testPublisher;

    @BeforeEach
    void setUp() {
        // Setup Language
        testLanguage = new Language();
        testLanguage.setId(1L);
        testLanguage.setName("Vietnamese");
        testLanguage.setCode("vi");

        // Setup Publisher
        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");
        testPublisher.setAddress("123 Publisher Street");
        testPublisher.setEmail("publisher@example.com");
        testPublisher.setPhone("0123456789");
    }

    // ==================== LANGUAGE TESTS ====================

    @Test
    @DisplayName("Test 1: Get All Languages - Lấy danh sách tất cả ngôn ngữ")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllLanguages() throws Exception {
        // Given
        Language language2 = new Language();
        language2.setId(2L);
        language2.setName("English");
        language2.setCode("en");

        List<Language> languages = Arrays.asList(testLanguage, language2);
        when(languageService.getAllLanguages()).thenReturn(languages);

        // When & Then
        mockMvc.perform(get("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Vietnamese"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("English"));

        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    @DisplayName("Test 2: Get Language by ID - Lấy ngôn ngữ theo ID")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetLanguageById() throws Exception {
        // Given
        when(languageService.getLanguageById(anyLong())).thenReturn(testLanguage);

        // When & Then
        mockMvc.perform(get("/api/v1/language/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vietnamese"));

        verify(languageService, times(1)).getLanguageById(anyLong());
    }

    @Test
    @DisplayName("Test 3: Add Language - Thêm ngôn ngữ mới thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testAddLanguageSuccess() throws Exception {
        // Given
        when(languageService.saveLanguage(any(Language.class))).thenReturn(testLanguage);

        // When & Then
        mockMvc.perform(post("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLanguage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vietnamese"));

        verify(languageService, times(1)).saveLanguage(any(Language.class));
    }

    @Test
    @DisplayName("Test 4: Update Language - Cập nhật ngôn ngữ thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testUpdateLanguageSuccess() throws Exception {
        // Given
        testLanguage.setName("Tiếng Việt");
        when(languageService.updateLanguage(any(Language.class))).thenReturn(testLanguage);

        // When & Then
        mockMvc.perform(put("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLanguage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tiếng Việt"));

        verify(languageService, times(1)).updateLanguage(any(Language.class));
    }

    @Test
    @DisplayName("Test 5: Delete Language - Xóa ngôn ngữ thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testDeleteLanguageSuccess() throws Exception {
        // Given
        doNothing().when(languageService).deleteLanguage(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/language/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(languageService, times(1)).deleteLanguage(anyLong());
    }

    @Test
    @DisplayName("Test 6: Get Language by ID - Ngôn ngữ không tồn tại")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetLanguageByIdNotFound() throws Exception {
        // Given
        when(languageService.getLanguageById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/language/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(languageService, times(1)).getLanguageById(999L);
    }

    @Test
    @DisplayName("Test 7: Get All Languages - Danh sách rỗng")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllLanguagesEmpty() throws Exception {
        // Given
        when(languageService.getAllLanguages()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    @DisplayName("Test 8: Add Multiple Languages - Thêm nhiều ngôn ngữ")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testAddMultipleLanguages() throws Exception {
        // Given
        Language language2 = new Language();
        language2.setId(2L);
        language2.setName("English");
        language2.setCode("en");

        when(languageService.saveLanguage(any(Language.class)))
                .thenReturn(testLanguage)
                .thenReturn(language2);

        // When & Then - Add first language
        mockMvc.perform(post("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLanguage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vietnamese"));

        // Add second language
        mockMvc.perform(post("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(language2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("English"));

        verify(languageService, times(2)).saveLanguage(any(Language.class));
    }

    // ==================== PUBLISHER TESTS ====================

    @Test
    @DisplayName("Test 9: Get All Publishers - Lấy danh sách tất cả nhà xuất bản")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllPublishers() throws Exception {
        // Given
        Publisher publisher2 = new Publisher();
        publisher2.setId(2L);
        publisher2.setName("Second Publisher");
        publisher2.setAddress("456 Publisher Avenue");
        publisher2.setEmail("second@example.com");

        List<Publisher> publishers = Arrays.asList(testPublisher, publisher2);
        when(publisherService.getAllPublishers()).thenReturn(publishers);

        // When & Then
        mockMvc.perform(get("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Publisher"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second Publisher"));

        verify(publisherService, times(1)).getAllPublishers();
    }

    @Test
    @DisplayName("Test 10: Get Publisher by ID - Lấy nhà xuất bản theo ID")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetPublisherById() throws Exception {
        // Given
        when(publisherService.getPublisherById(anyLong())).thenReturn(testPublisher);

        // When & Then
        mockMvc.perform(get("/api/v1/publisher/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Publisher"))
                .andExpect(jsonPath("$.address").value("123 Publisher Street"))
                .andExpect(jsonPath("$.email").value("publisher@example.com"));

        verify(publisherService, times(1)).getPublisherById(anyLong());
    }

    @Test
    @DisplayName("Test 11: Add Publisher - Thêm nhà xuất bản mới thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testAddPublisherSuccess() throws Exception {
        // Given
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When & Then
        mockMvc.perform(post("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Publisher"))
                .andExpect(jsonPath("$.address").value("123 Publisher Street"))
                .andExpect(jsonPath("$.email").value("publisher@example.com"))
                .andExpect(jsonPath("$.phone").value("0123456789"));

        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 12: Update Publisher - Cập nhật nhà xuất bản thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testUpdatePublisherSuccess() throws Exception {
        // Given
        testPublisher.setName("Updated Publisher Name");
        testPublisher.setAddress("New Address 789");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When & Then
        mockMvc.perform(put("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Publisher Name"))
                .andExpect(jsonPath("$.address").value("New Address 789"));

        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 13: Delete Publisher - Xóa nhà xuất bản thành công")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testDeletePublisherSuccess() throws Exception {
        // Given
        doNothing().when(publisherService).deletePublisher(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/publisher/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(publisherService, times(1)).deletePublisher(anyLong());
    }

    @Test
    @DisplayName("Test 14: Get Publisher by ID - Nhà xuất bản không tồn tại")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetPublisherByIdNotFound() throws Exception {
        // Given
        when(publisherService.getPublisherById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/publisher/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(publisherService, times(1)).getPublisherById(999L);
    }

    @Test
    @DisplayName("Test 15: Get All Publishers - Danh sách rỗng")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllPublishersEmpty() throws Exception {
        // Given
        when(publisherService.getAllPublishers()).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(publisherService, times(1)).getAllPublishers();
    }

    @Test
    @DisplayName("Test 16: Update Publisher Contact Info - Cập nhật thông tin liên hệ")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testUpdatePublisherContactInfo() throws Exception {
        // Given
        testPublisher.setEmail("newemail@example.com");
        testPublisher.setPhone("0987654321");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When & Then
        mockMvc.perform(put("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.phone").value("0987654321"));

        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 17: Add Publisher - Thêm nhà xuất bản với thông tin đầy đủ")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testAddPublisherWithFullInfo() throws Exception {
        // Given
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);

        // When & Then
        mockMvc.perform(post("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phone").exists());

        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
    }

    @Test
    @DisplayName("Test 18: Language CRUD Operations - Kiểm tra chuỗi thao tác CRUD")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testLanguageCRUDOperations() throws Exception {
        // Create
        when(languageService.saveLanguage(any(Language.class))).thenReturn(testLanguage);
        mockMvc.perform(post("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLanguage)))
                .andExpect(status().isCreated());

        // Read
        when(languageService.getLanguageById(1L)).thenReturn(testLanguage);
        mockMvc.perform(get("/api/v1/language/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Update
        testLanguage.setName("Updated Language");
        when(languageService.updateLanguage(any(Language.class))).thenReturn(testLanguage);
        mockMvc.perform(put("/api/v1/language")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLanguage)))
                .andExpect(status().isOk());

        // Delete
        doNothing().when(languageService).deleteLanguage(1L);
        mockMvc.perform(delete("/api/v1/language/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify all operations
        verify(languageService, times(1)).saveLanguage(any(Language.class));
        verify(languageService, times(1)).getLanguageById(1L);
        verify(languageService, times(1)).updateLanguage(any(Language.class));
        verify(languageService, times(1)).deleteLanguage(1L);
    }

    @Test
    @DisplayName("Test 19: Publisher CRUD Operations - Kiểm tra chuỗi thao tác CRUD")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testPublisherCRUDOperations() throws Exception {
        // Create
        when(publisherService.savePublisher(any(Publisher.class))).thenReturn(testPublisher);
        mockMvc.perform(post("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isCreated());

        // Read
        when(publisherService.getPublisherById(1L)).thenReturn(testPublisher);
        mockMvc.perform(get("/api/v1/publisher/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Update
        testPublisher.setName("Updated Publisher");
        when(publisherService.updatePublisher(any(Publisher.class))).thenReturn(testPublisher);
        mockMvc.perform(put("/api/v1/publisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPublisher)))
                .andExpect(status().isOk());

        // Delete
        doNothing().when(publisherService).deletePublisher(1L);
        mockMvc.perform(delete("/api/v1/publisher/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify all operations
        verify(publisherService, times(1)).savePublisher(any(Publisher.class));
        verify(publisherService, times(1)).getPublisherById(1L);
        verify(publisherService, times(1)).updatePublisher(any(Publisher.class));
        verify(publisherService, times(1)).deletePublisher(1L);
    }

    @Test
    @DisplayName("Test 20: Delete Non-existent Publisher - Xóa nhà xuất bản không tồn tại")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testDeleteNonExistentPublisher() throws Exception {
        // Given
        doNothing().when(publisherService).deletePublisher(999L);

        // When & Then
        mockMvc.perform(delete("/api/v1/publisher/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(publisherService, times(1)).deletePublisher(999L);
    }
}
