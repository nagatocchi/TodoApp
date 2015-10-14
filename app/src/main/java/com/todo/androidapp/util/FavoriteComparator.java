package com.todo.androidapp.util;

import com.todo.androidapp.model.Todo;

import java.util.Comparator;

/**
 * Created by Anika on 07.07.15
 */

/**
 * Compares two Todo objects by means of their urgency status.
 */
public class FavoriteComparator implements Comparator<Todo> {

    /**
     * Method for comparing the urgency status of two Todos.
     *
     * @param lhs First Todo to compare.
     * @param rhs Second Todo to compare.
     * @return An integer variable indicating the order of Todos.
     */
    @Override
    public int compare(Todo lhs, Todo rhs) {
        int lhsFav = 0;
        int rhsFav = 0;

        if(lhs.getFavorite()) {
            lhsFav = 1;
        }
        if(rhs.getFavorite())
        {
            rhsFav = 1;
        }
        return rhsFav - lhsFav;
    }

}
