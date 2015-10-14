package com.todo.androidapp.util;

import com.todo.androidapp.model.Todo;
import java.util.Comparator;

/**
 * Created by Anika on 07.07.15
 */

/**
 * Compares two Todo objects by means of their done status.
 */
public class DoneComparator implements Comparator<Todo>{

    /**
     * Method for comparing the done status of two Todos.
     *
     * @param lhs First Todo to compare.
     * @param rhs Second Todo to compare.
     * @return An integer variable indicating the order of Todos.
     */
    @Override
    public int compare(Todo lhs, Todo rhs) {
        int lhsDone = 0;
        int rhsDone = 0;

        if(lhs.getDone()) {
            lhsDone = 1;
        }
        if(rhs.getDone())
        {
            rhsDone = 1;
        }
        return lhsDone - rhsDone;
    }
}
