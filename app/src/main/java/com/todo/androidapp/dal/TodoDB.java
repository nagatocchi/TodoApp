package com.todo.androidapp.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by Anika on 19.06.15
 */

/**
 * Creates the SQLite database. Also holds the column names.
 */
public class TodoDB extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "TodoApp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COMMA_SEP = ",";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";

    public static final String TABLE_NAME = "todo";
    public static final String COLUMN_ID_ENTRY_ID = "_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_DONE = "done";
    public static final String COLUMN_NAME_FAVORITE = "favorite";
    public static final String COLUMN_NAME_EXPIRY = "expires";
    public static final String COLUMN_NAME_DESCRIPTION = "description";

    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME
            +"(" + COLUMN_ID_ENTRY_ID + " integer primary key autoincrement, "
            + COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP
            + COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMMA_SEP
            + COLUMN_NAME_FAVORITE + TYPE_INTEGER + COMMA_SEP
            + COLUMN_NAME_DONE + TYPE_INTEGER + COMMA_SEP
            + COLUMN_NAME_EXPIRY + TYPE_INTEGER + ");";

    public TodoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method for creating a new SQLite database.
     *
     * @param database The database to create.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Method for upgrading the SQLite database to a new version.
     *
     * @param db The database to upgrade
     * @param oldVersion The old version number of the database.
     * @param newVersion The new version number of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TodoDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
