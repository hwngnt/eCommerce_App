package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private static final String USERNAME = "username";
    private static final long ITEM_ID = 1L;
    private static final int QUANTITY = 2;
    private static final String PRICE = "21.45";

    private CartController cartController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);

        when(userRepository.findByUsername(USERNAME)).thenReturn(getUser());
        when(itemRepository.findById(ITEM_ID)).thenReturn(getItem());
    }

    @Test
    public void addToCartTest() {
        int expectedQuantity = QUANTITY + 1;
        BigDecimal expectedTotal = new BigDecimal(PRICE).multiply(BigDecimal.valueOf(expectedQuantity));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setQuantity(QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        Cart cart = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), USERNAME);
        assertEquals(cart.getItems().size(), expectedQuantity);
        assertEquals(cart.getTotal(), expectedTotal);
    }

    @Test
    public void addToCartTestFailureOnInvalidUser() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("");
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setQuantity(QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void addToCartTestFailureOnInvalidItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void removeFromCartTest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        Cart cart = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), USERNAME);
        assertEquals(cart.getItems().size(), 0);
        assertEquals(cart.getTotal().intValue(), 0);
    }

    @Test
    public void removeFromCartTestOnInvalidUser() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("");
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setQuantity(QUANTITY);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void removeFromCartTestOnInvalidItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(QUANTITY);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    private static User getUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setCart(getCart(user));
        return user;
    }

    private static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem().orElse(null));
        return cart;
    }

    private static Optional<Item> getItem() {
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setPrice(new BigDecimal(PRICE));
        return Optional.of(item);
    }
}
