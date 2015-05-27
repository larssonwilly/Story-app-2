package com.example.willy.storyapp2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
    private EditText editStoryField;
    private EditText editStoryName;
    private TextView storyNameView;
    private TextView lengthLabel;
    private Button sendButton;
    private TextView endOfStoryView;
    private Dialog d;

    //Stringbuilder is a tool for handling strings, we use it for the append method
    private StringBuilder storyText = new StringBuilder("");

    //Final variables ToDo: Ska inte public vara enligt javadoc?
    public static final int MAX_LENGTH_VISIBLE = 40;
    public static final int MAX_NUM_POSTS_IN_STORY = 10; //Since it starts from 0, it will be one more.
    public static final int MIN_POST_LENGTH = 15;

    //Lists
    private List<ParseObject> storyList;
    private List<ParseObject> unfinishedStoryList;
    private List<ParseObject> writesList;

    //Story Variables
    private ParseObject currentStory;
    private String currentStoryID;
    private String storyName;


    //State variables
    private boolean isCreatingNewStory;
    private boolean isLastPoster;


    /**
     * Handles logic setting up the Story Mode for the user.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        //Initiating elements
        editStoryField = (EditText) findViewById(R.id.storyEditText);
        sendButton = (Button) findViewById(R.id.sendStoryButton);
        endOfStoryView = (TextView) findViewById(R.id.theStory);
        lengthLabel = (TextView) findViewById(R.id.textMinLength_ID);


        //Sets actionbar title to current users name.
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());


        //Downloads lists with stories and posts on background thread
        new DownloadStoriesTask().execute();


        // Listener for the textEdit field
        editStoryField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                    // Renders the text white when its marked
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        editStoryField.setHintTextColor(Color.WHITE);
                    }

                    // Renders the text black when the user no longer presses the cursor onto the textfield
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        editStoryField.setHintTextColor(Color.BLACK);
                    }
                    return false;

            }

        });

        // Initially the Send-button can't be pressed because there is no text to send
        sendButton.setEnabled(false);

        // Adds a TextChangedListener to the the text field editStoryField that enables or disables the Send-button using the method updateSendAvailability().
        editStoryField.addTextChangedListener(new TextWatcher() {
            @Override // ToDo: Återigen osäker, skall inte public kommenterat enligt javadoc?
            public void afterTextChanged(Editable arg0) {
                updateSendAvailability();

            }

            @Override // ToDo: Återigen osäker, skall inte public kommenterat enligt javadoc?
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateSendAvailability();
            }


            @Override // ToDo: Återigen osäker, skall inte public kommenterat enligt javadoc?
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        //Listener for when sendButton is pressed
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String inputText = editStoryField.getText().toString(); //the text that the user writes

                if (inputText.contains("fest") && inputText.contains("Hammaro")) {
                    startEasterEgg();
                } else {

                    pushStory(inputText); // updates story and sends to the database
                    addTextToView(inputText);
                    clearText(); //clears the text for new input

                    //Starts afterpostactivity //ToDo: lite överflödigt kanske?
                    startAfterPostActivity();

                }


            }
        });
    }

    /**
     * Starts the easter egg
     */
    public void startEasterEgg(){

        Intent intentEEA = new Intent(this, EasterEggActivity.class);
        startActivity(intentEEA);


    }

    /**
     * Starts AfterPostActivity // ToDo: Kanske kan lägga till en beskrivning av vad metoden gör?
     */
    public void startAfterPostActivity(){
        Intent intent = new Intent(this, AfterPostActivity.class);
        startActivity(intent);

    }

    /**
     * Updates the text and button to give the user information if the send is available
     */
    public void updateSendAvailability() {

        boolean isReady = editStoryField.getText().toString().length()>=MIN_POST_LENGTH;
        int length = 0;
        length = editStoryField.getText().length();

        if (!isReady){
            sendButton.setVisibility(View.INVISIBLE);
            int lengthLeft = MIN_POST_LENGTH - length;
            lengthLabel.setText("You need " + lengthLeft + " more characters" );
        }
        else {
            lengthLabel.setText("");
            sendButton.setVisibility(View.VISIBLE);

        }
        sendButton.setEnabled(isReady);
    }

    /**
     * Loads all the stories as objects into storyList and unfinishedStoryList.
     */
    public void loadAllStories() {


        unfinishedStoryList = new ArrayList<>();

        //Queries the database
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

    /**
     * Loads a random unfinished story into currentStory and currentStoryID
     */
    private void getRandomUnfinishedStory() {

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

    /**
     * Retrieves the story text and publishes it.
     */
    private void displayStoryText() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")); //load the story from the database and save it in local variable storyText
                    setStoryContentView(storyText); //set the "story view", which is the text field containing the last characters of the story, checks if whole word
                    setStoryNameView(storyTextServer.getString("storyName"));


                }
            }

        });




    }

    /**
     * Sets and displays the story name based upon input ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private void setStoryNameView(String input){

        storyNameView = (TextView) findViewById(R.id.storyNameView);
        storyNameView.setText(input);

    }

    /**
     * Displays the input in the StoryText view. ToDo: tror ej private metoder kommenteras enligt javadoc
     * Uses fixOnlyWords to trim the words if needed.
     */
    private void setStoryContentView(StringBuilder theStory) {
        String lastCharsOfStory = theStory.substring(Math.max(0, storyText.length() - (MAX_LENGTH_VISIBLE + 1)));

        if (storyText.length() < MAX_LENGTH_VISIBLE) {
            endOfStoryView.setText(lastCharsOfStory);
        }

        else {
            String lastWordsOfStory = fixOnlyWords(lastCharsOfStory);
            endOfStoryView.setText(lastWordsOfStory);
        }

    }

    /**
     * Adds the users post to the full story and publishes it to the database ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private void updateStory(final String inputText) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it
        query.getInBackground(currentStoryID, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(inputText + " ");
                    storyTextServer.put("story", storyText.toString());
                    storyTextServer.put("lastUser", ParseUser.getCurrentUser().getUsername());
                    storyTextServer.saveInBackground();

                }
            }

        });

    }

    /**
     * Handles logic for pushing the story to the database ToDo: tror ej private metoder kommenteras enligt javadoc
     *
     */
    private void pushStory(final String inputText) {

        if (isCreatingNewStory){
            createNewStory(inputText, storyName);
        }

        //Posts the story to the Writes class at Parse
        final ParseObject newPost = new ParseObject("Writes");
        newPost.put("storyPart", inputText);
        newPost.put("author", ParseUser.getCurrentUser().getUsername());
        newPost.put("inStory", currentStoryID);

        //Saves the story to the database
        try {
            newPost.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //Queries "Writes" for the current story
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("inStory", currentStoryID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {

                    // Set the story completed if the posts are too many
                    if (isLastPoster) {
                        setCurrentStoryComplete();
                    }

                    // Sets the story's number in the queue
                    if (retrievedList.size() > 0) {
                        newPost.put("numberInStory", retrievedList.size());
                    }
                    else {
                        newPost.put("numberInStory", 0);
                    }

                    //Saves
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

    /**
     * Sets the current active story as "Complete" in the database. ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private void setCurrentStoryComplete(){

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

    /**
     * Adds input to the story text display. ToDo: tror ej private metoder kommenteras enligt javadoc
     * Does not remove previous content.
     */
    private void addTextToView(final String inputText) {

        if (endOfStoryView.getText() == null){
            endOfStoryView.setText(endOfStoryView.getText() + " " + inputText);
        }
        else {
            endOfStoryView.setText(inputText);
        }

    }

    /**
     * Removes the first characters of the string if this is not a word and returns the string ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private String fixOnlyWords(String lastCharsOfStory) {
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


    /**
     * Empties the edit field ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private void clearText() {
        editStoryField.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    /**
     * Displays dialog for creating a new story. ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private void displayCreateNewStoryDialog(){


        d = new Dialog(StoryModeActivity.this);

        //Send button
        Button send = new Button(StoryModeActivity.this);
        send.setText("Start!");
        send.setTextColor(ColorPalette.getBlack());
        send.setBackgroundColor(ColorPalette.getGreen());
        send.setElevation(5);

        //Editstoryname
        editStoryName = new EditText(StoryModeActivity.this);
        editStoryName.setHint("Name your story");
        editStoryName.setPadding(80, 0, 50, 80);
        editStoryName.setTextColor(ColorPalette.getBlack());

        //Layout stuff
        LinearLayout layout = new LinearLayout(StoryModeActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editStoryName);
        layout.addView(send);

        //Dialog
        d.setContentView(layout);
        d.setTitle("Create new Story");
        d.show();

        //Listener for the dialogs send button
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                storyName = editStoryName.getText().toString();
                setStoryNameView(storyName);
                d.dismiss();
            }
        });



    }

    /**
     * Creates necessary database variables for the new story. ToDo: tror ej private metoder kommenteras enligt javadoc
     * Adds the new story to the current story. ToDo: Metoden har inparametrar, borde inte dessa också tas med i kommentarerna?
     */
    private void createNewStory(String inputText, String storyName) {

        final ParseObject newStory = new ParseObject("Story");
        newStory.put("story", inputText);
        newStory.put("creator", ParseUser.getCurrentUser().getUsername());
        newStory.put("score", 0);
        newStory.put("isCompleted", false);
        newStory.put("storyName", storyName);
        newStory.put("lastUser", ParseUser.getCurrentUser().getUsername());
        try {
            newStory.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentStory = newStory;
        currentStoryID = newStory.getObjectId();

    }

    @Override // ToDo: Denna behöver kanske kommenteras?
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


    /**
     * Class that handles downloading stories in the background, to avoid clogging UI thread. ToDo: tror ej private metoder kommenteras enligt javadoc
     */
    private class DownloadStoriesTask extends AsyncTask<URL, Integer, Long> {


        /**
         * Loads stories and posts into field variables.
         */
        protected Long doInBackground(URL... urls) {

            loadAllStories();
            if (unfinishedStoryList.size() > 0){
                getRandomUnfinishedStory();
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
            query.whereEqualTo("inStory", currentStoryID);

            try {
                writesList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //ProgressBar progressBar = new ProgressBar(this.getClass().);
        }
        // ToDo: Saknas kommentar
        protected void onPostExecute(Long result) {

            if (currentStoryID == null){
                Toast.makeText(StoryModeActivity.this, "No unfinished stories - creating new!", Toast.LENGTH_LONG).show();
                isCreatingNewStory = true;
                displayCreateNewStoryDialog();

            }

            else {


                String currentUser = ParseUser.getCurrentUser().getUsername();
                String lastUser = currentStory.getString("lastUser");


                if (lastUser.equals(currentUser)){
                    isCreatingNewStory = true;
                    Toast.makeText(StoryModeActivity.this,
                            "Can't continue own story - creating new!",
                            Toast.LENGTH_LONG).show();
                    displayCreateNewStoryDialog();
                }

                else {

                    if (writesList.size() >= MAX_NUM_POSTS_IN_STORY-1){
                        Toast.makeText(StoryModeActivity.this,
                                "You're the last poster! Finish up the story!",
                                Toast.LENGTH_LONG).show();
                        isLastPoster = true;
                    };

                    displayStoryText();
                    isCreatingNewStory = false;

                }


            }

        }
    }


}