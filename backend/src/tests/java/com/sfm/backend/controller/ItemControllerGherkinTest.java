package com.sfm.backend.controller;

import com.sfm.backend.model.Item;
import com.sfm.backend.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Unit tesztek az ItemController-hez")
public class ItemControllerGherkinTest {

    @Test
    @DisplayName("Given repository with two items, when list called, then return both items")
    void givenRepoWithTwoItems_whenListCalled_thenReturnBoth() {
        // Given
        List<Item> items = Arrays.asList(
                new Item(10L, "Item 1", "Desc 1"),
                new Item(20L, "Item 2", "Desc 2")
        );

        // Use a tiny stub repository instead of Mockito to avoid mocking concrete class issues
        ItemRepository repo = new ItemRepository() {
            @Override
            public List<Item> findAll() {
                return items;
            }
        };

        // When
        ItemController controller = new ItemController(repo);
        List<Item> result = controller.list();

        // Then
        assertEquals(items, result);
    }
}
