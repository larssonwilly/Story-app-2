package com.example.willy.storyapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * The MainActivity is the start screen of the app that shows 3 buttons: log in, sign up, and
 * start storyshowcase and also a picture
 *
 * @author  Willy Larsson
 * @version 1.0?
 * @since   2015-03-15
 */
public class MainActivity extends ActionBarActivity {

    /**
     * TYPE: Can have the value of LOGIN or SIGNUP depending on what button the user presses. This value is sent to AuthenticateActivity
     * LOGIN: This will be given to TYPE if user presses loginButton
     * SIGNUP: This value will be given to TYPE if user presses signupButton
     */
    public static final String TYPE = "type";
    public static final String LOGIN = "Log In";
    public static final String SIGNUP = "Sign Up";

    protected Button loginButton;
    protected Button signupButton;
    protected Button startShowcaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables connection to the database
        Parse.initialize(this, "kis8UPt0FJqm7CTURn5FozaYh1gPbCC6tNtEt0dP", "sNqbugJaRddwJmpA5WBSsJQVFT5JbZddBawtfEI8");

        loginButton = (Button) findViewById(R.id.Login);
        signupButton = (Button) findViewById(R.id.Signup);
        startShowcaseButton = (Button) findViewById(R.id.storyShowcaseButton);

        //if the user is already signed in, go directly to StoryModeActivity
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(this, StoryModeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else { //if the user is not logged in stay in the MainActivity
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                /**
                 *  When the log in button is clicked, start the AuthenticateActivity and send the value of TYPE to this activity
                 *  @param v    the View that was clicked
                 */
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AuthenticateActivity.class);
                    intent.putExtra(TYPE, LOGIN);
                    startActivity(intent);
                }
            });

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                /**
                 *  When the sign in button is clicked, start the AuthenticateActivity and send the value of TYPE to this activity
                 *  @param v    the View that was clicked
                 */
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AuthenticateActivity.class);
                    intent.putExtra(TYPE, SIGNUP);
                    startActivity(intent);
                }
            });

            // Start the Story Showcase activity if the button is pressed
            startShowcaseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                /**
                 *  When the story show case button is clicked, call the startStoryShowcase method
                 *  @param v    the View that was clicked
                 */
                public void onClick(View v){
                    startStoryShowcase();
                }

            });
        }

    }

    /**
     *  Starts the activity Story showcase
     */
    private void startStoryShowcase(){

        Intent intent = new Intent(this, StoryShowcaseActivity.class);
        startActivity(intent);

    }

}