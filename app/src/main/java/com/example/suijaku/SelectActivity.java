package com.example.suijaku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {
    ArrayList<String> selected_char = new ArrayList<>();
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
        final selection[] select = {new selection("zako"), new selection("strong"), new selection("robot_full_sigmoid"), new selection("robot_manyneurons"), new selection("robot_select"), new selection("robot_full_relu")};
        final TextView txt_name = findViewById(R.id.char_name);
        for (cnt = 0; cnt < 6; cnt++) {
            final int finalCnt = cnt;
            select[cnt].img_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (select[finalCnt].clicked) {
                        select[finalCnt].frame_name.setVisibility(View.INVISIBLE);
                        select[finalCnt].clicked = false;
                        selected_char.remove(select[finalCnt].label_name);
                    } else {
                        if (selected_char.size() >= 4) {
                            new AlertDialog.Builder(SelectActivity.this).setMessage("4人までしか選べません。").setPositiveButton("OK", null).show();
                        } else {
                            select[finalCnt].frame_name.setVisibility(View.VISIBLE);
                            select[finalCnt].clicked = true;
                            selected_char.add(select[finalCnt].label_name);
                            txt_name.setText(select[finalCnt].label_name);
                        }
                    }
                }
            });
        }
    }

    public void game_start(View view) {
        if (selected_char.size() == 4) {
            Intent intent = new Intent(SelectActivity.this, GameActivity.class);
            intent.putExtra("selected_char_list", selected_char);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(SelectActivity.this).setMessage("4人選んでください。").setPositiveButton("OK", null).show();
        }
    }


}
