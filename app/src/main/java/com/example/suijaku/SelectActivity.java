package com.example.suijaku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);
        ImageView zako_select = findViewById(getResources().getIdentifier("zako_img", "id", getPackageName()));
        final ImageView zako_frame = findViewById(getResources().getIdentifier("frame_zako", "id", getPackageName()));

        zako_select.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               zako_frame.setVisibility(View.VISIBLE);
                                           }
                                       }
        );
    }

    public void game_start(View view) {
        Intent intent = new Intent(SelectActivity.this, GameActivity.class);
        startActivity(intent);
    }


}
