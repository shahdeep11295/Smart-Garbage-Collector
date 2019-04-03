package com.example.chauh.swms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        Thread timer=new Thread()
        {
            public void run()
            {
                try{
                    sleep(3000);

                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent i=new Intent(getApplicationContext(),Signin.class);//You Can Use here Intent i=new Intent(getApplicationContext(),second.class);
                    startActivity(i);

                    finish();      //     song.stop();

                }

            }
        };
        timer.start();
    }
}
