package com.example.willy.storyapp2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class AfterPostActivity extends Activity {

    private Button createNewStoryButton;
    private Button storyShowcaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_post);

        //Initialize buttons
        createNewStoryButton = (Button) findViewById(R.id.createNewStoryButton_ID);
        storyShowcaseButton = (Button) findViewById(R.id.storyShowcaseAP_ID);


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

    private void startNewStoryshowcase() {

        Intent intent = new Intent(this, StoryShowcaseActivity.class);
        startActivity(intent);
    }

    private void createNewStory() {

        Intent intent = new Intent(this, StoryModeActivity.class);
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
                Intent intentStory = new Intent(this, StoryModeActivity.class);
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
