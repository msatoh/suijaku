package com.example.suijaku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectActivity extends AppCompatActivity {
    class selection{
        ImageView img_name;
        ImageView frame_name;
        String label_name;
        boolean clicked=false;
        public selection(String name){
            label_name=name;
            img_name=findViewById(getResources().getIdentifier(name+"_img", "id", getPackageName()));
            frame_name=findViewById(getResources().getIdentifier("frame_"+name,"id",getPackageName()));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);
        int cnt;
        final int[] num_of_clicked = { 0 };
        final selection select[]={new selection("zako"), new selection("strong"),new selection("robot_full_sigmoid"),new selection("robot_genetic"),new selection("robot_select"),new selection("robot_full_relu")};
        final TextView txt_name=findViewById(R.id.char_name);
        for(cnt=0;cnt<6;cnt++) {
            final int finalCnt = cnt;
            select[cnt].img_name.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (select[finalCnt].clicked) {
                                                                select[finalCnt].frame_name.setVisibility(View.INVISIBLE);
                                                                select[finalCnt].clicked = false;
                                                                num_of_clicked[0]--;
                                                            } else {
                                                                if (num_of_clicked[0] >= 4) {
                                                                    AlertThread thread_alert = new AlertThread();
                                                                    thread_alert.start();
                                                                } else {
                                                                    select[finalCnt].frame_name.setVisibility(View.VISIBLE);
                                                                    select[finalCnt].clicked = true;
                                                                    num_of_clicked[0]++;
                                                                    txt_name.setText(select[finalCnt].label_name);
                                                                }
                                                            }
                                                        }
                                                    }
            );
        }
    }

    class AlertThread extends Thread{
        public void run(){
            new AlertDialog.Builder(getApplicationContext()).setMessage("4人までしか選べません。").setPositiveButton("OK", null).show();
        }
    }

    public void game_start(View view) {
        Intent intent = new Intent(SelectActivity.this, GameActivity.class);
        startActivity(intent);
    }


}
