package com.example.suijaku;

import static android.graphics.Color.rgb;
import static com.example.suijaku.Cst.FILE_PATH;
import static com.example.suijaku.Cst.FILE_PATH_NNBSelect;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.abs;
import static java.util.logging.Logger.global;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.window.layout.WindowMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class StatusActivity extends AppCompatActivity {

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
                startActivity(new Intent(StatusActivity.this, StatusActivity2.class));
                finish();
            }else if(absY > absX && nowY-startY<-50) {
                //    横方向の操作の場合
                startActivity(new Intent(StatusActivity.this, StatusActivity3.class));
                finish();
            }
            return true;
        }
    }
    public static int ScreenHeight;
    public static int ScreenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_avtivity);
        gestureDetector=new GestureDetectorCompat(this, new mOnGestureListener());
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        Point realSize = new Point();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            disp.getRealSize(realSize);
        }

        ScreenWidth = realSize.x;
        ScreenHeight = realSize.y;
    }
    public void del_nn(View view){
        Path p = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p = Paths.get(FILE_PATH);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.deleteIfExists(p);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
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
        int mnt, i, j;
        int num_1st=0,num_2nd=0,num_3rd=0;
        float[][] joint1stlayer = new float[0][],joint2ndlayer = new float[0][],joint3rdlayer = new float[0][];
        float max_param = 0.0f;
        try{
            NNBrain networks = new NNBrain();
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
            for (i = 0; i < num_2nd; i++) {
                for (j = 0; j < num_3rd; j++) {
                    joint3rdlayer[i][j] = networks.rtn_nn().perceptron3rd[j].weight[i];
                    if (max_param < abs(joint3rdlayer[i][j])) {
                        max_param = joint3rdlayer[i][j];
                    }
                }
            }
            mpaint.setTextSize(24);
            canvas.drawText("brain: sigmoid border=" + networks.rtn_nn().finalbias, 50, StatusActivity.ScreenHeight-270, mpaint);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (mnt = 0; mnt < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; mnt++) {
            canvas.drawCircle(50, 50 + (StatusActivity.ScreenHeight-265)/(NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2)* mnt, 10, mpaint);
        }
        for (mnt = 0; mnt < num_1st; mnt++) {
            canvas.drawCircle(50+StatusActivity.ScreenWidth/3, 50 + (StatusActivity.ScreenHeight-265)/num_1st* mnt, 10, mpaint);
        }
        for (mnt = 0; mnt < 12; mnt++) {
            canvas.drawCircle(50+2*StatusActivity.ScreenWidth/3, 100 + (StatusActivity.ScreenHeight-265)/13* mnt, 10, mpaint);
        }
        for (mnt = 0; mnt < 11; mnt++) {
            canvas.drawCircle(StatusActivity.ScreenWidth-50, 100 + (StatusActivity.ScreenHeight-265)/12* mnt, 10, mpaint);
        }
        for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
            for (j = 0; j < num_1st; j++) {
                if (joint1stlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint1stlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint1stlayer[i][j] * 255) / max_param), (int) (abs(joint1stlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(50, 50 + (StatusActivity.ScreenHeight-265)/(NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2)* i, 50+StatusActivity.ScreenWidth/3, 50 + (StatusActivity.ScreenHeight-265)/num_1st* j, mpaint);
            }
        }
        for (i = 0; i < num_1st; i++) {
            for (j = 0; j < num_2nd; j++) {
                if (joint2ndlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint2ndlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint2ndlayer[i][j] * 255) / max_param), (int) (abs(joint2ndlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(50+StatusActivity.ScreenWidth/3, 50 + (StatusActivity.ScreenHeight-265)/num_1st* i, 50+2*StatusActivity.ScreenWidth/3, 100 + (StatusActivity.ScreenHeight-265)/13* j, mpaint);
            }
        }
        for (i = 0; i < num_2nd; i++) {
            for (j = 0; j < num_3rd; j++) {
                if (joint3rdlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint3rdlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255, (int) (abs(joint3rdlayer[i][j] * 255) / max_param), (int) (abs(joint3rdlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(50+2*StatusActivity.ScreenWidth/3, 100 + (StatusActivity.ScreenHeight-265)/13* i, StatusActivity.ScreenWidth-50, 100 + (StatusActivity.ScreenHeight-265)/12* j, mpaint);
            }
        }
        canvas.drawText("max_param=" + max_param, (float) (StatusActivity.ScreenWidth*0.6), StatusActivity.ScreenHeight-270, mpaint);
    }

}