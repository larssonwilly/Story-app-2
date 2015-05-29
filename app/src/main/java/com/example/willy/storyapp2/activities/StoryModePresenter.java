package com.example.willy.storyapp2.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.willy.storyapp2.R;
import com.example.willy.storyapp2.helpers.StorylistHandler;
import com.example.willy.storyapp2.helpers.TextHelper;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.net.URL;

/**
 * Presnter that presents data from the StorylistHandler to the view.
 * Handles all logic related to creating and continuing stories and posts.
 */
public class StoryModePresenter implements View.OnClickListener, View.OnTouchListener, TextWatcher {

    //References
    private StoryModeViewActivity view;
    private StorylistHandler lh;
    private ParseObject currentStory;

    //Final variables
    public static final int MAX_LENGTH_VISIBLE = 40;
    public static final int MIN_POST_LENGTH = 15;

    //State variables
    private boolean isCreatingNewStory;
    private boolean isLastPoster;

    /**
     *
     * @param view The view that the presenter should present data to
     */
    public StoryModePresenter(StoryModeViewActivity view) {
        this.view = view;
        startStoryMode();
    }

    /**
     * Initializing the game
     */
    private void startStoryMode() {
        lh = new StorylistHandler(this);
        view.setProgressBarVisible(View.VISIBLE);
        new LoadStoryTask().execute();

    }

    /**
     * This class is activated if the user continues the story instead of creating a new
     */
    private void continueStory() {
        currentStory = lh.getRandomAvailableStory();
        lh.loadPostsFromStory(currentStory.getObjectId());

        // User is the last poster
        if (lh.checkIfLastPoster()) {
            view.makeToast("You're the last poster, finish up the story!", Toast.LENGTH_LONG);
            isLastPoster = true;
        }
        displayStory();

    }

    /**
     *
     * @param currentStory the current active the.
     */
    public void setCurrentStory(ParseObject currentStory) {
        this.currentStory = currentStory;
    }


    /**
     *  Starts the activity "startAfterPostActivity"
     */
    private void startAfterPostActivity() {
        Intent intent = new Intent(view, AfterPostActivity.class);
        view.startActivity(intent);
    }

    /**
     * Starts the Easter Egg activity
     */
    public void startEasterEgg() {
        Intent intentEEA = new Intent(view, EasterEggActivity.class);
        view.startActivity(intentEEA);
    }

    /**
     * Displays the story text and name
     */
    private void displayStory() {

        //Fetching the story name and content
        view.setStoryName(currentStory.getString("storyName"));
        StringBuilder storyText = new StringBuilder();
        storyText.append(currentStory.getString("story"));

        //Using the texthelper to make sure the story text doesn't start with half-finished words.
        TextHelper th = new TextHelper();
        view.setStoryText(th.trimStory(storyText, MAX_LENGTH_VISIBLE));
    }

    /**
     * Publishes the story to the database
     * @param inputText the user input to publish
     */
    private void publish(String inputText) {

        //If the user is creating a new story, we must first create the story before we can add the post
        if (isCreatingNewStory) {
            lh.addNewStory(view.getShownStoryName(), ParseUser.getCurrentUser().getUsername());
        }

        lh.addNewPost(inputText, ParseUser.getCurrentUser().getUsername(), currentStory.getObjectId(), isLastPoster);
        lh.addPostToStory(inputText, currentStory);

    }

    /**
     * Creates the dialog which prompts the user for a story name
     */
    private void createNewStoryDialog() {
        view.createNewStoryDialog();
    }

    /**
     * Updates the hint text and send button if the post is ready to send
     */
    private void updateSendAvailability() {

        boolean isReady = view.getInputTextLength() >= MIN_POST_LENGTH;

        if (!isReady) {
            view.setSendButtonVisibility(View.INVISIBLE);
            int lengthLeft = MIN_POST_LENGTH - view.getInputTextLength();
            view.setMinLengthHintText("You need " + lengthLeft + " more characters");
        } else {
            view.setMinLengthHintText("");
            view.setSendButtonVisibility(View.VISIBLE);
        }
        view.setSendButtonEnabled(isReady);

    }


    @Override
    public void onClick(View v) {


        // Logic for what happens when the user presses send
        if (v.getId() == R.id.sendStoryButton) {
            String inputText = view.getInputText();

            //Easter egg logic
            if (inputText.contains("fest") && inputText.contains("Hammaro")) {
                startEasterEgg();


            } else {

                //Executes the send command in the background while showing a progress bar
                view.setProgressBarVisible(View.VISIBLE);
                new SendTask(view.getInputText()).execute();
            }

        } else if (v.getId() == R.id.dialogSendButton) {

            //Stores the story name localy until the story is published
            view.setStoryName(view.getDialogStoryName());
            view.dismissDialog();

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.storyEditText) {

            // Renders the text white when its marked
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setEditHintTextColor(Color.WHITE);
            }

            // Renders the text black when the user no longer presses the cursor onto the textfield
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setEditHintTextColor(Color.BLACK);
            }
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        updateSendAvailability();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateSendAvailability();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * Loads the story in the background while displaying a progress bar
     */
    private class LoadStoryTask extends AsyncTask<URL, Integer, Long> {

        boolean isAvailable = false;


        protected Long doInBackground(URL... urls) {
            lh.loadStories();
            isAvailable = lh.filterAvailableStories();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Long result) {

            view.setProgressBarVisible(View.INVISIBLE);


            // User finds no available stories and thus creates a new one
            if (!isAvailable) {
                createNewStoryDialog();
                view.makeToast("No available stories - creating new", Toast.LENGTH_SHORT);
                isCreatingNewStory = true;
            } else {

                // User continues a story
                continueStory();
                isCreatingNewStory = false;

            }

        }

    }

    /**
     * Sends the story in the background while displaying a progress bar
     */
    private class SendTask extends AsyncTask<URL, Integer, Long> {

        String inputText;

        public SendTask(String inputText) {
            this.inputText = inputText;
        }

        protected Long doInBackground(URL... urls) {
            publish(inputText);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        protected void onPostExecute(Long result) {
            view.setProgressBarVisible(View.INVISIBLE);
            startAfterPostActivity();

        }

    }

}





