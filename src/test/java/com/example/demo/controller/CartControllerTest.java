package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }


    @Test
    public void test_add_to_cart_happy_path() {
        Cart emptyCart = new Cart();

        User user = new User();
        user.setUsername("annika");
        user.setCart(emptyCart);
        emptyCart.setUser(user);

        Item item = new Item();
        item.setName("Pen");
        item.setPrice(new BigDecimal(23.4));

        when(userRepo.findByUsername(anyString())).thenReturn(user);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(
                new ModifyCartRequest("annika", 1, 1)
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(user.getUsername(), cart.getUser().getUsername());
        assertEquals(item.getPrice(), cart.getTotal());
        assertEquals(Arrays.asList(item), cart.getItems());
    }

    @Test
    public void remove_from_cart_happy_test() {
        Cart emptyCart = new Cart();

        User user = new User();
        user.setUsername("annika");
        user.setCart(emptyCart);
        emptyCart.setUser(user);

        Item item = new Item();
        item.setName("Pen");
        item.setPrice(new BigDecimal(23.4));

        when(userRepo.findByUsername(anyString())).thenReturn(user);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        cartController.addTocart(new ModifyCartRequest("annika", 1, 1));
        ResponseEntity<Cart> response = cartController.removeFromcart(new ModifyCartRequest("annika", 1, 1));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }
}
