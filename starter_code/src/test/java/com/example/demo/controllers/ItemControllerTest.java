package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
        
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L,"Milk", new BigDecimal(3.89), "Dairy PRODUCT"));
        items.add(new Item(2L,"Carrot", new BigDecimal(1.76), "Vegetable"));
        items.add(new Item(3L,"Mango", new BigDecimal(5.25), "Fresh fruit"));

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findByName("Milk")).thenReturn(Arrays.asList(items.get(0)));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(items.get(0)));
    }

    @Test
    public void getItems(){
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(3, items.size());
    }
    @Test
    public void items_not_found(){
    	 ResponseEntity<Item> response = itemController.getItemById(5L);
         assertNull(response.getBody());
         assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void get_Item_By_Id(){
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals("Milk", item.getName());
        assertEquals(new BigDecimal(3.89), item.getPrice());
    
    }
    
    @Test
    public void get_Items_By_Name_fail(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("DOG");
        assertNull(response.getBody());
        assertEquals(404,response.getStatusCodeValue());

    }
    @Test
    public void get_Items_By_Name(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("");
        assertNull(response.getBody());
        assertEquals(404,response.getStatusCodeValue());
        get_Items_By_Name_fail();
        
        ResponseEntity<List<Item>> response1 = itemController.getItemsByName("null");
        assertNull(response1.getBody());
        assertEquals(404,response1.getStatusCodeValue());
    }
}
