package com.example.willy.storyapp2.helpers;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Class for direct connection with the database. Is retrieved and formatted by the StorylistHandler class
 */
public class DatabaseConnection {

    ParseObject newPost;
    ParseObject newStory;


    /**
     * Creates a newPost object at "Writes"
     */
    public void createNewPost() {
        newPost = new ParseObject("Writes");
    }

    /**
     * Creats a new object in "Story"
     */
    public void createNewStory() {
        newStory = new ParseObject("Story");
    }

    /**
     * Stores data in the post database
     * @param key in what column the data should be stored
     * @param value the data
     */
    public void putPost(String key, Object value) {
        newPost.put(key, value);
    }

    /**
     * Stores data in the story database
     * @param key in what column the data should be stored
     * @param value the data
     */
    public void putStory(String key, Object value) {
        newStory.put(key, value);
    }

    /**
     * @return locally stored post object
     */
    public ParseObject getNewPost() {
        return newPost;
    }

    /**
     * @return locally stored story object
     */
    public ParseObject getNewStory() {
        return newStory;
    }

    /**
     * Saves the newPost object to the database
     */
    public void savePost() {
        try {
            newPost.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the newStory object in the database
     */
    public void saveStory() {
        try {
            newStory.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return list of finished stories
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getFinishedStories() throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        query.whereEqualTo("isCompleted", true);
        return query.find();


    }

    /**
     * @return list of all stories
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getStories() throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        return query.find();

    }

    /**
     * @param author username of the specified author
     * @return a list of posts from the author
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getPostsFromAuthor(String author) throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("author", author);
        return query.find();

    }

    /**
     * Retrieves post from a specific story
     * @param story the ObjectID of the story as a String
     * @return a list of all the posts a story containts
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getPostsFromStory(String story) throws com.parse.ParseException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Writes");
        query.whereEqualTo("inStory", story);
        return query.find();

    }

}


