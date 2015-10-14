package com.todo.androidapp.bl;

import com.todo.androidapp.model.Todo;

import java.util.List;

/**
 * Created by Anika on 13.07.15
 */
public interface WebAppConnector {
    String WEBAPP_URL = "http://127.0.0.1:8080/TodolistWebapp/";

    Todo createTodo(Todo item) throws Exception;
    List<Todo> readAllTodos() throws Exception;
    Todo updateTodo(Todo item) throws Exception;
    boolean deleteTodo(Todo item) throws Exception;
}
