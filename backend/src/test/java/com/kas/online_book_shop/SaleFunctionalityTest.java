package com.kas.online_book_shop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Sale/Staff Functionality Test - Trường")
public class SaleFunctionalityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private Order testOrder;
    private User testCustomer;
    private OrderDetail testOrderDetail;

    @BeforeEach
    void setUp() {
        // Setup Customer
        testCustomer = new User();
        testCustomer.setId(1L);
        testCustomer.setEmail("customer@example.com");
        testCustomer.setFullName("Test Customer");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setAccountState(AccountState.ACTIVE);
        testCustomer.setPhone("0123456789");
        testCustomer.setAddress("123 Customer Street");

        // Setup Book for Order Detail
        Book testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(150000.0);

        // Setup Order Detail
        testOrderDetail = new OrderDetail();
        testOrderDetail.setId(1L);
        testOrderDetail.setBook(testBook);
        testOrderDetail.setQuantity(2);
        testOrderDetail.setPrice(300000.0);

        // Setup Order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testCustomer);
        testOrder.setOrderState(OrderState.PROCESSING);
        testOrder.setPaymentState(PaymentState.PENDING);
        testOrder.setShippingState(ShippingState.PREPARING);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setTotalPrice(300000.0);
        testOrder.setOrderDetails(Arrays.asList(testOrderDetail));
    }

    // ==================== ORDER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Test 1: Get All Orders - Xem tất cả đơn hàng")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetAllOrders() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAll()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/v1/order/get-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderState").value("PROCESSING"))
                .andExpect(jsonPath("$[0].totalPrice").value(300000.0));

        verify(orderService, times(1)).getAll();
    }

    @Test
    @DisplayName("Test 2: Get Order by ID - Xem chi tiết đơn hàng")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetOrderById() throws Exception {
        // Given
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(get("/api/v1/order/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.fullName").value("Test Customer"))
                .andExpect(jsonPath("$.totalPrice").value(300000.0));

        verify(orderService, times(1)).getOrderById(anyLong());
    }

    @Test
    @DisplayName("Test 3: Update Shipping State - Cập nhật trạng thái vận chuyển")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testUpdateShippingState() throws Exception {
        // Given
        doNothing().when(orderService).changeOrderShippingState(anyLong(), any(ShippingState.class));

        // When & Then
        mockMvc.perform(put("/api/v1/order/update-shipping/1/SHIPPING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).changeOrderShippingState(1L, ShippingState.SHIPPING);
    }

    @Test
    @DisplayName("Test 4: Update Shipping State - Từ PREPARING sang SHIPPING")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testUpdateShippingFromPreparingToShipping() throws Exception {
        // Given
        doNothing().when(orderService).changeOrderShippingState(anyLong(), any(ShippingState.class));

        // When & Then
        mockMvc.perform(put("/api/v1/order/update-shipping/1/SHIPPING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, timeOrder State - Cập nhật trạng thái đơn hàng sang COMPLETED")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testUpdateOrderState() throws Exception {
        // Given
        doNothing().when(orderService).changeOrderState(anyLong(), any(OrderState.class));

        // When & Then
        mockMvc.perform(put("/api/v1/order/update-orderState/1/COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).changeOrderState(1L, OrderState.COMPLETED);
    }

    @Test
    @DisplayName("Test 5ct(status().isNoContent());

        verify(orderService, times(1)).getOrderById(anyLong());
        verify(orderService, times(1)).cancel(anyLong());
    }

    @Test
    @DisplayName("Test 8: Cancel Order - Không thể hủy đơn đang vận chuyển")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testCancelOrderWhileShipping() throws Exception {
        // Given
        testOrder.setShippingState(ShippingState.SHIPPING);
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(put("/api/v1/order/cancel/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Không thể hủy đơn hàng đang trong quá trình vận chuyển"));

        verify(orderService, times(1)).getOrderById(anyLong());
        verify(orderService, times(0)).cancel(anyLong());
    }

    @Test
    @DisplayName("Test 9: Get Order by User - Xem đơn hàng theo khách hàng")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetOrderByUser() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrderByUser(anyLong())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/v1/order/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExp6: Cancel Order - Không thể hủy đơn đang vận chuyển")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testCancelOrderWhileShipping() throws Exception {
        // Given
        testOrder.setShippingState(ShippingState.SHIPPING);
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(put("/api/v1/order/cancel/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Không thể hủy đơn hàng đang trong quá trình vận chuyển"));

        verify(orderService, times(1)).getOrderById(anyLong());
        verify(orderService, times(0)).cancel(anyLong());
    }

    @Test
    @DisplayName("Test 7.param("size", "5")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(orderService, times(1)).queryOrder(any(), any(), any(), any(), any(), any(Pageable.class));
    }

    // ==================== CUSTOMER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Test 11: Get All Customers - Xem danh sách khách hàng")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetAllCustomers() throws Exception {
        // Given
    // ==================== CUSTOMER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Test 8erPage = new PageImpl<>(customers, PageRequest.of(0, 5), 1);
        when(userService.getCustomerByFullNameContainingAndState(anyString(), any(), any(Pageable.class)))
                .thenReturn(customerPage);

        // When & Then
        mockMvc.perform(get("/api/v1/user/customer")
                        .param("fullName", "Test")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Test Customer"));

        verify(userService, times(1)).getCustomerByFullNameContainingAndState(anyString(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Test 13: Filter Customer by State - Lọc khách hàng theo trạng thái")
    @WithMockUser(usern9e = "staff@example.com", roles = {"STAFF"})
    void testGetUserById() throws Exception {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(testCustomer);

        // When & Then
        mockMvc.perform(get("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.phone").value("0123456789"));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @DisplayName("Test 15: Get User by Email - Tìm khách hàng theo email")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetUserByEmail() throws Exception {
        // Given
        when(userService.getUserByEmail(anyString())).thenReturn(testCustomer);

        // When & Then
        mockMvc.perform(get("/api/v1/user/by-email/customer@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test Customer"));

        verify(userService, times(1)).getUserByEmail(anyString());
    }

    // ==================== BOOK VIEWING TESTS ====================

    @Test
    @DisplayName("Test 16: Get All Books - Xem danh sách sách")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void testGetAllBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 5), 1);
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);
0
        // When & Then
        mockMvc.perform(get("/api/v1/book/sorted-and-paged")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Book"))
                .andExpect(jsonPath("$.content[0].price").value(150000.0));

        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    @DisplayName("Test 17: Get Book by ID - Xem chi tiết sách")
