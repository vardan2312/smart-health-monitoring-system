package com.example.monitoring;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imageViewHeart;
    private static final int ANIMATION_DURATION = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewHeart = findViewById(R.id.imageViewHeart);
        // Create ObjectAnimator for scaling up
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(imageViewHeart, "scaleY", 1.2f);
        scaleUp.setDuration(3000); // Duration for scaling up (3 seconds)
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());

        // Create ObjectAnimator for scaling down
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(imageViewHeart, "scaleY", 1.0f);
        scaleDown.setDuration(3000); // Duration for scaling down (3 seconds)
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());

        // Create AnimatorSet to play the animations sequentially
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleUp).before(scaleDown); // Scale up first, then scale down
        animatorSet.play(scaleDown).after(3000); // Start scaling down after 3 seconds

        // Start the animation
        animatorSet.start();

        // Delay transition to MainActivity after animation duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, ANIMATION_DURATION);

    }
}