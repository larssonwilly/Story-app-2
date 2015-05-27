package com.example.willy.storyapp2;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import utils.ColorPalette;

public class StoryModeActivity extends Activity {

    //The elements of the activity
    private EditText mEditStoryField;
    private EditText editStoryName;
    private TextView storyNameView;
    private TextView lengthLabel;

    private Button mButton;
    private TextView mEndOfStory;




    //Stringbuilder is a tool for handling strings, we use it for the append method
    private StringBuilder storyText = new StringBuilder("");
    public static int MAX_LENGTH_VISIBLE = 40;
    public static int MAX_NUM_POSTS_IN_STORY = 10;
    public static int MIN_POST_LENGTH = 15;

    //storyLists
    private List<ParseObject> storyList;
    private List<ParseObject> unfinishedStoryList;


    //story Variables
    private String currentStoryID;
    private boolean creatingNewStory;

    private String storyName;
    private Dialog d;

    private ParseObject currentStory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Standard Oncreate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        //initiate the elements
        mEditStoryField = (EditText) findViewById(R.id.storyEditText);
        mButton = (Button) findViewById(R.id.sendStoryButton);
        mEndOfStory = (TextView) findViewById(R.id.theStory);
        lengthLabel = (TextView) findViewById(R.id.textMinLength_ID);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());


        //gets the story from the database so that it is possible to see what the last person wrote
        new DownloadFilesTask().execute();


        // Indicates for the user that they have marked the textfield
        mEditStoryField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){

                // Renders the text white when its marked
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mEditStoryField.setHintTextColor(Color.WHITE);
                }

                // Renders the text black when the user no longer presses the cursor onto the textfield
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mEditStoryField.setHintTextColor(Color.BLACK);
                }
                return false;
            }

        });

        // Initially the Send-button can't be pressed because there is no text to send
        mButton.setEnabled(false);

        // Adds a TextChangedListener to the the text field mEditStoryField that enables or disables the Send-button using the method updateSendAvailability().
        mEditStoryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                updateSendAvailability();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateSendAvailability();
            }



            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        //when the "send" button is clicked, we need to update the story, display the last bit of the story, clear the text for new input
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String inputText = mEditStoryField.getText().toString(); //the text that the user writes

                if (inputText.contains("fest") && inputText.contains("Hammaro")){
                    startEasterEgg();
                }

                else {

                    pushStory(inputText); // updates story and sends to the database
                    addTextToView(inputText);
                    clearText(); //clears the text for new input

                    //TODO this should really check if the send actually was successful

                    //Starts afterpostactivity
                    startAfterPostActivity();

                }



            }
        });
    }

    public void startEasterEgg(){

        Intent intentEEA = new Intent(this, EasterEggActivity.class);
        startActivity(intentEEA);


    }

    public void startAfterPostActivity(){
        Intent intent = new Intent(this, AfterPostActivity.class);
        startActivity(intent);

    }

    // Enables the mButton if if the length of the text exceeds 15 characters.
    public void updateSendAvailability() {

        boolean isReady = mEditStoryField.getText().toString().length()>=MIN_POST_LENGTH;
        int length = 0;
        length = mEditStoryField.getText().length();

        if (!isReady){
            mButton.setVisibility(View.INVISIBLE);
            int lengthLeft = MIN_POST_LENGTH - length;
            lengthLabel.setText("You need " + lengthLeft + " more characters" );
        }
        else {
            lengthLabel.setText("");
            mButton.setVisibility(View.VISIBLE);

        }
        mButton.setEnabled(isReady);
    }

    // Loads all the stories as objects into storyList and unfinishedStoryList.
    // This should only be loaded in conjunction with an AsyncTask
    public void loadAllStories() {

        unfinishedStoryList = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");


        try {
            storyList = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();

        }

        //Adds all non-completed stories in "unfinishedStoryList"
        for (ParseObject story : storyList) {
            if (!story.getBoolean("isCompleted")){
                unfinishedStoryList.add(story);
            }
        }
    }

    //Changes currentStoryId to a random one
    public void getRandomUnfinishedStory() {

        Random rng = new Random();
        if (unfinishedStoryList.size() > 1) {
            int thisRng = rng.nextInt(unfinishedStoryList.size() - 1);
            currentStory = unfinishedStoryList.get(thisRng);
            currentStoryID = currentStory.getObjectId();
        }
        else{
            currentStory = unfinishedStoryList.get(0);
            currentStoryID = currentStory.getObjectId();
        }



    }

    //Changes currentStoryId to a random one
    public void displayStoryText() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")); //load the story from the database and save it in local variable storyText
                    setStoryView(storyText); //set the "story view", which is the text field containing the last characters of the story, checks if whole word
                    setStoryNameView(storyTextServer.getString("storyName"));


                }
            }

        });




    }

    public void setStoryNameView(String input){

        storyNameView = (TextView) findViewById(R.id.storyNameView);
        storyNameView.setText(input);


    }

    public void updateStory(final String inputText) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it, same problem as with getstory
        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    storyText.append(inputText + " "); //update local variable storyText with input text from user
                    storyTextServer.put("story", storyText.toString());
                    storyTextServer.put("lastUser", ParseUser.getCurrentUser().getUsername());
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    public void pushStory(final String inputText) {

        if (creatingNewStory){
            createNewStory(inputText, storyName);

        }

        //Posts the story to the Writes class at Parse
        final ParseObject newPost = new ParseObject("Writes");
        newPost.put("storyPart", inputText);
        newPost.put("author", ParseUser.getCurrentUser().getUsername());
        newPost.put("inStory", currentStoryID);


        //Queries "Writes" for the current story
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("inStory", currentStoryID);

        //Creates a super awesome query!
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {

                    // Set the story completed if the posts are too many
                    if (retrievedList.size() >= MAX_NUM_POSTS_IN_STORY-1) {
                        setCurrentStoryComplete();
                    }

                    // Sets the story's number in the queue
                    if (retrievedList.size() > 0) {
                        newPost.put("numberInStory", retrievedList.size());
                    } //TODO this sometimes bugs. Fix plz
                    else {
                        newPost.put("numberInStory", 0);
                    }
                    try {
                        newPost.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }//TODO ASyncTask

                } else {
                    e.printStackTrace();

                }
            }
        });

        newPost.saveInBackground();
        updateStory(inputText);

    }

    public void setCurrentStoryComplete(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it, same problem as with getstory
        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyTextServer.put("isCompleted", true);
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    public void createNewStory(String inputText, String storyName) {

        final ParseObject newStory = new ParseObject("Story");
        newStory.put("story", inputText);
        newStory.put("creator", ParseUser.getCurrentUser().getUsername());
        newStory.put("score", 0);
        newStory.put("isCompleted", false);
        newStory.put("storyName", storyName);
        newStory.put("lastUser", ParseUser.getCurrentUser().getUsername());
        try {
            newStory.save(); //TODO this should be done by AsyncTask, not in the main thread! FIX!!!
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentStoryID = newStory.getObjectId();

    }

    public void addTextToView(final String inputText) {

        if (mEndOfStory.getText() == null){
            mEndOfStory.setText(mEndOfStory.getText() + " " + inputText);
        }
        else {
            mEndOfStory.setText(inputText);
        }

    }

    public void setStoryView(StringBuilder theStory) {
        String lastCharsOfStory = theStory.substring(Math.max(0, storyText.length() - (MAX_LENGTH_VISIBLE + 1))); // we take the last 61 characters of the story, if the first character is a space then it will be only words

        if (storyText.length() < MAX_LENGTH_VISIBLE) { //if the story is shorter than the max number of characters we want to show, show entire story
            mEndOfStory.setText(lastCharsOfStory);
        } else { //if the story is longer than MAX_LENGHT_VISIBLE characters, we want to only show the last characters
            String lastWordsOfStory = fixOnlyWords(lastCharsOfStory); // if we "break" a word, we will need to fix the text so its only words, so we call the fixOnlyWords method
            mEndOfStory.setText(lastWordsOfStory);
        }

    }

    /*
        if the first character is a space, we know the lastCharsOfStory is only words, so we return it
        but if the first character is not a space, this means that we will not include the first characters, which is not an entire word
        so we iterate until a space is found, which means a new word is found and then we return the rest
     */

    public String fixOnlyWords(String lastCharsOfStory) {
        if (lastCharsOfStory.charAt(0) != ' ') {
            int iterator = 0;
            while (lastCharsOfStory.charAt(iterator) != ' ') {
                iterator++;
            }
            return lastCharsOfStory.substring(iterator + 1, lastCharsOfStory.length());
        } else {
            return lastCharsOfStory;
        }
    }

    public void clearText() {
        mEditStoryField.setText("");
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


    private void prepareNewStory(){


        d = new Dialog(StoryModeActivity.this);

        //Send button
        Button send = new Button(StoryModeActivity.this);
        send.setText("Start new story!");
        send.setTextColor(ColorPalette.getBlack());
        send.setBackgroundColor(ColorPalette.getGreen());
        send.setElevation(5);

        //Editstoryname
        editStoryName = new EditText(StoryModeActivity.this);
        editStoryName.setHint("What's the name of your story?");
        editStoryName.setPadding(50, 0, 50, 50);
        editStoryName.setTextColor(ColorPalette.getBlack());

        //Layout stuff
        LinearLayout layout = new LinearLayout(StoryModeActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editStoryName);
        layout.addView(send);

        d.setContentView(layout);
        d.show();


        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                storyName = editStoryName.getText().toString();
                setStoryNameView(storyName);
                d.dismiss();
            }
        });



    }

    //AsyncTask to load all stories
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {


        protected Long doInBackground(URL... urls) {

            loadAllStories();
            if (unfinishedStoryList.size() > 0){
                getRandomUnfinishedStory();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //ProgressBar progressBar = new ProgressBar(this.getClass().);
        }

        protected void onPostExecute(Long result) {


            if (currentStoryID == null){
                Toast.makeText(StoryModeActivity.this, "No unfinished stories - creating new!", Toast.LENGTH_LONG).show();
                creatingNewStory = true;
                prepareNewStory();

            }


            else {

                String currentUser = ParseUser.getCurrentUser().getUsername();
                String lastUser = currentStory.getString("lastUser");


                if (currentStory.getString("lastUser").equals(currentUser)){
                    creatingNewStory = true;
                    Toast.makeText(StoryModeActivity.this,
                            "Can't continue own story - creating new!",
                            Toast.LENGTH_LONG).show();
                    prepareNewStory();
                }

                else {
                    displayStoryText();
                    creatingNewStory = false;

                }


            }

        }
    }


}