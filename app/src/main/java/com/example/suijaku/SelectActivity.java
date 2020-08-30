package com.example.suijaku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);
    }
    public void game_start(View view){
        Intent intent = new Intent(SelectActivity.this, GameActivity.class);
        startActivity(intent);
    }
}
