package com.example.willy.storyapp2.helpers;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.net.URL;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Quaxi on 15-05-28.
 */
public class DatabaseConnection {

    ParseObject newPost;
    ParseObject newStory;


    public void createNewPost() {
        newPost = new ParseObject("Writes");
    }

    public void createNewStory() {
        newStory = new ParseObject("Story");
    }

    public void putPost(String key, Object value){
        newPost.put(key, value);
    }

    public void putStory(String key, Object value){
        newStory.put(key, value);
    }

    public ParseObject getNewPost(){
        return newPost;
    }

    public ParseObject getNewStory() {
        return newStory;
    }

    public void savePost() {
        try {
            newPost.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveStory() {
        try {
            newStory.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }


    public List<ParseObject> getStories() throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        return query.find();

    }

    public List<ParseObject> getPosts() throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        return query.find();

    }


    public List<ParseObject> getPostsFromStory(String story) throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("inStory", story);
        return query.find();

    }

}


