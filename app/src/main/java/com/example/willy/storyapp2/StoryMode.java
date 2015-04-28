package com.example.willy.storyapp2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.StringTokenizer;

public class StoryMode extends ActionBarActivity {

    //The elements of the activity
    private EditText mEditStoryField;
    private Button mButton;
    private TextView mEndOfStory;

    //Stringbuilder is a tool for handling strings, we use it for the append method
    private StringBuilder storyText = new StringBuilder("");
    public static int MAX_LENGTH_VISIBLE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        //initiate the elements
        mEditStoryField = (EditText) findViewById(R.id.storyEditText);
        mButton = (Button) findViewById(R.id.sendStoryButton);
        mEndOfStory = (TextView) findViewById(R.id.theStory);

        //when the "send" button is clicked, we need to update the story, display the last bit of the story, clear the text for new input
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mEditStoryField.getText().toString();
                Toast.makeText(StoryMode.this, "Send successful", Toast.LENGTH_LONG).show();
                updateStory(inputText); //updates story
                setStoryView(storyText); //set the "story view", which is the text field containing the last 60 characters of the story, checks if whole word
                clearText(); //clears the text for new input
            }
        });

    }

    public void updateStory(String inputText)   {
        storyText.append(inputText + " ");
    }

    public void setStoryView(StringBuilder storyText)  {
        String lastCharsOfStory = storyText.substring(Math.max(0, storyText.length() - (MAX_LENGTH_VISIBLE + 1))); // we take the last 61 characters of the story

        if(storyText.length() < 60) { //if the story is shorter than the max number of characters we want to show, show it
            mEndOfStory.setText(lastCharsOfStory);
        } else  {
            String lastWordsOfStory = fixOnlyWords(lastCharsOfStory);
            mEndOfStory.setText(lastWordsOfStory);
        }

    }

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