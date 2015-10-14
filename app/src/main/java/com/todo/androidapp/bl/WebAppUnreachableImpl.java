package com.todo.androidapp.bl;

import com.todo.androidapp.model.Todo;

import java.util.List;

/**
 * Created by Anika on 13.07.15
 */

/**
 * Connects the running application to database of the web application.
 * Empty Methods for security reasons - class is only used when web-application is not reachable.
 */
public class WebAppUnreachableImpl implements WebAppConnector {
    @Override
    public Todo createTodo(Todo item) throws Exception {
        return null;
    }

    @Override
    public List<Todo> readAllTodos() throws Exception {
        return null;
    }

    @Override
    public Todo updateTodo(Todo item) throws Exception {
        return null;
    }

    @Override
    public boolean deleteTodo(Todo item) throws Exception {
        return false;
    }
}
