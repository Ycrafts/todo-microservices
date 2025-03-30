package com.taskService.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.taskService.dtos.TodoItemDTO;
import com.taskService.models.TodoItem;
import com.taskService.models.TodoItem.TodoStatus;
import com.taskService.models.TodoList;
import com.taskService.repositories.TodoItemRepository;
import com.taskService.repositories.TodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final TodoListRepository todoListRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); 
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    public TodoItemDTO createTodoItem(TodoItemDTO todoItemDTO) {
        String currentUserId = getCurrentUserId();

        Optional<TodoList> todoListOptional = todoListRepository.findById(todoItemDTO.getTodoListId());
        if (todoListOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo List not found");
        }
        TodoList todoList = todoListOptional.get();

        if (!todoList.getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to add items to this todo list");
        }

        TodoItem todoItem = new TodoItem();
        todoItem.setName(todoItemDTO.getName());
        todoItem.setDescription(todoItemDTO.getDescription());
        todoItem.setDueDate(todoItemDTO.getDueDate());
        todoItem.setCreatedAt(LocalDateTime.now());
        todoItem.setUpdatedAt(LocalDateTime.now());
        todoItem.setTodoList(todoList);
        todoItem.setCreatedBy(currentUserId);

        if (todoItemDTO.getStatus() == null) {
            todoItem.setStatus(TodoStatus.PENDING);
        } else {
            todoItem.setStatus(todoItemDTO.getStatus());
        }

        TodoItem savedTodoItem = todoItemRepository.save(todoItem);
        return mapToDTO(savedTodoItem);
    }

    public List<TodoItemDTO> getAllTodoItems() {
        String currentUserId = getCurrentUserId();
        return todoItemRepository.findByCreatedBy(currentUserId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TodoItemDTO getTodoItemById(Long id) {
        String currentUserId = getCurrentUserId();
        Optional<TodoItem> todoItemOptional = todoItemRepository.findById(id);

        if (todoItemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo Item not found");
        }

        TodoItem todoItem = todoItemOptional.get();

        //the current user owns the TodoList of this TodoItem
        if (!todoItem.getTodoList().getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this todo item");
        }

        return mapToDTO(todoItem);
    }

    public List<TodoItemDTO> getAllTodoItemsForTodoList(Long todoListId) {
        String currentUserId = getCurrentUserId();
        Optional<TodoList> todoListOptional = todoListRepository.findById(todoListId);

        if (todoListOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo List not found");
        }
        TodoList todoList = todoListOptional.get();

        // the current user owns this TodoList
        if (!todoList.getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access items in this todo list");
        }

        List<TodoItem> todoItems = todoItemRepository.findByTodoList(todoList);
        return todoItems.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TodoItemDTO updateTodoItem(Long id, TodoItemDTO updatedTodoItemDTO) {
        String currentUserId = getCurrentUserId();
        Optional<TodoItem> todoItemOptional = todoItemRepository.findById(id);

        if (todoItemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo Item not found");
        }

        TodoItem existingTodoItem = todoItemOptional.get();

        // the current user owns the TodoList of this TodoItem
        if (!existingTodoItem.getTodoList().getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this todo item");
        }

        existingTodoItem.setName(updatedTodoItemDTO.getName());
        existingTodoItem.setDescription(updatedTodoItemDTO.getDescription());
        existingTodoItem.setDueDate(updatedTodoItemDTO.getDueDate());
        existingTodoItem.setUpdatedAt(LocalDateTime.now());

        if (updatedTodoItemDTO.getStatus() != null) {
            existingTodoItem.setStatus(updatedTodoItemDTO.getStatus());
        } else {
            existingTodoItem.setStatus(TodoStatus.PENDING);
        }
      
        TodoItem updatedTodoItem = todoItemRepository.save(existingTodoItem);
        return mapToDTO(updatedTodoItem);
    }

    public void deleteTodoItem(Long id) {
        String currentUserId = getCurrentUserId();
        Optional<TodoItem> todoItemOptional = todoItemRepository.findById(id);

        if (todoItemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo Item not found");
        }

        TodoItem todoItem = todoItemOptional.get();

        // the current user owns the TodoList of this TodoItem
        if (!todoItem.getTodoList().getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this todo item");
        }

        todoItemRepository.deleteById(id);
    }

    private TodoItemDTO mapToDTO(TodoItem todoItem) {
        return new TodoItemDTO(
                todoItem.getId(),
                todoItem.getName(),
                todoItem.getDescription(),
                todoItem.getStatus(),
                todoItem.getDueDate(),
                todoItem.getCreatedAt(),
                todoItem.getUpdatedAt(),
                todoItem.getTodoList().getId(),
                todoItem.getCreatedBy() // Set createdByUserId directly
        );
    }
}