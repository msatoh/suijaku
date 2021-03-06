package com.example.suijaku;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.example.suijaku.NNBrain;
import com.example.suijaku.R;

import java.io.IOException;

import static android.graphics.Color.rgb;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.abs;

public class StatusActivity2 extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);//    GestureDetectorにイベントを渡すために必要です
    }

    class mOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float startX = e1.getX();
            float startY = e1.getY();
            float nowX = e2.getX();
            float nowY = e2.getY();
            float absX = Math.abs(nowX - startX);
            float absY = Math.abs(nowY - startY);
//    フリック開始時のx,y座標、終了時のx,y座標を保持
//    指の移動距離の絶対値

            if (absX + absY < 50) {
                return false;
            }//    誤作動を防ぐため、移動距離が短い時は何もしない。調節してください
            if (absX > absY) {
                //    横方向の操作の場合
                startActivity(new Intent(StatusActivity2.this, StatusActivity.class));
                finish();
            }
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_avtivity2);
        gestureDetector=new GestureDetectorCompat(this, new mOnGestureListener());
    }
}

class StatusDraw2 extends View {
    Paint mpaint = new Paint();
    public StatusDraw2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mnt, i, j;
        float[][] joint1stlayer = new float[NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2][13];
        float[][] joint2ndlayer = new float[13][12];
        float[][] joint3rdlayer = new float[12][11];
        float max_param = 0.0f;
        try {
            NNBrain_Select networks = new NNBrain_Select();
            for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
                for (j = 0; j < 13; j++) {
                    joint1stlayer[i][j] = networks.rtn_nn().perceptron1st[j].weight[i];
                    if (max_param < abs(joint1stlayer[i][j])) {
                        max_param = joint1stlayer[i][j];
                    }
                }
            }
            for (i = 0; i < 13; i++) {
                for (j = 0; j < 12; j++) {
                    joint2ndlayer[i][j] = networks.rtn_nn().perceptron2nd[j].weight[i];
                    if (max_param < abs(joint2ndlayer[i][j])) {
                        max_param = joint2ndlayer[i][j];
                    }
                }
            }
            for (i = 0; i < 12; i++) {
                for (j = 0; j < 11; j++) {
                    joint3rdlayer[i][j] = networks.rtn_nn().perceptron3rd[j].weight[i];
                    if (max_param < abs(joint3rdlayer[i][j])) {
                        max_param = joint3rdlayer[i][j];
                    }
                }
            }
            mpaint.setTextSize(36);
            canvas.drawText("border=" + networks.rtn_nn().finalbias, 50, 1600, mpaint);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (mnt = 0; mnt < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; mnt++) {
            canvas.drawCircle(100, 150 + 60 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < 13; mnt++) {
            canvas.drawCircle(400, 150 + 115 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < 12; mnt++) {
            canvas.drawCircle(700, 175 + 120 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < 11; mnt++) {
            canvas.drawCircle(1000, 225 + 125 * mnt, 20, mpaint);
        }
        for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
            for (j = 0; j < 13; j++) {
                if (joint1stlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint1stlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint1stlayer[i][j] * 255) / max_param), (int) (abs(joint1stlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(100, 150 + 60 * i, 400, 150 + 115 * j, mpaint);
            }
        }
        for (i = 0; i < 13; i++) {
            for (j = 0; j < 12; j++) {
                if (joint2ndlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint2ndlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint2ndlayer[i][j] * 255) / max_param), (int) (abs(joint2ndlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(400, 150 + 115 * i, 700, 175 + 120 * j, mpaint);
            }
        }
        for (i = 0; i < 12; i++) {
            for (j = 0; j < 11; j++) {
                if (joint3rdlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint3rdlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint3rdlayer[i][j] * 255) / max_param), (int) (abs(joint3rdlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(700, 175 + 120 * i, 1000, 225 + 125 * j, mpaint);
            }
        }
        canvas.drawText("max_param=" + max_param, 350, 1600, mpaint);
    }
}