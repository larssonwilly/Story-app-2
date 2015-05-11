package com.example.willy.storyapp2;

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

public class AuthenticateActivity extends ActionBarActivity {

    protected String mAction;
    protected EditText mUserField;
    protected EditText mPasswordField;
    protected Button mButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        mUserField = (EditText) findViewById(R.id.userEdit);
        mPasswordField = (EditText) findViewById(R.id.passwordEdit);
        mButton = (Button) findViewById(R.id.button1);

        // Initializes the  progressbar for loading/waiting for the user of the app
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        // Makes the progressbar invisible (We don't want it to show directly when the app screen shows)
        mProgressBar.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        mAction = bundle.getString(MainActivity.TYPE);
        mButton.setText(mAction);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = mUserField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (mAction.equals(MainActivity.SIGNUP)) {
                    mProgressBar.setVisibility(View.VISIBLE);
					/*
					 * Sign up using ParseUser
					 */
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    // TODO: Förklara vad nedanstående kod-stycket gör
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            // Starts progressbar

                            if (e == null) {
                                // Hooray! Let them use the app now.
                                startActivity(new Intent(
                                        AuthenticateActivity.this,
                                        StoryMode.class));

                            } else {
                                // Sign up didn't succeed. Look at the
                                // ParseException to figure out what went wrong

                                Toast.makeText(AuthenticateActivity.this,
                                        "Sign up failed! Try again.",
                                        Toast.LENGTH_LONG).show();
                                // Sets progressbar invisible
                                mProgressBar.setVisibility(View.INVISIBLE);

                            }

                        }

                    });
                } else {
					/*
					 * Login using ParseUser
					 */


                    ParseUser.logInInBackground(username, password,
                            new LogInCallback() {
                                public void done(ParseUser user,
                                                 ParseException e) {
                                    // Shows progressbar
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    if (user != null) {
                                        // Hooray! The user is logged in.
                                        startActivity(new Intent(
                                                AuthenticateActivity.this,
                                                StoryMode.class));
                                    } else {
                                        // Shows progressbar
                                        mProgressBar.setVisibility(View.VISIBLE);
                                        // Login failed. Look at the
                                        // ParseException to see what happened.
                                        Toast.makeText(
                                                AuthenticateActivity.this,
                                                "Login failed! Try again.",
                                                Toast.LENGTH_LONG).show();
                                        // Sets progressbar invisible
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

    }

}
