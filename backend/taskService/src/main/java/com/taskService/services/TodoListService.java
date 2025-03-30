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

import com.taskService.models.TodoList;
import com.taskService.dtos.TodoListDTO;
import com.taskService.repositories.TodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoListService {

    private final TodoListRepository todoListRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    public List<TodoListDTO> getAllTodoLists() {
        String currentUserId = getCurrentUserId();
        return todoListRepository.findByCreatedBy(currentUserId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TodoListDTO getTodoListById(Long id) {
        String currentUserId = getCurrentUserId();
        Optional<TodoList> todoListOptional = todoListRepository.findById(id);

        if (todoListOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo List not found");
        }

        TodoList todoList = todoListOptional.get();

        if (!todoList.getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this todo list");
        }

        return mapToDTO(todoList);
    }

    public TodoListDTO createTodoList(TodoListDTO todoListDTO) {
        String currentUserId = getCurrentUserId();
        TodoList todoList = new TodoList();
        todoList.setName(todoListDTO.getName());
        todoList.setDescription(todoListDTO.getDescription());
        todoList.setCreatedAt(LocalDateTime.now());
        todoList.setUpdatedAt(LocalDateTime.now());
        todoList.setCreatedBy(currentUserId);
        TodoList savedTodoList = todoListRepository.save(todoList);
        return mapToDTO(savedTodoList);
    }

    public TodoListDTO updateTodoList(Long id, TodoListDTO updatedTodoListDTO) {
        String currentUserId = getCurrentUserId();
        return todoListRepository.findById(id)
                .map(existingTodoList -> {
                    if (!existingTodoList.getCreatedBy().equals(currentUserId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this todo list");
                    }
                    existingTodoList.setName(updatedTodoListDTO.getName());
                    existingTodoList.setDescription(updatedTodoListDTO.getDescription());
                    existingTodoList.setUpdatedAt(LocalDateTime.now());
                    TodoList savedTodoList = todoListRepository.save(existingTodoList);
                    return mapToDTO(savedTodoList);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo List not found"));
    }

    public void deleteTodoList(Long id) {
        String currentUserId = getCurrentUserId();
        Optional<TodoList> todoListOptional = todoListRepository.findById(id);
        if (todoListOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo List not found");
        }
        TodoList todoList = todoListOptional.get();
        if (!todoList.getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this todo list");
        }
        todoListRepository.deleteById(id);
    }

    private TodoListDTO mapToDTO(TodoList todoList) {
        TodoListDTO dto = new TodoListDTO();
        dto.setId(todoList.getId());
        dto.setName(todoList.getName());
        dto.setDescription(todoList.getDescription());
        dto.setCreatedAt(todoList.getCreatedAt());
        dto.setUpdatedAt(todoList.getUpdatedAt());
        dto.setCreatedByUserId(todoList.getCreatedBy()); 
        return dto;
    }
}