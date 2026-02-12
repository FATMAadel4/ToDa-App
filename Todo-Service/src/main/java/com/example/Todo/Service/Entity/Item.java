package com.example.Todo.Service.Entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Item details are required")
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_details_id")
    private ItemDetails itemDetails;
}
