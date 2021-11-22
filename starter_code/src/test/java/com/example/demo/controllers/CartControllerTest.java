package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private User user;
    private Cart cart;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Before
    public void setUpUserAndCart(){
        user = new User();
        user.setUsername("Shahida");
        user.setPassword("12345678");

        cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
    }

    @Before
    public void setUpItems(){
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L,"Rice", new BigDecimal(5.89), "Multrgrain Product"));
        items.add(new Item(2L,"Meat", new BigDecimal(5.76), "Cow Meat Red"));
        items.add(new Item(3L,"Banana", new BigDecimal(1.25), "Fresh fruit"));

        when(userRepository.findByUsername("Shahida")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(items.get(0)));
    }

    @Test
    public void add_to_cart(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Shahida");
        request.setItemId(1L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response);
        Cart obj = response.getBody();
        assertNotNull(obj);
        assertEquals(new BigDecimal(5.89), obj.getItems().get(0).getPrice());
        assertEquals("Rice", obj.getItems().get(0).getName());
        assertEquals("Shahida", obj.getUser().getUsername());
        
    }
    @Test
    public void add_to_cart_fail_user(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("abc");
        request.setItemId(1L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        
        assertEquals(404, response.getStatusCodeValue());
        Cart obj = response.getBody();
        assertNull(obj);
        add_to_cart();
    }
    @Test
    public void add_to_cart_fail_item_not_found(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Shahida");
        request.setItemId(5L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        
        assertEquals(404, response.getStatusCodeValue());
        Cart obj = response.getBody();
        assertNull(obj);
    }
    @Test
    public void remove_from_cart(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Shahida");
        request.setItemId(1L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response);
        Cart obj = response.getBody();
        assertNotNull(obj);
        assertEquals(0, obj.getItems().size());
    }
    
    @Test
    public void remove_from_cart_unsuccessful(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Shahida");
        request.setItemId(4L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        
        assertEquals(404, response.getStatusCodeValue());
        
        assertNull(response.getBody());
        add_to_cart_fail_user();
        
    }
}
