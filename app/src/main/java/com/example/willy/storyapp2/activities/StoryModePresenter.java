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
 * Created by Quaxi on 15-05-28.
 */
public class StoryModePresenter implements View.OnClickListener, View.OnTouchListener, TextWatcher {

    private StoryModeView view;
    private StorylistHandler lh;
    private ParseObject currentStory;

    //Final variables
    public static final int MAX_LENGTH_VISIBLE = 40;
    public static final int MIN_POST_LENGTH = 15;

    private boolean isCreatingNewStory;
    private boolean isLastPoster;

    public StoryModePresenter(StoryModeView view) {
        this.view = view;
        startStoryMode();
    }

    private void startStoryMode() {
        lh = new StorylistHandler(this);
        new LoadStoryTask().execute();
    }

    private void continueStory() {
        currentStory = lh.getRandomAvailableStory();
        lh.loadPostsFromStoryInForeGround(currentStory.getObjectId());
        // User is the last poster
        if (lh.checkIfLastPoster()){
            view.makeToast("You're the last poster, finish up the story!", Toast.LENGTH_LONG);
            isLastPoster = true;
        }
        displayStory();

    }

    public void setCurrentStory(ParseObject currentStory) {
        this.currentStory = currentStory;
    }


    private void startAfterPostActivity(){
        Intent intent = new Intent(view, AfterPostActivity.class);
        view.startActivity(intent);
    }

    public void startEasterEgg(){
        Intent intentEEA = new Intent(view, EasterEggActivity.class);
        view.startActivity(intentEEA);
    }

    private void displayStory(){
        TextHelper th = new TextHelper();

        view.setStoryName(currentStory.getString("storyName"));
        StringBuilder storyText = new StringBuilder();
        storyText.append(currentStory.getString("story"));
        view.setStoryText(th.trimStory(storyText, MAX_LENGTH_VISIBLE));
    }

    private void publish(String inputText, boolean isNewStory){

        if (isCreatingNewStory){
            lh.addNewStory(view.getShownStoryName(), ParseUser.getCurrentUser().getUsername());
        }
        lh.addNewPost(inputText, ParseUser.getCurrentUser().getUsername(), currentStory.getObjectId(), isLastPoster);
        lh.addPostToStory(inputText, currentStory);

    }

    private void createNewStoryDialog() {
        view.createNewStoryDialog();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sendStoryButton){
            String inputText = view.getInputText();

            if (inputText.contains("fest") && inputText.contains("Hammaro")) {
                startEasterEgg();


            } else {
                publish(view.getInputText(), false);
                startAfterPostActivity();
            }
        }

        else if (v.getId() == R.id.dialogSendButton){

            view.setStoryName(view.getDialogStoryName());
            view.dismissDialog();

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.storyEditText){

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


    private void updateSendAvailability() {

        boolean isReady = view.getInputTextLength()>=MIN_POST_LENGTH;

        if (!isReady){
            view.setSendButtonVisibility(View.INVISIBLE);
            int lengthLeft = MIN_POST_LENGTH - view.getInputTextLength();
            view.setMinLengthHintText("You need " + lengthLeft + " more characters");
        }
        else {
            view.setMinLengthHintText("");
            view.setSendButtonVisibility(View.VISIBLE);
        }
        view.setSendButtonEnabled(isReady);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateSendAvailability();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class LoadStoryTask extends AsyncTask<URL, Integer, Long> {


        protected Long doInBackground(URL... urls) {
            lh.loadStoriesInForeGround();
            lh.filterAvailableStories();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Long result) {


            // User finds no available stories and thus creates a new one
            if (lh.getAvailableStoryList().size() == 0) {
                createNewStoryDialog();
                isCreatingNewStory = true;
            }

            else{

                // User continues a story
                continueStory();
                isCreatingNewStory = false;

            }

        }

    }

}





