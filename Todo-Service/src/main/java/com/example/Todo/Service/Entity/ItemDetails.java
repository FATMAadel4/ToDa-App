package com.example.Todo.Service.Entity;
import com.example.Todo.Service.Enums.Priority;
import com.example.Todo.Service.Enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "item_details")

public class ItemDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;
    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private Status status;
    @PastOrPresent(message = "Created date can't be in the future")
    private LocalDateTime createdAt = LocalDateTime.now(); }
