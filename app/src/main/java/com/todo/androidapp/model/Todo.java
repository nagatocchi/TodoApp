package com.todo.androidapp.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * Created by Anika on 18.06.15
 */

/**
 * Todo object used the application.
 */
public class Todo implements Serializable{

    private String name;
    private String description;
    private boolean done;
    private boolean favorite;
    private GregorianCalendar expiry;
    private long id;

    public Todo() {
        this("empty", "empty", false, new GregorianCalendar(), 50);
    }

    public Todo(String name, String description, boolean favorite, GregorianCalendar expiry, int id) {
        this.name = name;
        this.description = description;
        this.done = false;
        this.favorite = favorite;
        this.expiry = expiry;
        this.id = id;
    }

    /**
     * Method for getting the name of a Todo.
     *
     * @return The name of the Todo.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method for getting the description of a Todo.
     *
     * @return The description of the Todo.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Method for checking if a Todo has been done.
     *
     * @return If the Todo has been done or not.
     */
    public boolean getDone() {
        return this.done;
    }

    /**
     * Method for checking if a Todo is urgent.
     *
     * @return If the Todo is urgent or not.
     */
    public boolean getFavorite() {
        return this.favorite;
    }

    /**
     * Method for getting the expiry date of a Todo.
     *
     * @return The expiry date of the Todo.
     */
    public GregorianCalendar getExpiry() {
        return this.expiry;
    }

    /**
     * Method for getting the ID of a Todo.
     *
     * @return The ID of the Todo.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Method for setting the name of a Todo.
     *
     * @param name The name to set for the Todo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method for setting the description of a Todo.
     *
     * @param description The description to set for the Todo.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method for setting if a Todo has been done.
     *
     * @param done If the Todo has been done or not.
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Method for setting if a Todo is urgent.
     *
     * @param favorite If the Todo is urgent or not.
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Method for setting the expiry date of a Todo.
     *
     * @param expiry The expiry date to set for the Todo.
     */
    public void setExpiry(GregorianCalendar expiry) {
        this.expiry = expiry;
    }

    /**
     * Method for setting the ID of a Todo.
     *
     * @param id The ID to set for the Todo.
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Method for printing the expiry date of a Todo.
     *
     * @return The readable expiry date of the Todo.
     */
    public String toString() {
        if(expiry != null) {
            return this.name + ", " + this.id + "  /  "
                    + String.valueOf(this.expiry.get(Calendar.YEAR)) + " "
                    + String.valueOf(this.expiry.get(Calendar.MONTH)) + " "
                    + String.valueOf(this.expiry.get(Calendar.DAY_OF_MONTH)) + "  /  "
                    + String.valueOf(this.expiry.get(Calendar.HOUR_OF_DAY)) + ":"
                    + String.valueOf(this.expiry.get(Calendar.MINUTE));
        } else {
            return this.name + ", " + this.id + "  /  " + "Expiry date is null";
        }
    }

}
