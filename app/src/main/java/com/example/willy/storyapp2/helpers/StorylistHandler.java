package com.example.willy.storyapp2.helpers;

import android.os.AsyncTask;

import com.example.willy.storyapp2.activities.StoryModePresenter;
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
 * Handles necessary lists and useful list methods.
 * Connected to the database via a reference to DatabaseConnection.
 * Formats the data for easy use and access by the presenter.
 */
public class StorylistHandler {

    //Lists
    private List<ParseObject> storyList;
    private List<ParseObject> availableStoryList;
    private List<ParseObject> finishedStoryList;
    private List<ParseObject> postList;

    //References
    private DatabaseConnection dbconn;
    private StoryModePresenter presenter;

    public static final int MAX_NUM_POSTS_IN_STORY = 10;

    /**
     * Constructor without a presenter, initializes the database
     */
    public StorylistHandler(){
        dbconn = new DatabaseConnection();
    }

    /**
     * Constructor with a presenter
     * @param presenter the connected presenter
     */
    public StorylistHandler(StoryModePresenter presenter) {
        this.presenter = presenter;
        dbconn = new DatabaseConnection();
    }

    /**
     * Adds a new post
     * @param post the content of the post
     * @param author the username of the author
     * @param story which story the post is related to
     * @param isLastPoster wheter the user is the last poster until the story is finished
     */
    public void addNewPost(String post, String author, String story, boolean isLastPoster) {

        dbconn.createNewPost();
        dbconn.putPost("storyPart", post);
        dbconn.putPost("author", author);
        dbconn.putPost("inStory", story);
        dbconn.savePost();
        updateNumbering(story, dbconn.getNewPost(), isLastPoster);
        dbconn.savePost();
        addPostToStory(post, dbconn.getNewPost());


    }

    /**
     * Adds a new story
     * @param storyName the name of the story
     * @param user the user that creates the story
     */
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

    /**
     * @return a random available story
     */
    public ParseObject getRandomAvailableStory() {

        ParseObject currentStory;

        Random rng = new Random();
        if (availableStoryList.size() > 1) {
            int thisRng = rng.nextInt(availableStoryList.size() - 1);
            currentStory = availableStoryList.get(thisRng);
        } else {
            currentStory = availableStoryList.get(0);
        }

        return currentStory;

    }

    public List<ParseObject> getFinishedStories() {
        return finishedStoryList;
    }


    /**
     * @return a list of the posts stored in the current postList
     */
    public List<ParseObject> getPostList() {
        return postList;
    }

    /**
     * Filters stories into available stories
     *
     * @return if the method found any available stories
     */
    public boolean filterAvailableStories() {

        availableStoryList = new ArrayList<>();
        boolean hasAvailable = false;

        for (ParseObject story : storyList) {
            if (!story.getBoolean("isCompleted")) {
                if (!story.getString("lastUser").equals(ParseUser.getCurrentUser().getUsername())){
                    availableStoryList.add(story);
                    hasAvailable = true;
                }
                else {
                    hasAvailable = false;
                }
            }
        }

        return hasAvailable;
    }

    /**
     * Sets the current story as complete
     * @param currentStoryID the ObjectID of the story to set as complete
     */
    public void setCurrentStoryComplete(String currentStoryID) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyTextServer.put("isCompleted", true);
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    /**
     * Updates the numbering of a post
     * @param story the related story for the post
     * @param post the post as a ParseObject
     * @param isLastPoster if the user is the last poster before story finishes
     */
    private void updateNumbering(final String story, final ParseObject post, final boolean isLastPoster) {

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");

        query.whereEqualTo("inStory", story);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {

                    // Set the story completed if the posts are too many
                    if (isLastPoster) {
                        setCurrentStoryComplete(story);
                    }

                    // Sets the story's number in the queue
                    if (retrievedList.size() > 0) {
                        post.put("numberInStory", retrievedList.size());
                    } else {
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
    public void addPostToStory(final String inputText, final ParseObject currentStory) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it
        query.getInBackground(currentStory.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    if (currentStory.getString("story") == null) {
                        storyTextServer.put("story", inputText + " ");
                    } else {
                        storyTextServer.put("story", currentStory.getString("story") + inputText + " ");
                    }
                    storyTextServer.put("lastUser", ParseUser.getCurrentUser().getUsername());
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    /**
     * Sets the current storylist
     * @param storyList a list of stories as ParseObjects
     */
    public void setStoryList(List<ParseObject> storyList) {
        this.storyList = storyList;
    }

    /**
     * Sets the current postList
     * @param postList a list of posts as ParseObjects
     */
    public void setPostList(List<ParseObject> postList) {
        this.postList = postList;
    }
    
    /**
     * Sets the finished story
     * @param finishedStoryList list of finished stories
     */
    public void setFinishedStoryList(List<ParseObject> finishedStoryList) {
        this.finishedStoryList = finishedStoryList;
    }

    /**
     * Loads posts from a specific author
     * @param author the authors username
     */
    public void loadPostsFromAuthor(String author) {
        try {
            setPostList(dbconn.getPostsFromAuthor(author));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads all finished stories into storyList
     */
    public void loadFinishedStories() {
        try {
            setFinishedStoryList(dbconn.getFinishedStories());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads all posts from a specific story
     * @param story ObjectID string of the story
     */
    public void loadPostsFromStory(String story) {
        try {
            setPostList(dbconn.getPostsFromStory(story));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the user was the last poster
     * @return true if the user was the last poster
     */
    public boolean checkIfLastPoster() {
        return postList.size() >= MAX_NUM_POSTS_IN_STORY - 1;
    }

    /**
     * Loads all stories into storyList
     */
    public void loadStories() {
        try {
            setStoryList(dbconn.getStories());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}




