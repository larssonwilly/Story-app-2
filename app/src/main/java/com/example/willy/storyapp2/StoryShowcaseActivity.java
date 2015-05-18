package com.example.willy.storyapp2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.willy.storyapp2.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryShowcaseActivity extends Activity {

    private List<ParseObject> storyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_showcase);


        loadAllStories();

        final ArrayList<String> storyListArray = new ArrayList<String>();



        for (ParseObject parseObject : storyList) {

            String id = parseObject.getObjectId();
            String story = parseObject.getString("story");

            storyListArray.add(id + story);
        }



        ListView storyListView = (ListView) findViewById(R.id.storyListView);
        ListAdapter storyAdapter = new StoryShowcaseAdapter(this, storyListArray);
        storyListView.setAdapter(storyAdapter);


      /*  listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                //Removes item. Used for testing purposes.
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);


                            }
                        });
            }

        });*/
    }



    public void loadAllStories() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        try {
            storyList = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        } //TODO AsyncTask this. This code is ineffective and may slow down application.

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        private HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        private Context context;
        

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {

            super(context, textViewResourceId, objects);

            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}

