package com.example.willy.storyapp2.helpers;

/**
 * Created by Quaxi on 15-05-29.
 */
public class TextHelper {

    public String fixOnlyWords(String lastCharsOfStory) {

        String returnString;

        if (lastCharsOfStory.charAt(0) != ' ') {
            int iterator = 0;
            while (lastCharsOfStory.charAt(iterator) != ' ') {
                iterator++;
            }
            returnString = lastCharsOfStory.substring(iterator + 1, lastCharsOfStory.length());

        } else {
            returnString = lastCharsOfStory;
        }

        return returnString;
    }

    public String trimStory(StringBuilder storyText, int max_length){

        String lastCharsOfStory = storyText.substring(Math.max(0, storyText.length() - (max_length + 1)));

        if (lastCharsOfStory.length() < max_length) {
            return lastCharsOfStory;
        }

        else {
            return fixOnlyWords(lastCharsOfStory);
        }

    }


}
