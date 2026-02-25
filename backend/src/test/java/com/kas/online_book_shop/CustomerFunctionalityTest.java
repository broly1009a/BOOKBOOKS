package com.kas.online_book_shop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.kas.online_book_shop.dto.OrderDetailDTO;
import com.kas.online_book_shop.enums.OrderState;
import com.kas.online_book_shop.enums.PaymentState;
import com.kas.online_book_shop.enums.ShippingState;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.Feedback;
import com.kas.online_book_shop.model.Order;
import com.kas.online_book_shop.model.OrderDetail;
import com.kas.online_book_shop.model.User;
import com.kas.online_book_shop.service.CartService;
import com.kas.online_book_shop.service.FeedbackService;
import com.kas.online_book_shop.service.OrderService;

/**
 * Unit Test cho Customer - Vũ
 * Chức năng: Cart/Order/Feedback
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Customer Functionality Test - Vũ")
public class CustomerFunctionalityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private FeedbackService feedbackService;

    private Order testOrder;
    private Order testCart;
    private Feedback testFeedback;
    private User testUser;
    private Book testBook;
    private OrderDetail testOrderDetail;
    private OrderDetailDTO orderDetailDTO;

    @BeforeEach
    void setUp() {
        // Setup user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("customer@example.com");
        testUser.setFullName("Test Customer");

        // Setup book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(100.0);
        testBook.setQuantity(10);

        // Setup order detail
        testOrderDetail = new OrderDetail();
        testOrderDetail.setId(1L);
        testOrderDetail.setBook(testBook);
        testOrderDetail.setQuantity(2);
        testOrderDetail.setPrice(200.0);

        // Setup order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setOrderState(OrderState.PROCESSING);
        testOrder.setPaymentState(PaymentState.PENDING);
        testOrder.setShippingState(ShippingState.PREPARING);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setTotalPrice(200.0);
        testOrder.setOrderDetails(Arrays.asList(testOrderDetail));

        // Setup cart
        testCart = new Order();
        testCart.setId(2L);
        testCart.setUser(testUser);
        testCart.setOrderState(OrderState.CART);
        testCart.setOrderDetails(new ArrayList<>());

        // Setup feedback
        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setUser(testUser);
        testFeedback.setBook(testBook);
        testFeedback.setContent("Great book!");
        testFeedback.setCreatedDate(LocalDateTime.now());

        // Setup DTO
        orderDetailDTO = new OrderDetailDTO(1L, 1L, 2);
    }

    // ==================== CART TESTS ====================

    @Test
    @DisplayName("Test 1: Get Cart - Lấy giỏ hàng theo user")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetCartByUser() throws Exception {
        // Given
        when(cartService.getCartByUser(anyLong())).thenReturn(testCart);

        // When & Then
        mockMvc.perform(get("/api/v1/cart/by-user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.orderState").value("CART"));

        verify(cartService, times(1)).getCartByUser(anyLong());
    }

    @Test
    @DisplayName("Test 2: Get All Carts - Lấy tất cả giỏ hàng")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllCarts() throws Exception {
        // Given
        List<Order> carts = Arrays.asList(testCart);
        when(cartService.getAllCart()).thenReturn(carts);

        // When & Then
        mockMvc.perform(get("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));

        verify(cartService, times(1)).getAllCart();
    }

    @Test
    @DisplayName("Test 3: Add to Cart - Thêm sản phẩm vào giỏ hàng")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testAddToCart() throws Exception {
        // Given
        doNothing().when(cartService).addToCart(any(OrderDetailDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDetailDTO)))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).addToCart(any(OrderDetailDTO.class));
    }

    @Test
    @DisplayName("Test 4: Update Cart - Cập nhật giỏ hàng")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testUpdateCart() throws Exception {
        // Given
        doNothing().when(cartService).updateCart(any(Order.class));

        // When & Then
        mockMvc.perform(put("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCart)))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).updateCart(any(Order.class));
    }

    @Test
    @DisplayName("Test 5: Add to Cart - Thêm sản phẩm với số lượng không hợp lệ")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testAddToCartWithInvalidQuantity() throws Exception {
        // Given
        OrderDetailDTO invalidDTO = new OrderDetailDTO(1L, 1L, -1);  // Invalid quantity
        doNothing().when(cartService).addToCart(any(OrderDetailDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isNoContent());  // Note: Should add validation
    }

    // ==================== ORDER TESTS ====================

    @Test
    @DisplayName("Test 6: Get Order by User - Lấy đơn hàng theo user")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetOrderByUser() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrderByUser(anyLong())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/v1/order/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderState").value("PROCESSING"));

        verify(orderService, times(1)).getOrderByUser(anyLong());
    }

    @Test
    @DisplayName("Test 7: Process Order - Xử lý đơn hàng")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testProcessOrder() throws Exception {
        // Given
        doNothing().when(orderService).changeOrderState(anyLong(), any(OrderState.class));
        doNothing().when(orderService).processOrder(any(Order.class));

        // When & Then
        mockMvc.perform(post("/api/v1/order/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).changeOrderState(anyLong(), any(OrderState.class));
        verify(orderService, times(1)).processOrder(any(Order.class));
    }

    @Test
    @DisplayName("Test 8: Get Order by ID - Lấy đơn hàng theo ID")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetOrderById() throws Exception {
        // Given
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(get("/api/v1/order/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(200.0));

        verify(orderService, times(1)).getOrderById(anyLong());
    }

    @Test
    @DisplayName("Test 9: Get All Orders - Lấy tất cả đơn hàng")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testGetAllOrders() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAll()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/v1/order/get-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(orderService, times(1)).getAll();
    }

    // ==================== FEEDBACK TESTS ====================

    @Test
    @DisplayName("Test 10: Get All Feedbacks - Lấy tất cả feedback")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetAllFeedbacks() throws Exception {
        // Given
        List<Feedback> feedbacks = Arrays.asList(testFeedback);
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        // When & Then
        mockMvc.perform(get("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Great book!"));

        verify(feedbackService, times(1)).getAllFeedbacks();
    }

    @Test
    @DisplayName("Test 11: Get Feedback by ID - Lấy feedback theo ID")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetFeedbackById() throws Exception {
        // Given
        when(feedbackService.getFeedbackById(anyLong())).thenReturn(testFeedback);

        // When & Then
        mockMvc.perform(get("/api/v1/feedback/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Great book!"));

        verify(feedbackService, times(1)).getFeedbackById(anyLong());
    }

    @Test
    @DisplayName("Test 12: Save Feedback - Tạo feedback mới")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testSaveFeedback() throws Exception {
        // Given
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(testFeedback);

        // When & Then
        mockMvc.perform(post("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFeedback)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Great book!"));

        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Test 13: Update Feedback - Cập nhật feedback")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testUpdateFeedback() throws Exception {
        // Given
        testFeedback.setContent("Updated feedback!");
        when(feedbackService.updateFeedback(any(Feedback.class))).thenReturn(testFeedback);

        // When & Then
        mockMvc.perform(put("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFeedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated feedback!"));

        verify(feedbackService, times(1)).updateFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Test 14: Delete Feedback - Xóa feedback")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testDeleteFeedback() throws Exception {
        // Given
        doNothing().when(feedbackService).deleteFeedback(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/feedback/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(feedbackService, times(1)).deleteFeedback(anyLong());
    }

    @Test
    @DisplayName("Test 15: Get Feedbacks Paged and Sorted - Lấy feedback phân trang và sắp xếp")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetFeedbacksPagedAndSorted() throws Exception {
        // Given
        List<Feedback> feedbacks = Arrays.asList(testFeedback);
        Page<Feedback> feedbackPage = new PageImpl<>(feedbacks, PageRequest.of(0, 5), 1);
        when(feedbackService.getAllFeedbacks(any(Pageable.class))).thenReturn(feedbackPage);

        // When & Then
        mockMvc.perform(get("/api/v1/feedback/sorted-and-paged")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(feedbackService, times(1)).getAllFeedbacks(any(Pageable.class));
    }
}
