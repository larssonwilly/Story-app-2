package com.example.willy.storyapp2;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class ParseWorkshopApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

		/*
		 * Add Parse initialization code here
		 */


        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }
}