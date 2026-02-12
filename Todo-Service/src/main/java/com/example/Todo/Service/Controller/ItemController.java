package com.example.Todo.Service.Controller;

import com.example.Todo.Service.DTO.UserTokenResponse;
import com.example.Todo.Service.Entity.Item;
import com.example.Todo.Service.Services.ItemService;
import com.example.Todo.Service.Services.UserServiceClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/todo")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody @Valid Item item,
                                     @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization header is missing");
            }

            UserTokenResponse user = userServiceClient.checkToken(token);
            item.setUserId(user.getUserId());
            Item savedItem = itemService.addItem(item);
            return ResponseEntity.ok(savedItem);

        } catch (feign.FeignException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public void deleteItem(@PathVariable Long id,
                           @RequestHeader("Authorization") String token) {

        UserTokenResponse user = userServiceClient.checkToken(token);
        itemService.deleteItem(id);
    }


    @PatchMapping("/update/{id}")
    public Item patchItem(@PathVariable Long id,
                          @RequestBody Map<String, Object> updates,
                          @RequestHeader("Authorization") String token) {

        UserTokenResponse user = userServiceClient.checkToken(token);
        return itemService.patchItem(id, updates, user.getUserId());
    }


    @GetMapping("/search/{id}")
    public Item searchItem(@PathVariable Long id,
                           @RequestHeader("Authorization") String token) {

        userServiceClient.checkToken(token);
        return itemService.searchItem(id);
    }
}
