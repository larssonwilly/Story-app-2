package com.example.willy.storyapp2.helpers;

/**
 * Helper class for formatting of texts relevant to the story
 */
public class TextHelper {


    /**
     * Fixes the string so that the visible story text for the user doesn't start with half a word
     * @param lastCharsOfStory the last visible part of the string
     * @return String value with half-finished words removed.
     */
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

    /**
     * Trims the length of the story, and adjusts if necessary with fixOnlyWords
     *
     * @param storyText the story content
     * @param MAX_LENGTH maximum visible length of the string
     * @return shortened String with half-cut words in the beginning removed.
     */
    public String trimStory(StringBuilder storyText, final int MAX_LENGTH) {

        String lastCharsOfStory = storyText.substring(Math.max(0, storyText.length() - (MAX_LENGTH + 1)));

        if (lastCharsOfStory.length() < MAX_LENGTH) {
            return lastCharsOfStory;
        } else {
            return fixOnlyWords(lastCharsOfStory);
        }

    }


}
