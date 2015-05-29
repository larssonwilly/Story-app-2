package com.example.willy.storyapp2.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.willy.storyapp2.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import com.example.willy.storyapp2.utils.ColorPalette;


/**
 * Adapter used by StoryShowCase activity.
 * Lists Stories along with the story name.
 */
public class StoryShowcaseAdapter extends ArrayAdapter<ParseObject> {

    TextView storyIdTextView;
    TextView storyContentTextView;
    List<ParseObject> postList;
    ParseUser currentUser;

    /**
     * Constructor that takes a context and a list for Stories and Posts.
     *
     * @param context Current application context.
     * @param storyObjects List of stories as objects
     * @param postObjects List of posts as objects. Used to check which stories user has contributed to
     */
    public StoryShowcaseAdapter(Context context, List storyObjects, List postObjects) {

        super(context, R.layout.story_showcase_list_layout, storyObjects);

        this.postList = postObjects;
        currentUser = ParseUser.getCurrentUser();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.story_showcase_list_layout, parent, false);
        final ParseObject parseObject = getItem(position);

        // Initializing variables
        storyIdTextView = (TextView) customView.findViewById(R.id.storyIdTextView);
        storyContentTextView = (TextView) customView.findViewById(R.id.storyContentTextView);

        // Setting text for the story name and story content
        storyIdTextView.setText(parseObject.getString("storyName"));
        storyContentTextView.setText(parseObject.getString("story"));

        // If the current user is logged in - use invertText() for all the stories the user has contributed to
        if (currentUser != null){
            if (postList != null) {
                for (ParseObject object : postList) {
                    if (object.getString("inStory").equals(parseObject.getObjectId())) {
                        invertText();
                        System.out.println();
                    }
                }
            }
        }

        return customView;


    }

    /**
     * Changes the background and text color
     */
    private void invertText() {
        storyContentTextView.setBackgroundColor(ColorPalette.getBlue());
        storyContentTextView.setTextColor(Color.WHITE);

    }
}
