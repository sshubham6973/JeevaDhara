package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;

public class app_blood_facts extends AppCompatActivity {
    ImageView back;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_blood_facts);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        back = findViewById(R.id.imageView12);
        scrollView = findViewById(R.id.scrollView6);
        scrollView.setVerticalScrollBarEnabled(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(app_blood_facts.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }
}