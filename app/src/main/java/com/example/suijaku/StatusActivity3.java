package com.example.suijaku;

import static android.graphics.Color.rgb;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.abs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import java.io.IOException;

public class StatusActivity3 extends AppCompatActivity {

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
                startActivity(new Intent(StatusActivity3.this, StatusActivity.class));
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

class StatusDraw3 extends View {
    Paint mpaint = new Paint();
    public StatusDraw3(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mnt, i, j;
        int num_1st=0,num_2nd=0,num_3rd=0;
        float[][] joint1stlayer = new float[0][],joint2ndlayer = new float[0][],joint3rdlayer = new float[0][];
        float max_param = 0.0f;
        try{
            NNBrain_ReLu networks = new NNBrain_ReLu();
            num_1st= networks.num_perceptron1st;
            num_2nd=networks.num_perceptron2nd;
            num_3rd=networks.num_perceptron3rd;
            joint1stlayer = new float[NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2][num_1st];
            joint2ndlayer = new float[num_1st][num_2nd];
            joint3rdlayer = new float[num_2nd][num_3rd];
            for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
                for (j = 0; j < num_1st; j++) {
                    joint1stlayer[i][j] = networks.rtn_nn().perceptron1st[j].weight[i];
                    if (max_param < abs(joint1stlayer[i][j])) {
                        max_param = joint1stlayer[i][j];
                    }
                }
            }
            for (i = 0; i < num_1st; i++) {
                for (j = 0; j < num_2nd; j++) {
                    joint2ndlayer[i][j] = networks.rtn_nn().perceptron2nd[j].weight[i];
                    if (max_param < abs(joint2ndlayer[i][j])) {
                        max_param = joint2ndlayer[i][j];
                    }
                }
            }
            for (i = 0; i <  num_2nd; i++) {
                for (j = 0; j < num_3rd; j++) {
                    joint3rdlayer[i][j] = networks.rtn_nn().perceptron3rd[j].weight[i];
                    if (max_param < abs(joint3rdlayer[i][j])) {
                        max_param = joint3rdlayer[i][j];
                    }
                }
            }
            mpaint.setTextSize(36);
            canvas.drawText("brain: relu border=" + networks.rtn_nn().finalbias, 50, 1600, mpaint);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (mnt = 0; mnt < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; mnt++) {
            canvas.drawCircle(100, 150 + 60 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < num_1st; mnt++) {
            canvas.drawCircle(400, 150 + 115 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < num_2nd; mnt++) {
            canvas.drawCircle(700, 175 + 120 * mnt, 20, mpaint);
        }
        for (mnt = 0; mnt < num_3rd; mnt++) {
            canvas.drawCircle(1000, 225 + 125 * mnt, 20, mpaint);
        }
        for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
            for (j = 0; j < num_1st; j++) {
                if (joint1stlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint1stlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint1stlayer[i][j] * 255) / max_param), (int) (abs(joint1stlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(100, 150 + 60 * i, 400, 150 + 115 * j, mpaint);
            }
        }
        for (i = 0; i < num_1st; i++) {
            for (j = 0; j < num_2nd; j++) {
                if (joint2ndlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint2ndlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint2ndlayer[i][j] * 255) / max_param), (int) (abs(joint2ndlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(400, 150 + 115 * i, 700, 175 + 120 * j, mpaint);
            }
        }
        for (i = 0; i < num_2nd; i++) {
            for (j = 0; j < num_3rd; j++) {
                if (joint3rdlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint3rdlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint3rdlayer[i][j] * 255) / max_param), (int) (abs(joint3rdlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(700, 175 + 120 * i, 1000, 225 + 125 * j, mpaint);
            }
        }
        canvas.drawText("max_param=" + max_param, 700, 1600, mpaint);
    }
}