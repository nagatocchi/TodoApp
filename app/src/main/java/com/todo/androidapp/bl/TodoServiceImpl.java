package com.todo.androidapp.bl;

import android.os.AsyncTask;
import android.util.Log;
import com.todo.androidapp.dal.SQLiteConnector;
import com.todo.androidapp.model.Todo;

import java.security.spec.ECField;
import java.util.List;
/**
 * Created by Anika on 18.06.15
 */

/**
 * Connects the UI with the web connector class and data access layer
 * for managing the SQLite database.
 */
public class TodoServiceImpl implements TodoService {

    private SQLiteConnector sqLiteConnector;
    private WebAppConnector webAppConnector;
    private boolean mWebAppReachable;

    /**
     * Custom constructor
     *
     * @param webAppReachable Sets boolean value for this class if application server
     *                        is reachable or not.
     */
    public TodoServiceImpl(boolean webAppReachable) {
        mWebAppReachable = webAppReachable;
    }


    /**
     * Method for creating a new Todo in the SQLite database and the web application if reachable.
     *
     * @param todo Todo to create.
     */
    @Override
    public void createTodo(Todo todo) {
            if(mWebAppReachable) {
                createServerTodo(todo);
            }
            createLocalTodo(todo);
    }

    /**
     * Method for creating a new Todo in the SQLite database.
     *
     * @param todo Todo to create.
     */
    public void createLocalTodo(Todo todo) {
        sqLiteConnector.open();
        sqLiteConnector.saveTodo(todo);
        sqLiteConnector.close();
    }

    /**
     * Method for creating a new Todo in the web application.
     *
     * @param todo Todo to create.
     */
    public void createServerTodo(Todo todo) {
        try {
            WebAppConnectionTask connectionTask = new WebAppConnectionTask(webAppConnector, todo, WebAppAction.CREATE);
            connectionTask.execute((Void) null);
        } catch (Exception e) {
            Log.e("TodoServiceImpl", e.getMessage(), e);
        }
    }

    /**
     * Method for editing a Todo in the SQLite database and the web application if reachable.
     *
     * @param todo Todo to edit.
     */
    @Override
    public void editTodo(Todo todo) {
        try {
            sqLiteConnector.open();
            sqLiteConnector.editTodo(todo);
            if(mWebAppReachable) {
                WebAppConnectionTask connectionTask = new WebAppConnectionTask(webAppConnector, todo, WebAppAction.UPDATE);
                connectionTask.execute((Void) null);
            }
            sqLiteConnector.close();
        } catch (Exception e) {
            Log.e("TodoServiceImpl", e.getMessage(), e);
        }
    }

    /**
     * Method for deleting a Todo in the SQLite database and the web application if reachable.
     *
     * @param todo Todo to delete.
     */
    @Override
    public void deleteTodo(Todo todo) {
        sqLiteConnector.open();
        sqLiteConnector.deleteTodo(todo);
        if (mWebAppReachable) {
            deleteServerTodo(todo);
        }
        sqLiteConnector.close();
    }

    /**
     * Method for deleting a Todo in the web application.
     *
     * @param todo Todo to delete.
     */
    public void deleteServerTodo(Todo todo) {
        try {
            WebAppConnectionTask connectionTask = new WebAppConnectionTask(webAppConnector, todo, WebAppAction.DELETE);
            connectionTask.execute((Void) null);
        } catch (Exception e) {
            Log.e("TodoServiceImpl", e.getMessage(), e);
        }
    }

    /**
     * Method gets all existing Todos from the SQLite database.
     *
     * @return A list of the Todos present in the SQLite database.
     */
    @Override
    public List<Todo> getAllTodos() {
        sqLiteConnector.open();
        List<Todo> temp = sqLiteConnector.getAllTodos();
        sqLiteConnector.close();
        return temp;
    }

    /**
     * Method for setting the SQLite connector class for further usage.
     *
     * @param sqLiteConnector The SQLite connector class to use.
     */
    @Override
    public void setSQLiteConnector(SQLiteConnector sqLiteConnector) {
        this.sqLiteConnector = sqLiteConnector;
    }

    /**
     * Method for setting the web-application connector class for further usage.
     *
     * @param webAppConnector The web-application connector class to use.
     */
    @Override
    public void setWebAppConnector(WebAppConnector webAppConnector) {
        this.webAppConnector = webAppConnector;
    }

    /**
     * Method for getting the web-application connector used in this class.
     *
     * @return the web-application connector class used.
     */
    @Override
    public WebAppConnector getWebAppConnector() {
       return this.webAppConnector;
    }

    /**
     * Asynchron task for connecting to the web application.
     */
    class WebAppConnectionTask extends AsyncTask<Void, Void, Boolean> {
        private final WebAppConnector mConnector;
        private final Todo mTodo;
        private WebAppAction mAction;

        public WebAppConnectionTask(WebAppConnector connector, Todo todo, WebAppAction action) {
            mConnector = connector;
            mTodo = todo;
            mAction = action;
        }

        /**
         * Background method for selecting the right task to send to the application server.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                switch (mAction) {
                    case CREATE:
                        mConnector.createTodo(mTodo);
                        break;
                    case DELETE:
                        mConnector.deleteTodo(mTodo);
                        break;
                    case UPDATE:
                        mConnector.updateTodo(mTodo);
                        break;
                    default:
                        break;
                }

                return true;
            } catch (Exception e) {
                Log.i("TodoServiceImpl", e.getMessage());
                return false;
            }
        }
    }
}



