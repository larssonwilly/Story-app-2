package com.example.willy.storyapp2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StoryMode extends ActionBarActivity {

    private EditText mEditStoryField;
    private Button mButton;
    private TextView mStory;
    private StringBuilder storyText = new StringBuilder("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        mEditStoryField = (EditText) findViewById(R.id.storyEditText);
        mButton = (Button) findViewById(R.id.sendStoryButton);
        mStory  = (TextView) findViewById(R.id.theStory);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mEditStoryField.getText().toString();
                Toast.makeText(StoryMode.this, "Send successful", Toast.LENGTH_LONG).show();
                updateStory(inputText);
            }
        });

    }

    public void updateStory(String inputText)   {
        storyText.append(inputText + " ");
        setStoryView(storyText);
    }

    public void setStoryView(StringBuilder storyText)  {
        String lastWords = storyText.substring(Math.max(0, storyText.length() - 45));
        mStory.setText(lastWords);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


}
