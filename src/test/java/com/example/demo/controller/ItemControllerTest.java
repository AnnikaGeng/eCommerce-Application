package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void test_get_item_by_id_happy_path() {
        Item item = new Item();
        item.setName("Pencil");
        item.setPrice(new BigDecimal(5.2));
        item.setId(1L);
        item.setDescription("this is a red pencil");

        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item1 = response.getBody();
        assertNotNull(item1);
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getPrice(), item1.getPrice());
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getDescription(), item1.getDescription());
    }

    @Test
    public void get_item_by_name_happy_path() {
        Item item = new Item();
        item.setName("Pencil");
        item.setDescription("this is a red pencil");

        when(itemRepo.findByName(anyString())).thenReturn((List<Item>) Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Pencil");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertEquals(Arrays.asList(item), items);
    }

    @Test
    public void get_all_items() {
        Item item1 = new Item();
        item1.setName("Pencil");

        Item item2 = new Item();
        item2.setName("Pen");

        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepo.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseBody = response.getBody();
        assertEquals(items, responseBody);
    }
}
