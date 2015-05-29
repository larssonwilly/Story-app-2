package com.example.willy.storyapp2.helpers;

import android.os.AsyncTask;

import com.example.willy.storyapp2.activities.StoryModePresenter;
import com.example.willy.storyapp2.activities.StoryModeView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Quaxi on 15-05-29.
 */
public class StorylistHandler {

    private List<ParseObject> storyList;
    private List<ParseObject> unfinishedStoryList;
    private List<ParseObject> postList;
    private DatabaseConnection dbconn;
    private StoryModePresenter presenter;


    public StorylistHandler(boolean loadStories, StoryModePresenter presenter) {
        this.presenter = presenter;

        dbconn = new DatabaseConnection();
        if (loadStories){
            loadAllInBackground();
        }
    }

    public void putPost(String key, Object value){
        dbconn.putPost(key, value);
    }

    public void addNewPost(String post, String author, String story){

        dbconn.createNewPost();
        dbconn.putPost("storyPart", post);
        dbconn.putPost("author", author);
        dbconn.putPost("inStory", story);
        dbconn.savePost();
        updateNumbering(story, dbconn.getNewPost());
        dbconn.savePost();


    }

    public void addNewStory(String storyName, String user) {

        dbconn.createNewStory();
        dbconn.putStory("creator", user);
        dbconn.putStory("score", 0);
        dbconn.putStory("isCompleted", false);
        dbconn.putStory("storyName", storyName);
        dbconn.putStory("lastUser", user);
        dbconn.saveStory();
        ParseObject returnStory = dbconn.getNewStory();
        presenter.setCurrentStory(returnStory);

    }

    public ParseObject getRandomUnfinishedStory() {

        ParseObject currentStory;

        Random rng = new Random();
        if (unfinishedStoryList.size() > 1) {
            int thisRng = rng.nextInt(unfinishedStoryList.size() - 1);
            currentStory = unfinishedStoryList.get(thisRng);
        }
        else{
            currentStory = unfinishedStoryList.get(0);
        }

        return currentStory;

    }

    public List<ParseObject> getStoryList() {
        return storyList;
    }

    public List<ParseObject> getUnfinishedStoryList() {
        return unfinishedStoryList;
    }

    public List<ParseObject> getPostList() {
        return postList;
    }

    private void filterUnfinishedStories(){

        unfinishedStoryList = new ArrayList<>();

        for (ParseObject story : storyList) {
            if (!story.getBoolean("isCompleted")){
                unfinishedStoryList.add(story);
                System.out.println();
            }
        }
    }

    public void setCurrentStoryComplete(){

    }

    private void updateNumbering(String story, final ParseObject post){

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");

        query.whereEqualTo("inStory", story);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {

                    // Set the story completed if the posts are too many
                    /*if (isLastPoster) {
                        setCurrentStoryComplete();
                    }*/ //TODO make this shit work!

                    // Sets the story's number in the queue
                    if (retrievedList.size() > 0) {
                        post.put("numberInStory", retrievedList.size());
                    }
                    else {
                        post.put("numberInStory", 0);
                    }

                    //Saves
                    try {
                        post.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    e.printStackTrace();

                }
            }
        });

    }

    /**
     * Adds the users post to the full story and publishes it to the database
     */
    public void updateStory(final String inputText, final ParseObject currentStory) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it
        query.getInBackground(currentStory.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyTextServer.put("story", currentStory.getString("story") + inputText);
                    storyTextServer.put("lastUser", ParseUser.getCurrentUser().getUsername());
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    public void setStoryList(List<ParseObject> storyList) {
        this.storyList = storyList;
    }

    public void setUnfinishedStoryList(List<ParseObject> unfinishedStoryList) {
        this.unfinishedStoryList = unfinishedStoryList;
    }

    public void setPostList(List<ParseObject> postList) {
        this.postList = postList;
    }

    private void loadAllInBackground(){
        new DownloadFilesTask().execute();
    }
    
    public void loadAllInForeground(){
        try {
            setStoryList(dbconn.getStories());
            System.out.println();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            setPostList(dbconn.getPosts());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        filterUnfinishedStories();
    }

    public void loadPostsInForeground(){
        try {
            setPostList(dbconn.getPosts());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean allStoriesCompleted(){
        for (ParseObject parseObject : storyList) {
            if (parseObject.getBoolean("isCompleted") == false){
                return false;
            }
        }
        return true;
    }


    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... params) {
            try {
                setStoryList(dbconn.getStories());
                System.out.println();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                setPostList(dbconn.getPosts());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            filterUnfinishedStories();
        }
    }
}



