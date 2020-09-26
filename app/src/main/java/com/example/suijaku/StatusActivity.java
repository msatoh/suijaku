package com.example.suijaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import static android.graphics.Color.rgb;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.abs;

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
                    mpaint.setColor(rgb(255,(int) (abs(joint2ndlayer[i][j] * 255) / max_param),(int) (abs(joint2ndlayer[i][j] * 255) / max_param) ));
                }
                canvas.drawLine(400, 150 + 115 * i, 700, 175 + 120 * j, mpaint);
            }
        }
        for (i = 0; i < 12; i++) {
            for (j = 0; j < 11; j++) {
                if (joint3rdlayer[i][j] > 0) {
                    mpaint.setColor(rgb(0, 0, (int) ((joint3rdlayer[i][j] * 255) / max_param)));
                } else {
                    mpaint.setColor(rgb(255,(int) (abs(joint3rdlayer[i][j] * 255) / max_param),(int) (abs(joint3rdlayer[i][j] * 255) / max_param)));
                }
                canvas.drawLine(700, 175 + 120 * i, 1000, 225 + 125 * j, mpaint);
            }
        }
        canvas.drawText("max_param=" + max_param, 350, 1600, mpaint);
    }
}