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

        // The start screen music
        final MediaPlayer startMusic = MediaPlayer.create(this, R.raw.luna_blanca_sunset);
        startMusic.start();
    }

    // Enters the StoryMode activity
    public void startStory() {

        Intent intent = new Intent(this, StoryMode.class);
        startActivity(intent);

    }

}