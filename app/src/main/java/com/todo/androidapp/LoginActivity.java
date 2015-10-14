package com.todo.androidapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnKeyListener;
import com.todo.androidapp.bl.AuthenticationService;
import com.todo.androidapp.bl.AuthenticationServiceImpl;
import com.todo.androidapp.bl.InvalidCredentialsException;
import com.todo.androidapp.bl.UnknownAuthenticationException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    public static final String SERVER_REACHABLE_STRING = "serverReachable";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton = null;

    private AuthenticationService authService = null;

    /**
     * On create method for starting the view.
     *
     * @param savedInstanceState Instance state of the view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("TodoApp Login");
        if (authService == null) {
            authService = new AuthenticationServiceImpl();
        }
        setContentView(R.layout.activity_login);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailView = (EditText) findViewById(R.id.email);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        showProgress(true);

        // Check for server availability
        ServerReachableTask checkTask = new ServerReachableTask(authService);
        checkTask.execute((Void) null);
    }

    private void displayLoginContent() {

        // Set up the login form.
        mEmailView.setError(getString(R.string.error_field_required));
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(view);
                    return true;
                } else {
                    return false;
                }
            }
        });

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmailView.getText().toString().isEmpty()) {
                    mEmailView.setError(getString(R.string.error_field_required));
                }
                if (mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty()) {
                    mEmailSignInButton.setEnabled(false);
                } else {
                    mEmailSignInButton.setEnabled(true);
                }
                mEmailSignInButton.setError(null);
                mEmailSignInButton.clearFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty()){
                    mEmailSignInButton.setEnabled(false);
                } else {
                    mEmailSignInButton.setEnabled(true);
                }
                mEmailSignInButton.setFocusableInTouchMode(false);
                mEmailSignInButton.setError(null);
                mEmailSignInButton.clearFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });


        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    public void onClick(View view) {
        if(mEmailSignInButton.isEnabled()) {
            attemptLogin();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_no_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, authService);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() == 6 && isNumeric(password);
    }

    private boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public class ServerReachableTask extends AsyncTask<Void, Void, Boolean> {

        private final AuthenticationService mTaskAuthService;

        public ServerReachableTask(AuthenticationService authenticationService) {
            this.mTaskAuthService = authenticationService;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return authService.webAppReachable();
            } catch (IOException e) {
                Log.w("LoginActivity", "Error on server reachable check: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean serverReachable) {
            showProgress(false);
            if(serverReachable) {
                displayLoginContent();
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(LoginActivity.SERVER_REACHABLE_STRING, false);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final AuthenticationService mTaskAuthService;
        private int errorCode;

        private final int ERROR_CODE_GENERIC = 1337;
        private final int ERROR_CODE_CREDENTIALS = 1234;


        UserLoginTask(String email, String password, AuthenticationService taskAuthService) {
            mEmail = email;
            mPassword = password;
            mTaskAuthService = taskAuthService;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mTaskAuthService.authenticate(mEmail, mPassword);
                //Thread.sleep(2000);
                return true;
            } catch (InvalidCredentialsException e) {
                Log.i("UserLoginTask", e.getMessage());
                errorCode = ERROR_CODE_CREDENTIALS;
                return false;
            } catch (UnknownAuthenticationException e) {
                Log.e("UserLoginTask", e.getMessage(), e);
                errorCode = ERROR_CODE_GENERIC;
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean authenticationSucceeded) {
            mAuthTask = null;
            showProgress(false);

            if (authenticationSucceeded) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(LoginActivity.SERVER_REACHABLE_STRING, true);
                startActivity(intent);
            } else {
                if(errorCode == ERROR_CODE_CREDENTIALS) {
                    mEmailSignInButton.setFocusable(true);
                    mEmailSignInButton.setFocusableInTouchMode(true);
                    mEmailSignInButton.setError(getString(R.string.error_invalid_login));
                    mEmailSignInButton.requestFocus();
                }
                else if (errorCode == ERROR_CODE_GENERIC) {
                    mEmailSignInButton.setFocusable(true);
                    mEmailSignInButton.setFocusableInTouchMode(true);
                    mEmailSignInButton.setError(getString(R.string.error_connection));
                    mEmailSignInButton.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

