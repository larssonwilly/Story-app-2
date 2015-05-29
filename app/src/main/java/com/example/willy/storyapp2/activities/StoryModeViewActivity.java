package com.example.willy.storyapp2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.willy.storyapp2.R;
import com.example.willy.storyapp2.utils.ColorPalette;
import com.parse.ParseUser;


public class StoryModeViewActivity extends Activity {


    private EditText editStoryField;
    private EditText editStoryName;

    private TextView storyNameView;
    private TextView endOfStoryView;

    private TextView minLengthHintText;
    private ProgressBar progressBar;

    private Button sendButton;
    private Dialog d;
    private StoryModePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_mode);

        //Initializing elements
        endOfStoryView = (TextView) findViewById(R.id.theStory);
        minLengthHintText = (TextView) findViewById(R.id.textMinLength_ID);
        storyNameView = (TextView) findViewById(R.id.storyNameView);
        editStoryField = (EditText) findViewById(R.id.storyEditText);
        sendButton = (Button) findViewById(R.id.sendStoryButton);
        sendButton.setEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.sendProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Referencing presenter
        presenter = new StoryModePresenter(this);

        //Setting listeners
        sendButton.setOnClickListener(presenter);
        editStoryField.setOnTouchListener(presenter);
        editStoryField.addTextChangedListener(presenter);

        //Sets actionbar title to current users name.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(ParseUser.getCurrentUser().getUsername());
        }

        //Referencing the presenter


    }

    public void createNewStoryDialog() {

        d = new Dialog(this);

        //Send button
        Button send = new Button(this);
        send.setText("Start!");
        send.setId(R.id.dialogSendButton);
        send.setTextColor(ColorPalette.getBlack());
        send.setBackgroundColor(ColorPalette.getGreen());
        send.setElevation(5);
        send.setOnClickListener(presenter);

        //Editstoryname
        editStoryName = new EditText(this);
        editStoryName.setHint("Name your story");
        editStoryName.setPadding(80, 0, 50, 80);
        editStoryName.setTextColor(ColorPalette.getBlack());

        //Layout stuff
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editStoryName);
        layout.addView(send);

        //Dialog
        d.setContentView(layout);
        d.setTitle("Create new Story");
        d.show();
    }

    public String getDialogStoryName() {
        return editStoryName.getText().toString();

    }

    public String getShownStoryName() {
        return storyNameView.getText().toString();
    }

    public void dismissDialog() {
        d.dismiss();
    }

    public void setStoryName(String storyName) {
        storyNameView.setText(storyName);
    }

    public void setSendButtonVisibility(int visibility) {
        sendButton.setVisibility(visibility);
    }

    public String getInputText() {
        return editStoryField.getText().toString();
    }

    public void setStoryText(String storyText) {
        endOfStoryView.setText(storyText);
    }

    public void setMinLengthHintText(String hintText) {
        minLengthHintText.setText(hintText);
    }

    public void makeToast(String toastText, int length) {
        Toast.makeText(this, toastText, length).show();
    }

    public void setEditHintTextColor(int color) {
        editStoryField.setHintTextColor(color);
    }

    public void setSendButtonEnabled(boolean isEnabled) {
        sendButton.setEnabled(isEnabled);
    }

    public int getInputTextLength() {
        return editStoryField.getText().length();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    public void setProgressBarVisible(int visibility) {
        progressBar.setVisibility(visibility);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.view_stories:
                Intent intentView = new Intent(this, StoryShowcaseActivity.class);
                startActivity(intentView);
                return true;

            case R.id.create_story:
                Intent intentStory = new Intent(this, StoryModeViewActivity.class);
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


}
