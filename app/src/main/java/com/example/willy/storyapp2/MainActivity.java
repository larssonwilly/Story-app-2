package com.example.willy.storyapp2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import com.parse.Parse;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {

    /** Publika skall kommenteras */
    public static final String TYPE = "type";
    public static final String LOGIN = "Log In";
    public static final String SIGNUP = "Sign Up";

    protected Button mLoginButton;
    protected Button mSignupButton;
    protected Button start_button;
    protected Button music_button;

    public MediaPlayer startMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "kis8UPt0FJqm7CTURn5FozaYh1gPbCC6tNtEt0dP", "sNqbugJaRddwJmpA5WBSsJQVFT5JbZddBawtfEI8");

        mLoginButton = (Button) findViewById(R.id.Login);
        mSignupButton = (Button) findViewById(R.id.Signup);

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(this, StoryMode.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AuthenticateActivity.class);
                    intent.putExtra(TYPE, LOGIN);
                    startActivity(intent);
                }
            });

            mSignupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AuthenticateActivity.class);
                    intent.putExtra(TYPE, SIGNUP);
                    startActivity(intent);
                }
            });
        }

        // Start the functionality of the app
        start_button = (Button) findViewById(R.id.Start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startStory();
            }
        });

        // The start screen music
        startMusic =  MediaPlayer.create(this, R.raw.sunaslancasunset);
        startMusic.setLooping(true);
        startMusic.start();

        // ToDo: knappen funkar inte ordentligt om man loggar ut och in
        //Creates button for turning start screen music on or off and defines its functionality
        music_button = (Button) findViewById(R.id.music_button); // Creates the music button
        music_button.setTag(1); // Gives the button a tag which later on is used to give the button the functionality to pause and restart the music
        music_button.setText("Off"); // Sets the text that is shown on the button
        runningMusic();
    }

    // Enters the StoryMode activity
    public void startStory() {

        Intent intent = new Intent(this, StoryMode.class);
        startActivity(intent);

    }

    public void runningMusic (){

        if(music_button.getTag().equals(1)) {
            music_button.setText("Off");
        }
        else
        {
            music_button.setText("On");
        }
        music_button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                final int status = (Integer) v.getTag(); // Integer used to determine what will happen when the button is pressed
                switch (status) {
                    // Turns the music off and change button text from "Off" to "On"
                    case 1:
                        startMusic.pause(); // Pauses the music
                        music_button.setText("On"); // Change button text to "On"
                        music_button.setTag(0); // Gives button new tag 0
                        break;
                    // Turns the music back on where it was paused and change the button text to "Off"
                    case 0:
                        startMusic.start(); // Restarts the music
                        music_button.setText("Off"); // Change button the text to "Off"
                        music_button.setTag(1); // Gives the button the original tag 1
                        break;
                }
            }
        });

    }}

