package com.example.peelingv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplasActivity extends AppCompatActivity {
    public static  int SPLASH_SCREEN = 3000;

    Animation topAnim,bottomAnim;
    ImageView image,detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splas);

        //Animation
        topAnim = AnimationUtils.loadAnimation(SplasActivity.this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(SplasActivity.this,R.anim.bottom_animation);
        //Hooks
        image  = findViewById(R.id.imageView);
        detail = findViewById(R.id.imgDetail);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplasActivity.this,Login.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}