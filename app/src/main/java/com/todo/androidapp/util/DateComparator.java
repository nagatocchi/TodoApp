package com.todo.androidapp.util;

import com.todo.androidapp.model.Todo;

import java.util.Comparator;

/**
 * Created by Anika on 07.07.15
 */

/**
 * Compares two Todo objects by means of their expiry date.
 */
public class DateComparator implements Comparator<Todo> {

    /**
     * Method for comparing the expiry dates of two Todos.
     *
     * @param lhs First Todo to compare.
     * @param rhs Second Todo to compare.
     * @return An integer variable indicating the order of Todos.
     */
    @Override
    public int compare(Todo lhs, Todo rhs) {

        if(lhs.getExpiry().after(rhs.getExpiry())) {
            return -1;
        }else if(rhs.getExpiry().after(lhs.getExpiry()))
        {
            return 1;
        }
        return 0;
    }

}
