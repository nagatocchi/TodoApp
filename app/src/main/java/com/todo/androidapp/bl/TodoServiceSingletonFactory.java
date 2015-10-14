package com.todo.androidapp.bl;

import android.content.Context;
import com.todo.androidapp.dal.SQLiteConnector;
/**
 * Created by Anika on 07.07.15
 */

/**
 * Makes sure there is only one Instance of the web-application connector and the
 * todo-service class.
 */
public class TodoServiceSingletonFactory {

    private static TodoService todoService;
    private static boolean webAppReachable;

    /**
     * Method for creating new instances of the web-application connector and
     * the todo-service classes if necessary.
     *
     * @param context The application context.
     * @return The instance of the todo-service class to use.
     */
    public static TodoService create(Context context) {
        if (todoService == null) {
            todoService = new TodoServiceImpl(webAppReachable);
            SQLiteConnector sqLiteConnector = new SQLiteConnector(context);
            if(webAppReachable) {
                WebAppConnector webAppConnector = new WebAppConnectorImpl();
                todoService.setWebAppConnector(webAppConnector);
            } else {
                WebAppConnector webAppConnector = new WebAppUnreachableImpl();
                todoService.setWebAppConnector(webAppConnector);
            }
            todoService.setSQLiteConnector(sqLiteConnector);
        }
        return todoService;
    }

    /**
     * Method for setting a variable to see if the application server is reachable.
     *
     * @param mWwebAppReachable Boolean application server reachable or not.
     */
    public static void setReachable(boolean mWwebAppReachable) {
        webAppReachable = mWwebAppReachable;
    }
}
