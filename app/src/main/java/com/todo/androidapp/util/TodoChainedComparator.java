package com.todo.androidapp.util;

import com.todo.androidapp.model.Todo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Anika on 07.07.15
 */

/**
 * Custom comparator class, sorts two Todo objects by the means of all comparators given.
 */
public class TodoChainedComparator implements Comparator<Todo> {

    private List<Comparator<Todo>> listComparators;

    /**
     * Custom constructor for setting the list and order of comparators.
     *
     * @param comparators Comparators to use (descending priority).
     */
    @SafeVarargs
    public TodoChainedComparator(Comparator<Todo>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    /**
     * Method for comparing two Todos.
     *
     * @param todo1 First Todo to compare.
     * @param todo2 Second Todo to compare.
     * @return An integer variable indicating the order of Todos.
     */
    @Override
    public int compare(Todo todo1, Todo todo2) {
        for (Comparator<Todo> comparator : listComparators) {
            int result = comparator.compare(todo1, todo2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
