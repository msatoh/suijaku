package com.example.suijaku;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
        final selection select[]={new selection("zako"), new selection("strong"),new selection("robot_full_sigmoid"),new selection("robot_genetic"),new selection("robot_select"),new selection("robot_full_relu")};
        for(cnt=0;cnt<6;cnt++) {
            final int finalCnt = cnt;
            select[cnt].img_name.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (select[finalCnt].clicked) {
                                                         select[finalCnt].frame_name.setVisibility(View.INVISIBLE);
                                                         select[finalCnt].clicked = false;
                                                     } else {
                                                         select[finalCnt].frame_name.setVisibility(View.VISIBLE);
                                                         select[finalCnt].clicked = true;
                                                     }
                                                 }
                                             }
            );
        }
    }

    public void game_start(View view) {
        Intent intent = new Intent(SelectActivity.this, GameActivity.class);
        startActivity(intent);
    }


}
