package com.taskService.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.taskService.models.TodoItem.TodoStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TodoItemDTO {

    private Long id;
    private String name;
    private String description;
    private TodoStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long todoListId; // To associate with a TodoList
    private String createdByUserId; // Changed from UserResponse to String for user ID
}