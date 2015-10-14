package com.todo.androidapp.bl;

import com.todo.androidapp.dal.SQLiteConnector;
import com.todo.androidapp.model.Todo;
import java.util.List;
/**
 * Created by Anika on 18.06.15
 */
public interface TodoService {
    /**
     * save Todo to SQLite / call Server
     */
    void createTodo(Todo todo);
    void createLocalTodo(Todo todo);
    void createServerTodo(Todo todo);
    void deleteTodo(Todo todo);
    void deleteServerTodo(Todo todo);
    void editTodo(Todo todo);
    List<Todo> getAllTodos();
    void setSQLiteConnector(SQLiteConnector sqLiteConnector);
    void setWebAppConnector(WebAppConnector webAppConnector);
    WebAppConnector getWebAppConnector();
}
