package com.example.willy.storyapp2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StoryShowcaseActivity extends Activity {

    private List<ParseObject> storyList = new ArrayList<ParseObject>();
    ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_showcase);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());




        loadAllStories();



        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");

        currentUser = ParseUser.getCurrentUser();

        if (currentUser != null){

            //queries all the users stories so we can check them later against existing stories.
            query.whereEqualTo("author", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                    if (e == null) {
                        System.out.println();
                        ListView storyListView = (ListView) findViewById(R.id.storyListView);
                        ListAdapter storyAdapter = new StoryShowcaseAdapter(StoryShowcaseActivity.this, storyList, retrievedList);
                        storyListView.setAdapter(storyAdapter);


                    } else {
                        e.printStackTrace();

                    }
                }
            });
        }




    }
    //ToDo: javadoc, public method
    public void loadAllStories() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        try {
            query.whereEqualTo("isCompleted", true);
            storyList = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    //ToDo: javadoc, public method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }
    //ToDo: javadoc, public method
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

