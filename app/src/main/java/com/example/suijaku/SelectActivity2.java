package com.example.suijaku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SelectActivity2 extends AppCompatActivity {
    String selected_char="";

    class selection {
        ImageView img_name;
        ImageView frame_name;
        String label_name;
        boolean clicked = false;

        public selection(String name) {
            label_name = name;
            img_name = findViewById(getResources().getIdentifier(name + "_img", "id", getPackageName()));
            frame_name = findViewById(getResources().getIdentifier("frame_" + name, "id", getPackageName()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity2);
        int cnt1;
        final int[] inner_cnt = new int[1];
        final selection[] select = {new selection("robot_full_sigmoid"), new selection("robot_genetic"), new selection("robot_select"), new selection("robot_full_relu")};
        final TextView txt_name = findViewById(R.id.char_name);
        for (cnt1 = 0; cnt1 < 4; cnt1++) {
            final int finalCnt = cnt1;
            select[cnt1].img_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!select[finalCnt].clicked) {
                        select[finalCnt].frame_name.setVisibility(View.VISIBLE);
                        select[finalCnt].clicked = true;
                        selected_char = select[finalCnt].label_name;
                        txt_name.setText(select[finalCnt].label_name);
                        for (inner_cnt[0] = 1; inner_cnt[0] < 4; inner_cnt[0]++) {
                            select[(finalCnt + inner_cnt[0]) % 4].frame_name.setVisibility(View.INVISIBLE);
                            select[(finalCnt + inner_cnt[0]) % 4].clicked = false;
                        }
                    }
                }
            });
        }
    }

    public void game_start(View view) {
        EditText num_gen = findViewById(R.id.editTextNumber);
        try {
            if (selected_char == "robot_genetic" && Integer.valueOf(num_gen.getText().toString()) == 0) {
                new AlertDialog.Builder(SelectActivity2.this).setMessage("世代数を入力してください(>0)").setPositiveButton("OK", null).show();
            } else if (selected_char == "") {
                new AlertDialog.Builder(SelectActivity2.this).setMessage("キャラを選択してください").setPositiveButton("OK", null).show();
            } else {
                Intent intent = new Intent(SelectActivity2.this, TrainActivity.class);
                intent.putExtra("selected_char_list", selected_char);
                intent.putExtra("number_of_generation", num_gen.toString());
                startActivity(intent);
            }
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(SelectActivity2.this).setMessage("世代数を入力してください").setPositiveButton("OK", null).show();
        }
    }
}
