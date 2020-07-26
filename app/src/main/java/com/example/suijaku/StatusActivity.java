package com.example.suijaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StatusActivity extends View {
    Paint mpaint = new Paint();

    public StatusActivity(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mounts;
        canvas.drawText("Hello world!", 50, 50, mpaint);
        for (mounts = 0; mounts < 23; mounts++) {
            canvas.drawCircle(150, 150 + 60 * mounts, 25, mpaint);
        }
    }
}