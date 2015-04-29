package com.example.willy.storyapp2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the functionality of the app
        Button start_button = (Button) findViewById(R.id.Start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startStory();
            }
        });

        // The start screen music, creating mediaplayer and shows path to the audio that should be played
        final MediaPlayer startMusic = MediaPlayer.create(this, R.raw.luna_blanca_sunset);
        startMusic.start(); // Starts the start screen music

        //Creates button for turning start screen music on or off and defines its functionality
        final Button music_button = (Button) findViewById(R.id.music_button); // Creates the music button
        music_button.setTag(1); // Gives the button a tag which later on is used to give the button the functionality to pause and restart the music
        music_button.setText("Off"); // Sets the text that is shown on the button
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
    }

            // Enters the StoryMode activity
            public void startStory() {

                Intent intent = new Intent(this, StoryMode.class);
                startActivity(intent);

            }

        }