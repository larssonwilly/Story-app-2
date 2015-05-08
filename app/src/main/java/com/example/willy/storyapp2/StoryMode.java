package com.example.willy.storyapp2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

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

        //gets the story from the database so that it is possible to see what the last person wrote

       getRandomStory();

        //when the "send" button is clicked, we need to update the story, display the last bit of the story, clear the text for new input
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mEditStoryField.getText().toString(); //the text that the user writes
                Toast.makeText(StoryMode.this, "Send successful", Toast.LENGTH_LONG).show();
                updateStory(inputText); // updates story and sends to the database
                clearText(); //clears the text for new input
            }
        });
    }


    //Loads all the stories as objects into storyList
    public void loadAllStories() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        try {
            storyList = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        } //TODO AsyncTask this. This code is ineffective and may slow down application.

    }

    public void getRandomStory() {




        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        loadAllStories();
        Random rng = new Random();
        String storyID = "7QaY0rG71I"; //Default story


        if (storyList != null){

            int thisRng = rng.nextInt(storyList.size()-1);
            randStoryId = storyList.get(thisRng).getObjectId();

        }



        // Retrieve the object by id, at the moment the same story is loaded from the database for all users, but we want it to connect a story for particular users
        query.getInBackground(randStoryId, new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")).toString(); //load the story from the database and save it in local variable storyText
                    setStoryView(storyText); //set the "story view", which is the text field containing the last 60 characters of the story, checks if whole word
                }
            }

        });


    }

    public void getStory()  {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");

        // Retrieve the object by id, at the moment the same story is loaded from the database for all users, but we want it to connect a story for particular users
        query.getInBackground("7QaY0rG71I", new GetCallback<ParseObject>() {
            public void done(ParseObject storyTextServer, com.parse.ParseException e) {
                if (e == null) {
                    storyText.append(storyTextServer.getString("story")).toString(); //load the story from the database and save it in local variable storyText
                    setStoryView(storyText); //set the "story view", which is the text field containing the last 60 characters of the story, checks if whole word
                }
            }

        });

    }

    public void updateStory(final String inputText)  {

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

    public void setStoryView(StringBuilder theStory)  {
        String lastCharsOfStory = theStory.substring(Math.max(0, storyText.length() - (MAX_LENGTH_VISIBLE + 1))); // we take the last 61 characters of the story, if the first character is a space then it will be only words

        if(storyText.length() < 60) { //if the story is shorter than the max number of characters we want to show, show entire story
            mEndOfStory.setText(lastCharsOfStory);
        } else  { //if the story is longer than 60 characters, we want to only show the last 60 characters
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
        if(lastCharsOfStory.charAt(0) != ' ')   {
            int iterator = 0;
            while(lastCharsOfStory.charAt(iterator) != ' ')    {
                iterator++;
            }   return lastCharsOfStory.substring(iterator + 1, lastCharsOfStory.length());
        }   else    {
            return lastCharsOfStory;
        }
    }

    public void clearText() {
        mEditStoryField.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

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