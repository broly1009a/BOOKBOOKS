package com.kas.online_book_shop;

import com.kas.online_book_shop.controller.BookController;
import com.kas.online_book_shop.enums.BookState;
import com.kas.online_book_shop.model.*;
import com.kas.online_book_shop.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho Manager - Công
 * Chức năng: Add book/Delete book/Change Book
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Manager Functionality Test - Công")
public class ManagerFunctionalityTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;

    @BeforeEach
    void setUp() {
        // Setup Publisher
        Publisher testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");

        // Setup Author
        Author testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setName("Test Author");
        testAuthor.setCompany("Test Company");

        // Setup Language
        Language testLanguage = new Language();
        testLanguage.setId(1L);
        testLanguage.setName("Vietnamese");

        // Setup Category
        BookCategory testCategory = new BookCategory();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        // Setup Collection
        BookCollection testCollection = new BookCollection();
        testCollection.setId(1L);
        testCollection.setName("Bestsellers");

        // Setup Book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book Title");
        testBook.setISBN("978-3-16-148410-0");
        testBook.setPrice(150000L);
        testBook.setStock(50);
        testBook.setDescription("Test book description");
        testBook.setPage(300);
        testBook.setPublicationDate(LocalDate.of(2024, 1, 1));
        testBook.setState(BookState.ACTIVE);
        testBook.setPublisher(testPublisher);
        testBook.setAuthors(List.of(testAuthor));
        testBook.setLanguage(testLanguage);
        testBook.setCategory(testCategory);
        testBook.setCollections(List.of(testCollection));
    }

    // ==================== ADD BOOK TESTS ====================

    @Test
    @DisplayName("Test 1: Add Book - Thêm sách mới thành công")
    void testAddBookSuccess()  {
        // Given
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.saveBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Book Title", response.getBody().getTitle());
        assertEquals("978-3-16-148410-0", response.getBody().getISBN());
        assertEquals(150000L, response.getBody().getPrice());
        assertEquals(50, response.getBody().getStock());
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 2: Add Book - Thêm sách với thông tin đầy đủ")
    void testAddBookWithFullInformation()  {
        // Given
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.saveBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTitle());
        assertNotNull(response.getBody().getISBN());
        assertNotNull(response.getBody().getPrice());
        assertNotNull(response.getBody().getStock());
        assertNotNull(response.getBody().getDescription());
        assertNotNull(response.getBody().getPage());
        assertNotNull(response.getBody().getPublicationDate());
        assertNotNull(response.getBody().getState());
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 3: Add Book - Thêm sách với giá âm (validation)")
    void testAddBookWithNegativePrice()  {
        // Given
        Book invalidBook = new Book();
        invalidBook.setTitle("Invalid Book");
        invalidBook.setPrice(-100L);  // Negative price
        invalidBook.setStock(10);

        when(bookService.saveBook(any(Book.class))).thenReturn(invalidBook);

        // When
        ResponseEntity<Book> response = bookController.saveBook(invalidBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());  // Note: Should add validation
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 4: Add Book - Thêm sách với số lượng âm (validation)")
    void testAddBookWithNegativeQuantity()  {
        // Given
        Book invalidBook = new Book();
        invalidBook.setTitle("Invalid Quantity Book");
        invalidBook.setPrice(100L);
        invalidBook.setStock(-5);  // Negative stock

        when(bookService.saveBook(any(Book.class))).thenReturn(invalidBook);

        // When
        ResponseEntity<Book> response = bookController.saveBook(invalidBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());  // Note: Should add validation
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 5: Add Multiple Books - Thêm nhiều sách")
    void testAddMultipleBooks()  {
        // Given
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Second Test Book");
        book2.setPrice(200000L);
        book2.setStock(30);

        when(bookService.saveBook(any(Book.class)))
                .thenReturn(testBook)
                .thenReturn(book2);

        // When - Add first book
        ResponseEntity<Book> response1 = bookController.saveBook(testBook);
        // Add second book
        ResponseEntity<Book> response2 = bookController.saveBook(book2);

        // Then
        assertNotNull(response1);
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(1L, Objects.requireNonNull(response1.getBody()).getId());
        
        assertNotNull(response2);
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());
        assertEquals(2L, Objects.requireNonNull(response2.getBody()).getId());
        
        verify(bookService, times(2)).saveBook(any(Book.class));
    }

    // ==================== UPDATE/CHANGE BOOK TESTS ====================

    @Test
    @DisplayName("Test 6: Update Book - Cập nhật thông tin sách thành công")
    void testUpdateBookSuccess()  {
        // Given
        testBook.setTitle("Updated Book Title");
        testBook.setPrice(180000L);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Book Title", response.getBody().getTitle());
        assertEquals(180000L, response.getBody().getPrice());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 7: Update Book - Thay đổi giá sách")
    void testUpdateBookPrice()  {
        // Given
        testBook.setPrice(200000L);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200000L, response.getBody().getPrice());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 8: Update Book - Thay đổi số lượng sách")
    void testUpdateBookQuantity()  {
        // Given
        testBook.setStock(100);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().getStock());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 9: Update Book - Thay đổi trạng thái sách")
    void testUpdateBookState()  {
        // Given
        testBook.setState(BookState.HIDDEN);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BookState.HIDDEN, response.getBody().getState());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("Test 10: Update Book - Thay đổi mô tả sách")
    void testUpdateBookDescription()  {
        // Given
        testBook.setDescription("Updated description with more details");
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated description with more details", response.getBody().getDescription());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }

    // ==================== DELETE BOOK TESTS ====================

    @Test
    @DisplayName("Test 11: Delete Book - Xóa sách thành công")
    void testDeleteBookSuccess()  {
        // Given
        doNothing().when(bookService).deleteBook(anyLong());

        // When
        ResponseEntity<Void> response = bookController.deleteBook(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    @DisplayName("Test 12: Delete Book - Xóa sách không tồn tại")
    void testDeleteNonExistentBook()  {
        // Given
        doNothing().when(bookService).deleteBook(999L);

        // When
        ResponseEntity<Void> response = bookController.deleteBook(999L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(999L);
    }

    // ==================== GET BOOK TESTS ====================

    @Test
    @DisplayName("Test 13: Get Book by ID - Lấy thông tin sách theo ID")
    void testGetBookById()  {
        // Given
        when(bookService.getBookById(anyLong())).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.getBookById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Book Title", response.getBody().getTitle());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    @DisplayName("Test 14: Get All Books - Lấy danh sách tất cả sách với phân trang")
    void testGetAllBooksSortedAndPaged()  {
        // Given
        List<Book> books = Collections.singletonList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 5), 1);
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        // When
        ResponseEntity<Page<Book>> response = bookController.getAllBooksSortedAndPaged("id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        assertEquals("Test Book Title", response.getBody().getContent().get(0).getTitle());
        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    @DisplayName("Test 15: Verify Book Inventory - Kiểm tra tồn kho sau khi thay đổi")
    void testVerifyBookInventory()  {
        // Given
        testBook.setStock(0);
        testBook.setState(BookState.HIDDEN);
        when(bookService.updateBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(testBook);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getStock());
        assertEquals(BookState.HIDDEN, response.getBody().getState());
        verify(bookService, times(1)).updateBook(any(Book.class));
    }
}
