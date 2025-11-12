package com.sfm.backend.repository;

import com.sfm.backend.model.Item;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepository {
    private final Map<Long, Item> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public ItemRepository() {
        save(new Item(null, "Example 1", "Első példa elem"));
        save(new Item(null, "Example 2", "Második példa elem"));
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public Item save(Item item) {
        if (item.getId() == null) {
            long id = idGenerator.incrementAndGet();
            item.setId(id);
        }
        store.put(item.getId(), item);
        return item;
    }
}
