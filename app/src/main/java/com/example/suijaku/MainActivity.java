package com.example.suijaku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void game_start(View view){
        Toast myToast = Toast.makeText(
                getApplicationContext(),
                "ポップアップ！！",
                Toast.LENGTH_SHORT
        );
        myToast.show();
    }
}