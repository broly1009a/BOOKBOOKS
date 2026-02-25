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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kas.online_book_shop.enums.BookState;
import com.kas.online_book_shop.model.Author;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.BookCategory;
import com.kas.online_book_shop.model.BookCollection;
import com.kas.online_book_shop.model.Language;
import com.kas.online_book_shop.model.Publisher;
import com.kas.online_book_shop.service.BookService;

/**
 * Unit Test cho Manager - Công
 * Chức năng: Add book/Delete book/Change Book
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Manager Functionality Test - Công")
public class ManagerFunctionalityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book testBook;
    private Publisher testPublisher;
    private Author testAuthor;
    private Language testLanguage;
    private BookCategory testCategory;
    private BookCollection testCollection;

    @BeforeEach
    void setUp() {
        // Setup Publisher
        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");

        // Setup Author
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setName("Test Author");
        testAuthor.setBio("Author Bio");

        // Setup Language
        testLanguage = new Language();
        testLanguage.setId(1L);
        testLanguage.setName("Vietnamese");

        // Setup Category
        testCategory = new BookCategory();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        // Setup Collection
        testCollection = new BookCollection();
        testCollection.setId(1L);
        testCollection.setName("Bestsellers");

        // Setup Book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book Title");
        testBook.setIsbn("978-3-16-148410-0");
        testBook.setPrice(150000.0);
        testBook.setQuantity(50);
        testBook.setDescription("Test book description");
        testBook.setPageCount(300);
        testBook.setPublicationDate(LocalDate.of(2024, 1, 1));
        testBook.setState(BookState.AVAILABLE);
        testBook.setPublisher(testPublisher);
        testBook.setAuthor(testAuthor);
        testBook.setLanguage(testLanguage);
        testBook.setCategories(Arrays.asList(testCategory));
        testBook.setCollection(testCollection);
    }

    // ==================== ADD BOOK TESTS ====================

    @Test
    @DisplayName("Test 1: Add Book - Thêm sách mới thành công")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testAddBookSuccess() throws Exception {
        // Given
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book Title"))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"))
                .andExpect(jsonPath("$.price").value(150000.0))
                .andExpect(jsonPath("$.quantity").value(50));

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 2: Add Book - Thêm sách với thông tin đầy đủ")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testAddBookWithFullInformation() throws Exception {
        // Given
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.isbn").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.quantity").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.pageCount").exists())
                .andExpect(jsonPath("$.publicationDate").exists())
                .andExpect(jsonPath("$.state").exists());

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 3: Add Book - Thêm sách với giá âm (validation)")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testAddBookWithNegativePrice() throws Exception {
        // Given
        Book invalidBook = new Book();
        invalidBook.setTitle("Invalid Book");
        invalidBook.setPrice(-100.0);  // Negative price
        invalidBook.setQuantity(10);

        when(bookService.saveBook(any(Book.class))).thenReturn(invalidBook);

        // When & Then
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isCreated());  // Note: Should add validation

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 4: Add Book - Thêm sách với số lượng âm (validation)")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testAddBookWithNegativeQuantity() throws Exception {
        // Given
        Book invalidBook = new Book();
        invalidBook.setTitle("Invalid Quantity Book");
        invalidBook.setPrice(100.0);
        invalidBook.setQuantity(-5);  // Negative quantity

        when(bookService.saveBook(any(Book.class))).thenReturn(invalidBook);

        // When & Then
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isCreated());  // Note: Should add validation

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 5: Add Multiple Books - Thêm nhiều sách")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testAddMultipleBooks() throws Exception {
        // Given
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Second Test Book");
        book2.setPrice(200000.0);
        book2.setQuantity(30);

        when(bookService.saveBook(any(Book.class)))
                .thenReturn(testBook)
                .thenReturn(book2);

        // When & Then - Add first book
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        // Add second book
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(bookService, times(2)).saveBook(any(Book.class));
    }

    // ==================== UPDATE/CHANGE BOOK TESTS ====================

    @Test
    @DisplayName("Test 6: Update Book - Cập nhật thông tin sách thành công")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testUpdateBookSuccess() throws Exception {
        // Given
        testBook.setTitle("Updated Book Title");
        testBook.setPrice(180000.0);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book Title"))
                .andExpect(jsonPath("$.price").value(180000.0));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 7: Update Book - Thay đổi giá sách")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testUpdateBookPrice() throws Exception {
        // Given
        testBook.setPrice(200000.0);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(200000.0));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 8: Update Book - Thay đổi số lượng sách")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testUpdateBookQuantity() throws Exception {
        // Given
        testBook.setQuantity(100);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 9: Update Book - Thay đổi trạng thái sách")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testUpdateBookState() throws Exception {
        // Given
        testBook.setState(BookState.OUT_OF_STOCK);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("OUT_OF_STOCK"));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 10: Update Book - Thay đổi mô tả sách")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testUpdateBookDescription() throws Exception {
        // Given
        testBook.setDescription("Updated description with more details");
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description with more details"));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    // ==================== DELETE BOOK TESTS ====================

    @Test
    @DisplayName("Test 11: Delete Book - Xóa sách thành công")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testDeleteBookSuccess() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(anyLong());
    }

    @Test
    @DisplayName("Test 12: Delete Book - Xóa sách không tồn tại")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testDeleteNonExistentBook() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(999L);

        // When & Then
        mockMvc.perform(delete("/api/v1/book/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(999L);
    }

    // ==================== GET BOOK TESTS ====================

    @Test
    @DisplayName("Test 13: Get Book by ID - Lấy thông tin sách theo ID")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testGetBookById() throws Exception {
        // Given
        when(bookService.getBookById(anyLong())).thenReturn(testBook);

        // When & Then
        mockMvc.perform(get("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book Title"));

        verify(bookService, times(1)).getBookById(anyLong());
    }

    @Test
    @DisplayName("Test 14: Get All Books - Lấy danh sách tất cả sách với phân trang")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testGetAllBooksSortedAndPaged() throws Exception {
        // Given
        List<Book> books = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 5), 1);
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/api/v1/book/sorted-and-paged")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Book Title"));

        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    @DisplayName("Test 15: Verify Book Inventory - Kiểm tra tồn kho sau khi thay đổi")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void testVerifyBookInventory() throws Exception {
        // Given
        testBook.setQuantity(0);
        testBook.setState(BookState.OUT_OF_STOCK);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(0))
                .andExpect(jsonPath("$.state").value("OUT_OF_STOCK"));

        verify(bookService, times(1)).updateBook(any(Book.class));
    }
}
