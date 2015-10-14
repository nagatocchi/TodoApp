package com.todo.androidapp.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.todo.androidapp.model.Todo;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
/**
 * Created by Anika on 18.06.15
 */

/**
 * Handles the SQLite database.
 */
public class SQLiteConnector {
    // Database fields
    private SQLiteDatabase database;
    private TodoDB todoDb;
    private String[] allColumns = {
            TodoDB.COLUMN_ID_ENTRY_ID, TodoDB.COLUMN_NAME_NAME,
            TodoDB.COLUMN_NAME_DESCRIPTION, TodoDB.COLUMN_NAME_FAVORITE,
            TodoDB.COLUMN_NAME_DONE, TodoDB.COLUMN_NAME_EXPIRY
            };

    public SQLiteConnector(Context context) {
        todoDb = new TodoDB(context);
    }

    /**
     * Method opens the SQLite database for writing.
     */
    public void open() throws SQLException {
        database = todoDb.getWritableDatabase();
    }

    /**
     * Method closes the SQLite database.
     */
    public void close() {
        todoDb.close();
    }

    /**
     * Method for creating a new Todo in the SQLite database.
     *
     * @param todo Todo to create.
     */
    public void saveTodo(Todo todo) throws SQLiteException {
        ContentValues values = new ContentValues();
        values.put(TodoDB.COLUMN_NAME_NAME, todo.getName());
        values.put(TodoDB.COLUMN_NAME_DESCRIPTION, todo.getDescription());

        values.put(TodoDB.COLUMN_NAME_FAVORITE, favoriteToInt(todo));
        values.put(TodoDB.COLUMN_NAME_DONE, doneToInt(todo));

        if(todo.getExpiry() != null) {
            long convertedValue = todo.getExpiry().getTimeInMillis();
            values.put(TodoDB.COLUMN_NAME_EXPIRY, convertedValue);
        } else {
            values.put(TodoDB.COLUMN_NAME_EXPIRY, "null");
        }

        long rowId = database.insert(TodoDB.TABLE_NAME, null,  values);
        todo.setId(rowId);
    }

    /**
     * Method for deleting a Todo in the SQLite database.
     *
     * @param todo Todo to delete.
     */
    public void deleteTodo(Todo todo) {
        long id = todo.getId();
        database.delete(TodoDB.TABLE_NAME, TodoDB.COLUMN_ID_ENTRY_ID + " = " + id, null);
    }

    /**
     * Method for editing a Todo in the SQLite database.
     *
     * @param todo Todo to edit.
     */
    public void editTodo(Todo todo) {
        long id = todo.getId();
        long convertedValue = todo.getExpiry().getTimeInMillis();

        ContentValues values = new ContentValues();

        values.put(TodoDB.COLUMN_NAME_NAME, todo.getName());
        values.put(TodoDB.COLUMN_NAME_DESCRIPTION, todo.getDescription());
        values.put(TodoDB.COLUMN_NAME_FAVORITE, favoriteToInt(todo));
        values.put(TodoDB.COLUMN_NAME_DONE, doneToInt(todo));
        values.put(TodoDB.COLUMN_NAME_EXPIRY, convertedValue);

        database.update(TodoDB.TABLE_NAME, values, TodoDB.COLUMN_ID_ENTRY_ID + " = " + id, null);
    }

    /**
     * Method gets all existing Todos from the SQLite database.
     *
     * @return A list of the Todos present in the SQLite database.
     */
    public List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<Todo>();

        Cursor cursor = database.query(TodoDB.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Todo todo = cursorToTodo(cursor);
            todos.add(todo);
            cursor.moveToNext();
        }
        cursor.close();
        return todos;
    }

    /**
     * Method creates a new Todo from a cursor element.
     *
     * @param cursor The cursor to parse.
     * @return A Todo.
     */
    private Todo cursorToTodo(Cursor cursor) {
        Todo todo = new Todo();
        todo.setId(cursor.getLong(0));
        todo.setName(cursor.getString(1));
        todo.setDescription(cursor.getString(2));
        if(cursor.getInt(3) == 0) {
            todo.setFavorite(false);
        } else {
            todo.setFavorite(true);
        }
        if(cursor.getInt(4) == 0) {
            todo.setDone(false);
        } else {
            todo.setDone(true);
        }
        GregorianCalendar convert = new GregorianCalendar();
        convert.setTimeInMillis(cursor.getLong(5));
        todo.setExpiry(convert);
        return todo;
    }

    /**
     * Method changes the favorite setting of a Todo from boolean to integer.
     * SQLite cannot store boolean values.
     *
     * @param todo The Todo to parse.
     * @return An integer value based on the boolean favorite setting of
     * the Todo (0 if false, 1 if true).
     */
    private int favoriteToInt(Todo todo) {
        if(!todo.getFavorite()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Method changes the done setting of a Todo from boolean to integer.
     * SQLite cannot store boolean values.
     *
     * @param todo The Todo to parse.
     * @return An integer value based on the boolean done setting of
     * the Todo (0 if false, 1 if true).
     */
    private int doneToInt(Todo todo) {
        if(!todo.getDone()) {
            return 0;
        } else {
            return  1;
        }
    }

}
