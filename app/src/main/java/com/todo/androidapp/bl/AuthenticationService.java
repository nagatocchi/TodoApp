package com.todo.androidapp.bl;

import java.io.IOException;

/**
 * Created by Anika on 03.07.15
 */
public interface AuthenticationService {
        void authenticate(String username, String pw) throws InvalidCredentialsException, UnknownAuthenticationException;
        boolean webAppReachable() throws IOException;
    }
