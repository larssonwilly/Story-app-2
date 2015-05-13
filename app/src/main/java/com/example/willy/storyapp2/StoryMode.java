package com.example.willy.storyapp2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Random;

public class StoryMode extends ActionBarActivity {

    //The elements of the activity
    private EditText mEditStoryField;
    private Button mButton;
    private TextView mEndOfStory;

    //Stringbuilder is a tool for handling strings, we use it for the append method
    private StringBuilder storyText = new StringBuilder("");
    private String theStory = "";
    ParseObject storyTextServer = new ParseObject("Story");
    public static int MAX_LENGTH_VISIBLE = 40;
    public static int MAX_NUM_POSTS_IN_STORY = 10;
    private List<ParseObject> storyList;
    private String randStoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        //initiate the elements
        mEditStoryField = (EditText) findViewById(R.id.storyEditText);
        mButton = (Button) findViewById(R.id.sendStoryButton);
        mEndOfStory = (TextView) findViewById(R.id.theStory);


        /**
         *  See the method down below
         */
        getRandomStory();

        // Indicates for the user that they have marked the textfield
        // ToDo: Could use some improvement, for an example should the hint text continously blink until the user clicks somewhere else on the screen
        mEditStoryField.setOnTouchListener(new View.OnTouchListener() {
            /**
             * On touch
             */
            //ToDo: Should there really be a public boolean inside a protected void? <-- Just asking coz I don't know.
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

        /**
         *when the "send" button is clicked, we need to update the story, display the last bit of the story, clear the text for new input
         */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mEditStoryField.getText().toString(); //the text that the user writes
                Toast.makeText(StoryMode.this, "Send successful", Toast.LENGTH_LONG).show();
                postStory(inputText); // updates story and sends to the database
                addTextToView(inputText);
                clearText(); //clears the text for new input
            }
        });
    }

    /**
     * Loads all the sotries as objexts into storylist
     */
    public void loadAllStories() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        try {
            storyList = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        } //TODO AsyncTask this. This code is ineffective and may slow down application.

    }
    /**
     * Gets the story from the database so that it is possible to see what the last person wrote
     * //ToDo: This method isn't really good since it just gets one random story.
     * */
    public void getRandomStory() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        loadAllStories();
        Random rng = new Random();
        String storyID = "7QaY0rG71I"; //Default story


        if (storyList != null) {
            int thisRng = rng.nextInt(storyList.size() - 1);
            randStoryId = storyList.get(thisRng).getObjectId();
        }


        // Retrieve the object by id, at the moment the same story is loaded from the database for all users, but we want it to connect a story for particular users
        query.getInBackground(randStoryId, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")); //load the story from the database and save it in local variable storyText
                    setStoryView(storyText); //set the "story view", which is the text field containing the last 60 characters of the story, checks if whole word
                }
            }

        });


    }
    /**
    * Get the default story
    */
    public void getDefaultStory() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the object by id, at the moment the same story is loaded from the database for all users, but we want it to connect a story for particular users
        query.getInBackground("7QaY0rG71I", new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")); //load the story from the database and save it in local variable storyText
                    setStoryView(storyText); //set the "story view", which is the text field containing the last 60 characters of the story, checks if whole word
                }
            }

        });

    }
    /**
     * update Story
     */
    public void updateStory(final String inputText) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the story by id and update it, same problem as with getstory
        query.getInBackground(randStoryId, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {

                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    storyText.append(inputText + " "); //update local variable storyText with input text from user
                    storyTextServer.put("story", storyText.toString());
                    storyTextServer.saveInBackground();

                }
            }

        });

    }
    /**
     * Post story
     */
    public void postStory(final String inputText) {

        final ParseObject newPost = new ParseObject("Writes");
        newPost.put("storyPart", inputText);
        newPost.put("author", ParseUser.getCurrentUser().getUsername());
        newPost.put("inStory", randStoryId);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("inStory", randStoryId);

        //Creates a super awesome query!
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {

                    if (retrievedList.size() > MAX_NUM_POSTS_IN_STORY) {
                        Log.d("TooLong", "NÄ NU BLEV DET FAN FÖR MYCKET, LUGNA NER DIG!!!!");
                        //TODO Insert logic for too long story!

                    }

                    int maxNumber = 0;

                    for (ParseObject object : retrievedList) {
                        int numberInStory = object.getInt("numberInStory");

                        if (maxNumber <= numberInStory) {
                            maxNumber = numberInStory;
                            maxNumber++;
                        }

                        newPost.put("numberInStory", maxNumber);
                        newPost.saveInBackground();


                    }//TODO make this solution more elegant.

                } else {

                }
            }
        });


        newPost.saveInBackground();
        updateStory(inputText);

    }
    /**
     * Add the text to View
     */
    public void addTextToView(final String inputText) {

        mEndOfStory.setText(mEndOfStory.getText() + " " + inputText);



    }
    /**
     * Set story View
     */
    public void setStoryView(StringBuilder theStory) {
        String lastCharsOfStory = theStory.substring(Math.max(0, storyText.length() - (MAX_LENGTH_VISIBLE + 1))); // we take the last 61 characters of the story, if the first character is a space then it will be only words

        if (storyText.length() < MAX_LENGTH_VISIBLE) { //if the story is shorter than the max number of characters we want to show, show entire story
            mEndOfStory.setText(lastCharsOfStory);
        } else { //if the story is longer than 60 characters, we want to only show the last 60 characters
            String lastWordsOfStory = fixOnlyWords(lastCharsOfStory); // if we "break" a word, we will need to fix the text so its only words, so we call the fixOnlyWords method
            mEndOfStory.setText(lastWordsOfStory);
        }

    }

    /*
       * If the first character is a space, we know the lastCharsOfStory is only words, so we return it
       *but if the first character is not a space, this means that we will not include the first characters, which is not an entire word
        *so we iterate until a space is found, which means a new word is found and then we return the rest
        *This also makes sure that if someone hit the send button with an unfinished last word this is shown to the next writer so he/she can continue and make the story right
     */

    /**
     * The method fixes so the text has only words
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
    /**
     * Clear text
     */
    public void clearText() {
        mEditStoryField.setText("");
    }

    /**
     *  The method checks if the story is finished.
     *  The method is used .... , ... , ... and ...
     *  */
    public void setStoryFinished (List<ParseObject> storyList) {

    }

    /**
     * on Create Options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * on Options Item Selected Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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