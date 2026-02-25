package com.kas.online_book_shop;

import com.kas.online_book_shop.controller.CartController;
import com.kas.online_book_shop.controller.FeedbackController;
import com.kas.online_book_shop.controller.OrderController;
import com.kas.online_book_shop.dto.OrderDetailDTO;
import com.kas.online_book_shop.enums.OrderState;
import com.kas.online_book_shop.enums.PaymentState;
import com.kas.online_book_shop.enums.ShippingState;
import com.kas.online_book_shop.model.*;
import com.kas.online_book_shop.service.CartService;
import com.kas.online_book_shop.service.FeedbackService;
import com.kas.online_book_shop.service.OrderService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho Customer - Vũ
 * Chức năng: Cart/Order/Feedback
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Functionality Test - Vũ")
public class CustomerFunctionalityTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private CartController cartController;

    @InjectMocks
    private OrderController orderController;

    @InjectMocks
    private FeedbackController feedbackController;

    private Order testOrder;
    private Order testCart;
    private Feedback testFeedback;
    private OrderDetailDTO orderDetailDTO;

    @BeforeEach
    void setUp() {
        // Setup user
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("customer@example.com");
        testUser.setFullName("Test Customer");

        // Setup book
        Book testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(100L);
        testBook.setStock(10);

        // Setup order detail
        OrderDetail testOrderDetail = new OrderDetail();
        testOrderDetail.setId(1L);
        testOrderDetail.setBook(testBook);
        testOrderDetail.setAmount(2);
        testOrderDetail.setOriginalPrice(100L);
        testOrderDetail.setSalePrice(100L);

        // Setup order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setState(OrderState.PROCESSING);
        testOrder.setPaymentState(PaymentState.PENDING);
        testOrder.setShippingState(ShippingState.NOTSHIPPING);
        testOrder.setCreated(LocalDateTime.now());
        testOrder.setShippingPrice(20000L);
        testOrder.setOrderDetails(List.of(testOrderDetail));

        // Setup cart
        testCart = new Order();
        testCart.setId(2L);
        testCart.setUser(testUser);
        testCart.setState(OrderState.CART);
        testCart.setOrderDetails(new ArrayList<>());

        // Setup feedback
        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setUser(testUser);
        testFeedback.setBook(testBook);
        testFeedback.setComment("Great book!");
        testFeedback.setCreatedAt(LocalDateTime.now());

        // Setup DTO
        orderDetailDTO = new OrderDetailDTO(1L, 1L, 2);
    }

    // ==================== CART TESTS ====================

    @Test
    @DisplayName("Test 1: Get Cart - Lấy giỏ hàng theo user")
    void testGetCartByUser()  {
        // Given
        when(cartService.getCartByUser(anyLong())).thenReturn(testCart);

        // When
        ResponseEntity<Order> response = cartController.getCartByUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals(OrderState.CART, response.getBody().getState());
        verify(cartService, times(1)).getCartByUser(1L);
    }

    @Test
    @DisplayName("Test 2: Get All Carts - Lấy tất cả giỏ hàng")
    void testGetAllCarts()  {
        // Given
        List<Order> carts = Collections.singletonList(testCart);
        when(cartService.getAllCart()).thenReturn(carts);

        // When
        ResponseEntity<List<Order>> response = cartController.getAllCart();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).getId());
        verify(cartService, times(1)).getAllCart();
    }

    @Test
    @DisplayName("Test 3: Add to Cart - Thêm sản phẩm vào giỏ hàng")
    void testAddToCart()  {
        // Given
        doNothing().when(cartService).addToCart(any(OrderDetailDTO.class));

        // When
        ResponseEntity<Void> response = cartController.addCart(orderDetailDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).addToCart(any(OrderDetailDTO.class));
    }

    @Test
    @DisplayName("Test 4: Update Cart - Cập nhật giỏ hàng")
    void testUpdateCart()  {
        // Given
        doNothing().when(cartService).updateCart(any(Order.class));

        // When
        ResponseEntity<Void> response = cartController.updateUserCart(testCart);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).updateCart(any(Order.class));
    }

    @Test
    @DisplayName("Test 5: Add to Cart - Thêm sản phẩm với số lượng không hợp lệ")
    void testAddToCartWithInvalidQuantity()  {
        // Given
        OrderDetailDTO invalidDTO = new OrderDetailDTO(1L, 1L, -1);  // Invalid quantity
        doNothing().when(cartService).addToCart(any(OrderDetailDTO.class));

        // When
        ResponseEntity<Void> response = cartController.addCart(invalidDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());  // Note: Should add validation
        verify(cartService, times(1)).addToCart(any(OrderDetailDTO.class));
    }

    // ==================== ORDER TESTS ====================

    @Test
    @DisplayName("Test 6: Get Order by User - Lấy đơn hàng theo user")
    void testGetOrderByUser()  {
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
        assertEquals(OrderState.PROCESSING, response.getBody().get(0).getState());
        verify(orderService, times(1)).getOrderByUser(1L);
    }

    @Test
    @DisplayName("Test 7: Process Order - Xử lý đơn hàng")
    void testProcessOrder()  {
        // Given
        doNothing().when(orderService).changeOrderState(anyLong(), any(OrderState.class));
        doNothing().when(orderService).processOrder(any(Order.class));

        // When
        ResponseEntity<Void> response = orderController.processOrder(testOrder);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).changeOrderState(anyLong(), any(OrderState.class));
        verify(orderService, times(1)).processOrder(any(Order.class));
    }

    @Test
    @DisplayName("Test 8: Get Order by ID - Lấy đơn hàng theo ID")
    void testGetOrderById()  {
        // Given
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);

        // When
        ResponseEntity<Order> response = orderController.getOrderById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertNotNull(response.getBody().getTotalPrice());
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("Test 9: Get All Orders - Lấy tất cả đơn hàng")
    void testGetAllOrders()  {
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
        verify(orderService, times(1)).getAll();
    }

    // ==================== FEEDBACK TESTS ====================

    @Test
    @DisplayName("Test 10: Get All Feedbacks - Lấy tất cả feedback")
    void testGetAllFeedbacks()  {
        // Given
        List<Feedback> feedbacks = Collections.singletonList(testFeedback);
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        // When
        ResponseEntity<List<Feedback>> response = feedbackController.getAllFeedbacks();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Great book!", response.getBody().get(0).getComment());
        verify(feedbackService, times(1)).getAllFeedbacks();
    }

    @Test
    @DisplayName("Test 11: Get Feedback by ID - Lấy feedback theo ID")
    void testGetFeedbackById()  {
        // Given
        when(feedbackService.getFeedbackById(anyLong())).thenReturn(testFeedback);

        // When
        ResponseEntity<Feedback> response = feedbackController.getFeedbackById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Great book!", response.getBody().getComment());
        verify(feedbackService, times(1)).getFeedbackById(1L);
    }

    @Test
    @DisplayName("Test 12: Save Feedback - Tạo feedback mới")
    void testSaveFeedback()  {
        // Given
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(testFeedback);

        // When
        ResponseEntity<Feedback> response = feedbackController.saveFeedback(testFeedback);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Great book!", response.getBody().getComment());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Test 13: Update Feedback - Cập nhật feedback")
    void testUpdateFeedback()  {
        // Given
        testFeedback.setComment("Updated feedback!");
        when(feedbackService.updateFeedback(any(Feedback.class))).thenReturn(testFeedback);

        // When
        ResponseEntity<Feedback> response = feedbackController.updateFeedback(testFeedback);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated feedback!", response.getBody().getComment());
        verify(feedbackService, times(1)).updateFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Test 14: Delete Feedback - Xóa feedback")
    void testDeleteFeedback()  {
        // Given
        doNothing().when(feedbackService).deleteFeedback(anyLong());

        // When
        ResponseEntity<Void> response = feedbackController.deleteFeedback(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(feedbackService, times(1)).deleteFeedback(1L);
    }

    @Test
    @DisplayName("Test 15: Get Feedbacks Paged and Sorted - Lấy feedback phân trang và sắp xếp")
    void testGetFeedbacksPagedAndSorted()  {
        // Given
        List<Feedback> feedbacks = Collections.singletonList(testFeedback);
        Page<Feedback> feedbackPage = new PageImpl<>(feedbacks, PageRequest.of(0, 5), 1);
        when(feedbackService.getAllFeedbacks(any(Pageable.class))).thenReturn(feedbackPage);

        // When
        ResponseEntity<Page<Feedback>> response = feedbackController.getAllFeedbacksPagedAndSorted(
                "id", 0, 5, "asc");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        verify(feedbackService, times(1)).getAllFeedbacks(any(Pageable.class));
    }
}
