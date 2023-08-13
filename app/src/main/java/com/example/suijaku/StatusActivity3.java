package com.example.suijaku;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import static com.example.suijaku.Cst.FILE_PATH_NNBSelect;
import static com.example.suijaku.Cst.FILE_PATH_manyneurons;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
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
            if (absY > absX && nowY-startY>50) {
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
        setContentView(R.layout.activity_status_avtivity3);
        gestureDetector=new GestureDetectorCompat(this, new mOnGestureListener());
    }
    public void del_nnbs(View view) {
        Path p = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p = Paths.get(FILE_PATH_NNBSelect);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.deleteIfExists(p);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public void del_manyn(View view){
            Path q = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                q = Paths.get(FILE_PATH_manyneurons);
            }

            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.deleteIfExists(q);
                }
            }catch(IOException e) {
                System.out.println(e);
            }
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
        int i, j;
        int num_1st=0,num_2nd=0,num_3rd=0;
        float[][] joint1stlayer,joint2ndlayer,joint3rdlayer;
        float max_param_select = 0.0f;
        float max_param_manyneuwons=0.0f;
        try{
            NNBrain_Select networks_nnbselect = new NNBrain_Select();
            NNBrain_manyneurons networks_manyneurons=new NNBrain_manyneurons();
            num_1st= networks_nnbselect.num_perceptron1st;
            num_2nd=networks_nnbselect.num_perceptron2nd;
            num_3rd=networks_nnbselect.num_perceptron3rd;
            joint1stlayer = new float[NUM_OF_PLAYERS - 1 + (int) (pow(2,4)+pow(2,4)+pow(2,3))][num_1st];
            joint2ndlayer = new float[num_1st][num_2nd];
            joint3rdlayer = new float[num_2nd][num_3rd];
            for (i = 0; i < NUM_OF_PLAYERS - 1 + (int) (pow(2,4)+pow(2,4)+pow(2,3)); i++) {
                for (j = 0; j < num_1st; j++) {
                    joint1stlayer[i][j] = networks_nnbselect.rtn_nn().perceptron1st[j].weight[i];
                    if (max_param_select < abs(joint1stlayer[i][j])) {
                        max_param_select = joint1stlayer[i][j];
                    }
                }
            }
            for (i = 0; i < num_1st; i++) {
                for (j = 0; j < num_2nd; j++) {
                    joint2ndlayer[i][j] = networks_nnbselect.rtn_nn().perceptron2nd[j].weight[i];
                    if (max_param_select < abs(joint2ndlayer[i][j])) {
                        max_param_select = joint2ndlayer[i][j];
                    }
                }
            }
            for (i = 0; i <  num_2nd; i++) {
                for (j = 0; j < num_3rd; j++) {
                    joint3rdlayer[i][j] = networks_nnbselect.rtn_nn().perceptron3rd[j].weight[i];
                    if (max_param_select < abs(joint3rdlayer[i][j])) {
                        max_param_select = joint3rdlayer[i][j];
                    }
                }
            }
            num_1st= networks_manyneurons.num_perceptron1st;
            num_2nd=networks_manyneurons.num_perceptron2nd;
            num_3rd=networks_manyneurons.num_perceptron3rd;
            joint1stlayer = new float[NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2][num_1st];
            joint2ndlayer = new float[num_1st][num_2nd];
            joint3rdlayer = new float[num_2nd][num_3rd];
            for (i = 0; i < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; i++) {
                for (j = 0; j < num_1st; j++) {
                    joint1stlayer[i][j] = networks_manyneurons.rtn_nn().perceptron1st[j].weight[i];
                    if (max_param_select < abs(joint1stlayer[i][j])) {
                        max_param_select = joint1stlayer[i][j];
                    }
                }
            }
            for (i = 0; i < num_1st; i++) {
                for (j = 0; j < num_2nd; j++) {
                    joint2ndlayer[i][j] = networks_manyneurons.rtn_nn().perceptron2nd[j].weight[i];
                    if (max_param_select < abs(joint2ndlayer[i][j])) {
                        max_param_select = joint2ndlayer[i][j];
                    }
                }
            }
            for (i = 0; i <  num_2nd; i++) {
                for (j = 0; j < num_3rd; j++) {
                    joint3rdlayer[i][j] = networks_manyneurons.rtn_nn().perceptron3rd[j].weight[i];
                    if (max_param_select < abs(joint3rdlayer[i][j])) {
                        max_param_select = joint3rdlayer[i][j];
                    }
                }
            }
            mpaint.setTextSize(36);
            canvas.drawText("brain: NNBselect border=" + networks_nnbselect.rtn_nn().finalbias, 50, 200, mpaint);
            canvas.drawText("brain: NNBmanynaurons border=" + networks_manyneurons.rtn_nn().finalbias, 50, 500, mpaint);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        canvas.drawText("max_param=" + max_param_select, 50, 300, mpaint);
        canvas.drawText("max_param=" + max_param_manyneuwons, 50, 600, mpaint);
    }
}