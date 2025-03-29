package com.taskService.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskService.models.TodoItem;
import com.taskService.models.TodoList;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem,Long> {
    List<TodoItem> findByCreatedBy(String createdBy); // Changed parameter type to String
    List<TodoItem> findByTodoList(TodoList todoList);
}
