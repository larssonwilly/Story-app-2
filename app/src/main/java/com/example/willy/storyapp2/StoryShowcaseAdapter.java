package com.example.willy.storyapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;




class StoryShowcaseAdapter extends ArrayAdapter<String> {

    public StoryShowcaseAdapter(Context context, List stories) {
        super(context, R.layout.story_showcase_list_layout, stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.story_showcase_list_layout, parent, false);

        String storyIdAndContent = getItem(position);
        String singleStoryId = storyIdAndContent.substring(0, 9);
        String singleStory = storyIdAndContent.substring(9);

        TextView storyIdTextView = (TextView) customView.findViewById(R.id.storyIdTextView);

        TextView storyContentTextView = (TextView) customView.findViewById(R.id.storyContentTextView);
        ImageView storyTellerLogoView = (ImageView) customView.findViewById(R.id.storyTellerLogoView);

        storyIdTextView.setText(singleStoryId);
        storyContentTextView.setText(singleStory);
        storyTellerLogoView.setImageResource(R.drawable.storyteller_logo_alpha);


        return customView;


    }
}
