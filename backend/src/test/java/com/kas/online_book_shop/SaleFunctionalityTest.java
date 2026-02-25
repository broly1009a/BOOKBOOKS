package com.kas.online_book_shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.kas.online_book_shop.controller.BookController;
import com.kas.online_book_shop.controller.OrderController;
import com.kas.online_book_shop.controller.UserController;
import com.kas.online_book_shop.enums.AccountState;
import com.kas.online_book_shop.enums.OrderState;
import com.kas.online_book_shop.enums.PaymentState;
import com.kas.online_book_shop.enums.Role;
import com.kas.online_book_shop.enums.ShippingState;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.Order;
import com.kas.online_book_shop.model.OrderDetail;
import com.kas.online_book_shop.model.User;
import com.kas.online_book_shop.service.BookService;
import com.kas.online_book_shop.service.OrderService;
import com.kas.online_book_shop.service.UserService;

/**
 * Unit Test cho Sale/Staff - Trường
 * Chức năng: Quản lý đơn hàng và Xem thông tin khách hàng
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Sale/Staff Functionality Test - Trường")
public class SaleFunctionalityTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderController orderController;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private BookController bookController;

    private Order testOrder;
    private User testCustomer;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Setup Customer
        testCustomer = new User();
        testCustomer.setId(1L);
        testCustomer.setEmail("customer@example.com");
        testCustomer.setFullName("Test Customer");
        testCustomer.setRole(Role.USER);
        testCustomer.setState(AccountState.ACTIVE);
        testCustomer.setPhone("0123456789");
        testCustomer.setAddress("123 Customer Street");

        // Setup Book for Order Detail
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(150000L);

        // Setup Order Detail
        OrderDetail testOrderDetail = new OrderDetail();
        testOrderDetail.setId(1L);
        testOrderDetail.setBook(testBook);
        testOrderDetail.setAmount(2);
        testOrderDetail.setOriginalPrice(150000L);
        testOrderDetail.setSalePrice(150000L);

        // Setup Order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testCustomer);
        testOrder.setState(OrderState.PROCESSING);
        testOrder.setPaymentState(PaymentState.PENDING);
        testOrder.setShippingState(ShippingState.NOTSHIPPING);
        testOrder.setCreated(LocalDateTime.now());        testOrder.setShippingPrice(20000L);        testOrder.setOrderDetails(List.of(testOrderDetail));
    }

    // ==================== ORDER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Test 1: Get All Orders - Xem tất cả đơn hàng")
    void testGetAllOrders() {
        // Given
        List<Order> orders = Collections.singletonList(testOrder);
        when(orderService.getAll()).thenReturn(orders);

        // When
        ResponseEntity<List<Order>> response = orderController.getAll();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(OrderState.PROCESSING, response.getBody().get(0).getState());
        assertNotNull(response.getBody().get(0).getTotalPrice());
        verify(orderService, times(1)).getAll();
    }

    @Test
    @DisplayName("Test 2: Get Order by ID - Xem chi tiết đơn hàng")
    void testGetOrderById() {
        // Given
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When
        ResponseEntity<Order> response = orderController.getOrderById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Customer", response.getBody().getUser().getFullName());
        assertNotNull(response.getBody().getTotalPrice());
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("Test 3: Update Shipping State - Cập nhật trạng thái vận chuyển")
    void testUpdateShippingState() {
        // Given
        doNothing().when(orderService).changeOrderShippingState(anyLong(), any(ShippingState.class));

        // When
        ResponseEntity<Void> response = orderController.updateShipping(1L, ShippingState.SHIPPING);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).changeOrderShippingState(1L, ShippingState.SHIPPING);
    }

    @Test
    @DisplayName("Test 4: Update Shipping State - Từ NOTSHIPPING sang SHIPPING")
    void testUpdateShippingFromPreparingToShipping() {
        // Given
        doNothing().when(orderService).changeOrderShippingState(anyLong(), any(ShippingState.class));

        // When
        ResponseEntity<Void> response = orderController.updateShipping(1L, ShippingState.SHIPPING);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).changeOrderShippingState(1L, ShippingState.SHIPPING);
    }

    @Test
    @DisplayName("Test 5: Update Order State - Cập nhật trạng thái đơn hàng sang COMPLETED")
    void testUpdateOrderState() {
        // Given
        doNothing().when(orderService).changeOrderState(anyLong(), any(OrderState.class));

        // When
        ResponseEntity<Void> response = orderController.updateOrderState(1L, OrderState.COMPLETED);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).changeOrderState(1L, OrderState.COMPLETED);
    }

    @Test
    @DisplayName("Test 6: Cancel Order - Hủy đơn hàng thành công")
    void testCancelOrder() {
        // Given
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        doNothing().when(orderService).cancel(anyLong());

        // When
        ResponseEntity<String> response = orderController.cancelOrder(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).getOrderById(1L);
        verify(orderService, times(1)).cancel(1L);
    }

    @Test
    @DisplayName("Test 7: Cancel Order - Không thể hủy đơn đang vận chuyển")
    void testCancelOrderWhileShipping() {
        // Given
        testOrder.setShippingState(ShippingState.SHIPPING);
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When
        ResponseEntity<String> response = orderController.cancelOrder(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Không thể hủy đơn hàng đang trong quá trình vận chuyển", response.getBody());
        verify(orderService, times(1)).getOrderById(1L);
        verify(orderService, times(0)).cancel(anyLong());
    }

    @Test
    @DisplayName("Test 8: Get Order by User - Xem đơn hàng theo khách hàng")
    void testGetOrderByUser() {
        // Given
        List<Order> orders = Collections.singletonList(testOrder);
        when(orderService.getOrderByUser(anyLong())).thenReturn(orders);

        // When
        ResponseEntity<List<Order>> response = orderController.getOrderByUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Test Customer", response.getBody().get(0).getUser().getFullName());
        verify(orderService, times(1)).getOrderByUser(1L);
    }

    @Test
    @DisplayName("Test 9: Query Orders - Tìm kiếm đơn hàng với bộ lọc")
    void testQueryOrders() {
        // Given
        List<Order> orders = Collections.singletonList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 5), 1);
        when(orderService.queryOrder(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(orderPage);

        // When
        ResponseEntity<Page<Order>> response = orderController.queryOrder(
                null, null, null, null, null, "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        verify(orderService, times(1)).queryOrder(any(), any(), any(), any(), any(), any(Pageable.class));
    }

    // ==================== CUSTOMER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Test 10: Get All Customers - Xem danh sách khách hàng")
    void testGetAllCustomers() {
        // Given
        List<User> customers = Collections.singletonList(testCustomer);
        Page<User> customerPage = new PageImpl<>(customers, PageRequest.of(0, 5), 1);
        when(userService.getCustomerByFullNameContainingAndState(anyString(), any(), any(Pageable.class)))
                .thenReturn(customerPage);

        // When
        ResponseEntity<Page<User>> response = userController.getCustomer(
                "", null, "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        assertEquals("Test Customer", response.getBody().getContent().get(0).getFullName());
        verify(userService, times(1)).getCustomerByFullNameContainingAndState(anyString(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Test 11: Search Customer by Name - Tìm kiếm khách hàng theo tên")
    void testSearchCustomerByName() {
        // Given
        List<User> customers = Collections.singletonList(testCustomer);
        Page<User> customerPage = new PageImpl<>(customers, PageRequest.of(0, 5), 1);
        when(userService.getCustomerByFullNameContainingAndState(anyString(), any(), any(Pageable.class)))
                .thenReturn(customerPage);

        // When
        ResponseEntity<Page<User>> response = userController.getCustomer(
                "Test", null, "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Customer", response.getBody().getContent().get(0).getFullName());
        verify(userService, times(1)).getCustomerByFullNameContainingAndState(eq("Test"), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Test 12: Get User by ID - Xem chi tiết khách hàng")
    void testGetUserById() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(testCustomer);

        // When
        ResponseEntity<User> response = userController.getUserById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Customer", response.getBody().getFullName());
        assertEquals("customer@example.com", response.getBody().getEmail());
        assertEquals("0123456789", response.getBody().getPhone());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Test 13: Filter Customer by State - Lọc khách hàng theo trạng thái")
    void testFilterCustomerByState() {
        // Given
        List<User> customers = Collections.singletonList(testCustomer);
        Page<User> customerPage = new PageImpl<>(customers, PageRequest.of(0, 5), 1);
        when(userService.getCustomerByFullNameContainingAndState(anyString(), any(AccountState.class), any(Pageable.class)))
                .thenReturn(customerPage);

        // When
        ResponseEntity<Page<User>> response = userController.getCustomer(
                "", AccountState.ACTIVE, "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AccountState.ACTIVE, response.getBody().getContent().get(0).getState());
        verify(userService, times(1)).getCustomerByFullNameContainingAndState(eq(""), eq(AccountState.ACTIVE), any(Pageable.class));
    }

    @Test
    @DisplayName("Test 14: Get User by Email - Tìm khách hàng theo email")
    void testGetUserByEmail() {
        // Given
        when(userService.getUserByEmail(anyString())).thenReturn(testCustomer);

        // When
        ResponseEntity<User> response = userController.getUserByEmail("customer@example.com");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("customer@example.com", response.getBody().getEmail());
        assertEquals("Test Customer", response.getBody().getFullName());
        verify(userService, times(1)).getUserByEmail("customer@example.com");
    }

    // ==================== BOOK VIEWING TESTS ====================

    @Test
    @DisplayName("Test 15: Get All Books - Xem danh sách sách")
    void testGetAllBooks() {
        // Given
        List<Book> books = Collections.singletonList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 5), 1);
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        // When
        ResponseEntity<Page<Book>> response = bookController.getAllBooksSortedAndPaged(
                "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        assertEquals("Test Book", response.getBody().getContent().get(0).getTitle());
        assertEquals(150000L, response.getBody().getContent().get(0).getPrice());
        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    @DisplayName("Test 16: Get Book by ID - Xem chi tiết sách")
    void testGetBookById() {
        // Given
        when(bookService.getBookById(anyLong())).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.getBookById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Book", response.getBody().getTitle());
        assertEquals(150000L, response.getBody().getPrice());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    @DisplayName("Test 17: Search Book by Name - Tìm kiếm sách theo tên")
    void testSearchBookByName() {
        // Given
        List<Book> books = Collections.singletonList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 5), 1);
        when(bookService.getBooksByName(anyString(), any(Pageable.class))).thenReturn(bookPage);

        // When
        ResponseEntity<Page<Book>> response = bookController.searchBookByName(
                "Test", "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Book", response.getBody().getContent().get(0).getTitle());
        verify(bookService, times(1)).getBooksByName(eq("Test"), any(Pageable.class));
    }
}
