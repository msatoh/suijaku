package com.example.suijaku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void game_start(View view){
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
    public void train_npc(View view){
        Intent intent = new Intent(MainActivity.this, TrainActivity.class);
        startActivity(intent);
    }
    public void growth_of_npc(View view){
        Intent intent = new Intent(MainActivity.this, StatusActivity.class);
        startActivity(intent);
    }

}