package com.todo.androidapp.bl;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
/**
 * Created by Anika on 03.07.15
 */

/**
 * Give the user input credentials to the application server and check
 * if they are valid.
 */
public class AuthenticationServiceImpl implements AuthenticationService{

    /**
     * Method for authenticating the user input credentials.
     *
     * @param username username taken from the email field.
     * @param pw user input password.
     * @throws InvalidCredentialsException if the input username and password
     * are wrong or do not match.
     * @throws  UnknownAuthenticationException if something went wrong connecting to
     * the application server.
     */
    @Override
    public void authenticate(String username, String pw) throws InvalidCredentialsException, UnknownAuthenticationException{
        HttpURLConnection urlConnection = null;
        boolean invalidCredentials = true;
        try {

            JSONObject keyArg = new JSONObject();
            keyArg.put("email", username);
            keyArg.put("pwd", pw);

            URL url = new URL(WebAppConnector.WEBAPP_URL + "users/auth/");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(keyArg.toString());
            out.close();
            Log.i("AuthenticationService", "ResponseCode: " + urlConnection.getResponseCode());

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            if (!readStream(in)){
                invalidCredentials = true;
            } else
                invalidCredentials = false;
        } catch (Exception e) {
            throw new UnknownAuthenticationException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        if(invalidCredentials) {
            throw new InvalidCredentialsException("Invalid Credentials for Username: " + username);
        }
    }

    /**
     * Method to check server returned input-stream.
     *
     * @param in The passed input-stream.
     * @return Boolean if method was successful.
     * @throws IOException if this reader is closed or some other I/O error occurs.
     */
    private boolean readStream(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        if(total.toString().equals(Boolean.TRUE.toString())){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to check if the application server is available.
     *
     * @return Boolean if application server is reachable.
     * @throws IOException if there is an IO error during the retrieval.
     */
    public boolean webAppReachable() throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection con =
                (HttpURLConnection) new URL(WebAppConnector.WEBAPP_URL).openConnection();
        con.setConnectTimeout(5000);
        con.setRequestMethod("HEAD");
        return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    }
}
