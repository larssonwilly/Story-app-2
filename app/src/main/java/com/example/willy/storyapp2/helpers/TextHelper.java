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

    public String trimStory(String storyText, int max_length){

        String returnText = storyText.substring(0, Math.min(storyText.length(), max_length));

        if (returnText.length() < max_length) {
            return returnText;
        }

        else {
            return fixOnlyWords(returnText);
        }

    }


}
