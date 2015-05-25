package com.example.willy.storyapp2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

/**
 * The MainActivity is the start screen of the app that shows 3 buttons: log in, sign up, and
 * start storyshowcase and also a picture
 *
 * @author  Willy Larsson
 * @version 1.0?
 * @since   2015-03-15
 */
public class AuthenticateActivity extends Activity {

    /**
     * action: used to set the text on mButton.
     * userField: used to write the user name
     * passwordField: used to write the password
     * mButton: When pressed, takes user to storyMode.
     */
    protected String action;
    protected EditText userField;
    protected EditText passwordField;
    protected Button mButton;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        userField = (EditText) findViewById(R.id.userEdit);
        passwordField = (EditText) findViewById(R.id.passwordEdit);
        mButton = (Button) findViewById(R.id.button1);

        // Initializes the  progressbar for loading/waiting for the user of the app
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        // Makes the progressbar invisible (We don't want it to show directly when the app screen shows)
        progressBar.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras(); //bundle is used to get information via the intent
        action = bundle.getString(MainActivity.TYPE); // the information of TYPE is saved in action, which is either "Log in" or "Sign up"
        mButton.setText(action); // the button text is set to the text of the action.

        mButton.setOnClickListener(new View.OnClickListener() {
            /**
             *  When the user presses the button to log in or sign up this method is called
             * @param v The view that has been clicked
             */
            @Override
            public void onClick(View v) {

                //save the username and password from the respective text fields
                String username = userField.getText().toString();
                String password = passwordField.getText().toString();

                //The user will sign up if the value of action is "Sign Up"
                if (action.equals(MainActivity.SIGNUP)) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Sign up using ParseUser and set the username and password in the database User in Parse's backend
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    /**
                     * A SignUpCallback is used to run code after signing up a ParseUser in a background thread
                     * <p>
                     *     The easiest way to use a SignUpCallback is through an anonymous inner class. Override the done
                     *     function to specify what the callback should do after the save is complete. The done function
                     *     will be run in the UI thread, while the signup happens in a background thread. This ensures that
                     *     the UI does not freeze while the signup happens.
                     * <p>
                     *     This code signs up the object myUser and does different things depending
                     *     on whether the signup succeeded or not.
                     *
                     * @param e The exception raised by the signUp, or null if it succeeded
                     */
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) { //Sign up succeeded

                                Toast.makeText(AuthenticateActivity.this, "Welcome, " +
                                        ParseUser.getCurrentUser().getUsername(),
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(
                                        AuthenticateActivity.this,
                                        StoryModeActivity.class));
                            } else { //Sign up didn't succeed. Look at the ParseException to figure out what went wrong
                                Toast.makeText(AuthenticateActivity.this,
                                        "Sign up failed! Try again.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);// Sets progressbar invisible

                            }

                        }

                    });
                } else { // If the value of action is "log in" the user should instead log in

					/*
					 * A LogInCallback is used to run code after logging in a user.
					 * <p>
					 *     The easiest way to use a LogInCallback is through an anonymous inner class. Override the done
					 *     function to specify what the callback should do after the login is complete. The done function
					 *     will be run in the UI thread, while the login happens in a background thread. This ensures that
					 *     the UI does not freeze while the save happens.
					 * <p>
					 *     this code logs in a user and does different things depending on whether
					  *    the login succeeded or not.
					  *
					  *    @username the username the user puts
					  *    @password the password the user puts
					 */
                    ParseUser.logInInBackground(username, password,
                            new LogInCallback() {
                                public void done(ParseUser user,
                                                 ParseException e) {
                                    // Shows progressbar
                                    progressBar.setVisibility(View.VISIBLE);
                                    if (user != null) {
                                        // The log in was successful
                                        startActivity(new Intent(
                                                AuthenticateActivity.this,
                                                StoryModeActivity.class));
                                    } else {
                                        // Shows progressbar
                                        progressBar.setVisibility(View.VISIBLE);
                                        // Login failed. Look at the
                                        // ParseException to see what happened.
                                        Toast.makeText(
                                                AuthenticateActivity.this,
                                                "Login failed! Try again.",
                                                Toast.LENGTH_LONG).show();
                                        // Sets progressbar invisible
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

    }

}