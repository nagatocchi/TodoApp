package com.todo.androidapp.bl;

import android.util.Log;
import com.todo.androidapp.model.Todo;
import com.todo.androidapp.util.ConvertCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Anika on 03.07.15
 */

/**
 * Connects the running application to database of the web application.
 */
public class WebAppConnectorImpl implements WebAppConnector{
    public static final String WEBAPP_URL = "http://127.0.0.1:8080/TodolistWebapp/";

    /**
     * Method for creating a new Todo in the web application.
     *
     * @param item Todo to create.
     * @throws Exception if there was an error creating a new Todo or connecting to the database.
     */
    @Override
    public Todo createTodo(Todo item) throws Exception{
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(WEBAPP_URL + "todos/");
            JSONObject keyArg = new JSONObject();
            String date = ConvertCalendar.convertCalendarToReadableString((item.getExpiry()));
            keyArg.put("id", item.getId());
            keyArg.put("expiry", date);
            keyArg.put("name", item.getName());
            keyArg.put("description", item.getDescription());

            Log.i("WebAppConnector", keyArg.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());

            out.write(keyArg.toString());

            out.close();
            Log.i("WebAppConnector", "CREATE ResponseCode: " + urlConnection.getResponseCode());

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Method gets all existing Todos from the web-application.
     *
     * @return A list of the Todos present in database of the web-application.
     * @throws Exception if there was an error reading or connecting to the database.
     */
    @Override
    public List<Todo> readAllTodos() throws Exception{
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(WEBAPP_URL + "todos/");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Log.i("WebAppConnector", "READ_ALL_TODOS ResponseCode: " + urlConnection.getResponseCode());

            List<Todo> temp = readTodoListFromInputstream(in);
            return temp;
            
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Method for editing a Todo in web-application database.
     *
     * @param item Todo to edit.
     * @throws Exception if there was an error editing the Todo or connecting to the database.
     */
    @Override
    public Todo updateTodo(Todo item) throws Exception{
        /* Angabe: "Die durch die SQLite Datenbank zugewiesenen IDs k?nnen durch
        die Webanwendung ?bernommen werden."
        Doch die mitgelieferte Web-Application uebernimmt die IDs nicht sondern weist den Todos neue IDs
        (Abhaengig vom Server-start und von 0 ansteigend) zu.
        In dieser Implementation wird davon ausgegangen, dass die IDs der Todos in der lokalen Datenbank,
        sowie in der Web-Applikation gleich sind. */
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(WEBAPP_URL + "todos/");
            JSONObject keyArg = new JSONObject();
            String date = ConvertCalendar.convertCalendarToReadableString((item.getExpiry()));
            keyArg.put("id", item.getId());
            keyArg.put("expiry", date);
            keyArg.put("name", item.getName());
            keyArg.put("description", item.getDescription());

            Log.i("WebAppConnector", keyArg.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(keyArg.toString());

            out.close();
            Log.i("WebAppConnector", "UPDATE ResponseCode: " + urlConnection.getResponseCode());

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;

    }

    /**
     * Method for deleting a Todo in the SQLite database and the web application if reachable.
     *
     * @param item Todo to delete.
     * @throws Exception if there was an error deleting the Todo or connecting to the database.
     */
    @Override
    public boolean deleteTodo(Todo item) throws Exception{
        /* Angabe: "Die durch die SQLite Datenbank zugewiesenen IDs k?nnen durch
        die Webanwendung ?bernommen werden."
        Doch die mitgelieferte Web-Application uebernimmt die IDs nicht sondern weist den Todos neue IDs
        (Abhaengig vom Server-start und von 0 ansteigend) zu.
        In dieser Implementation wird davon ausgegangen, dass die IDs der Todos in der lokalen Datenbank,
        sowie in der Web-Applikation gleich sind. */
        HttpURLConnection urlConnection = null;
        long dataItemId = item.getId();
        try {
            URL url = new URL(WEBAPP_URL + "todos/" + dataItemId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Log.i("WebAppConnector", "DELETE ResponseCode: " + urlConnection.getResponseCode());
            if(readBooleanFromInputstream(in)) {
                Log.i("WebAppConnector", "DELETE todo successful");
            } else {
                Log.e("WebAppConnector", "DELETE todo unsuccessful");
            }

            return readBooleanFromInputstream(in);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Method reads the input-stream and checks for a boolean.
     *
     * @param in The input-stream.
     * @return The boolean value read from the input-stream.
     * @throws IOException if this reader is closed or some other I/O error occurs.
     */
    private boolean readBooleanFromInputstream(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        return total.toString().equals(Boolean.TRUE.toString());
    }

    /**
     * Method reads the input-stream and compiles a list of Todos.
     *
     * @param in The input-stream.
     * @return The list of existing Todos read from the input-stream.
     * @throws IOException if this reader is closed or some other I/O error occurs.
     */
    private List<Todo> readTodoListFromInputstream(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        List<Todo> todoList = new ArrayList<Todo>();
        JSONArray jArray ;
        JSONObject jObject;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line + "\n");
            }
            jArray = new JSONArray(total.toString());
        } catch (Exception e) {
            Log.e("WebAppConnector", e.getMessage(), e);
            throw new NullPointerException();
        }


        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String name = oneObject.getString("name");
                String id = oneObject.getString("id");
                String description = oneObject.getString("description");
                String expiry = oneObject.getString("expiry");
                GregorianCalendar date = null;
                if(!expiry.equals("null")) {
                    date = new GregorianCalendar();
                    date.setTimeInMillis(Long.parseLong(expiry));
                }
                int idInt = Integer.parseInt(id);
                todoList.add(new Todo(name, description, false, date, idInt));
                System.out.println(todoList.get(i));
            } catch (JSONException e) {
                Log.e("WebAppConnector", e.getMessage(), e);
            }
        }
        return  todoList;
    }

}


