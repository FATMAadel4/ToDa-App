package com.example.Todo.Service.Services;

import com.example.Todo.Service.Entity.Item;
import com.example.Todo.Service.Entity.ItemDetails;
import com.example.Todo.Service.Exceptions.ProductNotFoundException;
import com.example.Todo.Service.Repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    public ItemService(ItemRepository itemRepository, ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ProductNotFoundException("Item not found");
        }
        itemRepository.deleteById(id);
    }


    public Item patchItem(Long id, Map<String, Object> updates, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Item not found"));


        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can't update this item");
        }


        if (updates.containsKey("title")) {
            item.setTitle(updates.get("title").toString());
        }

        if (updates.containsKey("itemDetails")) {
            ItemDetails details = objectMapper.convertValue(updates.get("itemDetails"), ItemDetails.class);
            item.setItemDetails(details);
        }

        return itemRepository.save(item);
    }

    public Item searchItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Item not found"));
    }
}
