package com.example.willy.storyapp2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.willy.storyapp2.R;
import com.example.willy.storyapp2.helpers.StoryShowcaseAdapter;
import com.example.willy.storyapp2.helpers.StorylistHandler;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URL;

/**
 * Handles all actions related to the Story Showcase mode.
 */
public class StoryShowcaseActivity extends Activity {


    ParseUser currentUser;
    StorylistHandler lh;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_showcase);

        lh = new StorylistHandler();

        //Sets the user name as the action bar text
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());
        progressBar = (ProgressBar) findViewById(R.id.showCaseProgress);

        //Loads all the necessary data
        new LoadStoriesTask().execute();



    }

    private void loadData(){
        lh.loadFinishedStories();
        lh.loadPostsFromAuthor(ParseUser.getCurrentUser().getUsername());
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

    private class LoadStoriesTask extends AsyncTask<URL, Integer, Long> {

        protected Long doInBackground(URL... urls) {
            loadData();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        protected void onPostExecute(Long result) {

            //Creates adapter
            ListAdapter storyAdapter = new StoryShowcaseAdapter(StoryShowcaseActivity.this, lh.getFinishedStories(), lh.getPostList());
            ListView storyListView = (ListView) findViewById(R.id.storyListView);
            storyListView.setAdapter(storyAdapter);

            //Sets the progress bar to invisible
            progressBar.setVisibility(View.INVISIBLE);



        }

    }



}

