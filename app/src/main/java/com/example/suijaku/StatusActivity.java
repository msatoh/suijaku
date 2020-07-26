package com.example.suijaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;

public class StatusActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_avtivity);
    }
}

class StatusDraw extends View {
    Paint mpaint = new Paint();

    public StatusDraw(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mounts;
        canvas.drawText("Hello world!", 50, 50, mpaint);
        for (mounts = 0; mounts < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; mounts++) {
            canvas.drawCircle(100, 150 + 60 * mounts, 25, mpaint);
        }
        for (mounts = 0; mounts < 13; mounts++) {
            canvas.drawCircle(400, 150 + 115 * mounts, 25, mpaint);
        }
        for (mounts = 0; mounts < 12; mounts++) {
            canvas.drawCircle(700, 175 + 120 * mounts, 25, mpaint);
        }
        for (mounts = 0; mounts < 11; mounts++) {
            canvas.drawCircle(1000, 225 + 125 * mounts, 25, mpaint);
        }
    }
}