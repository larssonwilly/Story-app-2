package com.example.willy.storyapp2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;




class StoryShowcaseAdapter extends ArrayAdapter<ParseObject> {

    TextView storyIdTextView;
    TextView storyContentTextView;
    List<ParseObject> storyList;
    ParseUser currentUser;




    public StoryShowcaseAdapter(Context context, List storyObjects) {
        super(context, R.layout.story_showcase_list_layout, storyObjects);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");

        currentUser = ParseUser.getCurrentUser();



        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedList, com.parse.ParseException e) {

                if (e == null) {
                    storyList = retrievedList;
                } else {
                    e.printStackTrace();

                }
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.story_showcase_list_layout, parent, false);


        final ParseObject parseObject = getItem(position);

        storyIdTextView = (TextView) customView.findViewById(R.id.storyIdTextView);
        storyContentTextView = (TextView) customView.findViewById(R.id.storyContentTextView);

        storyIdTextView.setText(parseObject.getString("storyName"));
        storyContentTextView.setText(parseObject.getString("story"));

        // If the current user is logged in - invert the text of all the stories the user has contributed to
        if (currentUser != null){
            if (storyList != null) {
                for (ParseObject object : storyList) {
                    if (object.getString("inStory").equals(parseObject.getObjectId())) {
                        invertText();
                    }
                }
            }
        }

        return customView;


    }

    public void invertText() {
        storyContentTextView.setBackgroundColor(Color.argb(255, 18, 149, 209));
        storyContentTextView.setTextColor(Color.WHITE);

    }
}
