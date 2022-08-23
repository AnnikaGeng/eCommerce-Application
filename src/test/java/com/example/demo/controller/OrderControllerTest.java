package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void test_submit_order_happy_path() {
        Item item = new Item();
        item.setName("Pen");
        item.setPrice(new BigDecimal(23.4));

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setUsername("annika");
        user.setCart(cart);
        cart.setUser(user);

        when(userRepo.findByUsername(anyString())).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("annika");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(user.getUsername(), userOrder.getUser().getUsername());
        assertEquals(item.getPrice(), userOrder.getTotal());
    }

    @Test
    public void test_get_user_order_history() {
        Item item = new Item();
        item.setName("Pen");
        item.setPrice(new BigDecimal(23.4));

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setUsername("annika");
        user.setCart(cart);
        cart.setUser(user);

        when(userRepo.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<UserOrder> order = orderController.submit("annika");
        UserOrder userOrder = order.getBody();
        List<UserOrder> orders = Arrays.asList(userOrder);

        when(orderRepo.findByUser(any())).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("annika");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> o = response.getBody();
        assertEquals(orders.get(0), o.get(0));
    }
}
