package com.example.willy.storyapp2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StoryMode extends ActionBarActivity {

    //The elements of the activity
    private EditText mEditStoryField;
    private Button mButton;
    private TextView mEndOfStory;
    //Stringbuilder is a tool for handling strings, we use it for the append method
    private StringBuilder storyText = new StringBuilder("");

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
                setStoryView(storyText); //set the "story view", which is the text field containing the last xx characters of the story
                clearText(); //clears the text for new input
            }
        });

    }

    public void updateStory(String inputText)   {
        storyText.append(inputText + " ");
    }

    public void setStoryView(StringBuilder storyText)  {
        String lastWords = storyText.substring(Math.max(0, storyText.length() - 60));
        mEndOfStory.setText(lastWords);
    }

    public void clearText() {
        mEditStoryField.setText("");
    }

}
