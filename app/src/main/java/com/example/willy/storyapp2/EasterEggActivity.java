package com.example.willy.storyapp2;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


//ToDo: javadoc, describe the class
public class EasterEggActivity extends Activity {
    RotateAnimation rA;
    MediaPlayer mediaPlayer;
    private Button randomButton;
    private ImageView img;
    private EasterEggActivity view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);




        img =  (ImageView) findViewById(R.id.image2);
        rA = new RotateAnimation(0, 11520, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rA.setDuration(20000);


        //Music Player
        mediaPlayer = MediaPlayer.create(this.getBaseContext(), R.raw.bennyhill);
        // no need to call prepare(); create() does that for you

        spin();



    }


    //ToDo: javadoc, public method
    public void ChangeBackground() {

        //Blinka blinka
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout1);
        final AnimationDrawable drawable = new AnimationDrawable();
        final Handler handler = new Handler();

        drawable.addFrame(new ColorDrawable(Color.RED), 428);
        drawable.addFrame(new ColorDrawable(Color.GREEN), 428);
        drawable.setOneShot(false);

        layout.setBackgroundDrawable(drawable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.start();
            }
        }, 100);



    }
    //ToDo: delete text below
    /*
    CountDownTimer timer = new CountDownTimer(5000, 5000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // Nothing to do
        }

        @Override
        public void onFinish() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    };*/


    //ToDo: javadoc, public method
    public void spin(){

        img.startAnimation(rA);
        mediaPlayer.start();
        ChangeBackground();
        // timer.start();


    }


    //ToDo: javadoc, public method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //ToDo: javadoc, public method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
