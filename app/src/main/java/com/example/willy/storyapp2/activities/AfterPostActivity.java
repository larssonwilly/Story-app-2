package com.example.willy.storyapp2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.willy.storyapp2.R;
import com.parse.ParseUser;


/**
 * Displays two buttons, allowing the user to choose between continuing storytelling, or
 * viewing the finished stories.
 */
public class AfterPostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_post);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());


        //Initialize buttons
        Button createNewStoryButton = (Button) findViewById(R.id.createNewStoryButton_ID);
        Button storyShowcaseButton = (Button) findViewById(R.id.storyShowcaseAP_ID);


        createNewStoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNewStory();
            }
        });
        storyShowcaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewStoryshowcase();
            }
        });


    }

    /**
     * Starts the StoryShowcaseActivity
     */
    private void startNewStoryshowcase() {

        Intent intent = new Intent(this, StoryShowcaseActivity.class);
        startActivity(intent);
    }

    /**
     * Starts a new story
     */
    private void createNewStory() {

        Intent intent = new Intent(this, StoryModeViewActivity.class);
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.view_stories:
                Intent intentView = new Intent(this, StoryShowcaseActivity.class);
                startActivity(intentView);
                return true;

            case R.id.create_story:
                Intent intentStory = new Intent(this, StoryModeViewActivity.class);
                startActivity(intentStory);
                return true;

            case R.id.logoutButton:
            /*
             * Log current user out using ParseUser.logOut()
			 */
                ParseUser.logOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
