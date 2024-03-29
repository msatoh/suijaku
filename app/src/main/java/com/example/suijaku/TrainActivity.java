package com.example.suijaku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.example.suijaku.Cst.FILE_PATH;
import static com.example.suijaku.Cst.FILE_PATH_NNBSelect;
import static com.example.suijaku.Cst.FILE_PATH_Relu;
import static com.example.suijaku.Cst.FILE_PATH_manyneurons;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;

public class TrainActivity extends AppCompatActivity {
    final int TRASH_TIME = 170;
    final int THINKING_TIME = 370;
    final Field field_entity = new Field();
    final Player[] p = new Player[NUM_OF_PLAYERS];
    final ArrayList<TextView> p_card = new ArrayList<>();
    final TextView[] com_card = new TextView[NUM_OF_PLAYERS];
    final TextView[] p_name = new TextView[NUM_OF_PLAYERS];
    final ImageView[] com_turn = new ImageView[NUM_OF_PLAYERS];
    final TextView[] p_stat = new TextView[NUM_OF_PLAYERS];
    ImageView p_turn;
    Handler pass_card;
    Handler trash_card;
    final Rank rank_use = new Rank();
    String char_li;
    private ArrayList<Integer> used_li = new ArrayList<>();

    private void init_array(ArrayList<Integer> used_li_in) {
        int cnt;
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            used_li_in.add(cnt);
        }
    }

    private Card gen_rand_card() {
        mark mark_in;
        Card a_card = new Card();
        Random mark_num_seed = new Random();
        int rand_seed = mark_num_seed.nextInt(used_li.size());
        int rand_param = used_li.get(rand_seed);
        int mark_num = rand_param / 13;
        int strength_num = rand_param % 13 + 1;
        used_li.remove((Integer) rand_param);
        switch (mark_num) {
            case 0:
                mark_in = mark.heart;
                break;
            case 1:
                mark_in = mark.spade;
                break;
            case 2:
                mark_in = mark.dia;
                break;
            case 3:
                mark_in = mark.club;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mark_num);
        }
        a_card.mark_use = mark_in;
        a_card.give_strength(strength_num);
        return a_card;
    }

    private String show_card(Card card_in) {
        String mark_disp;
        switch (card_in.mark_use) {
            case heart:
                mark_disp = "♥";
                break;
            case spade:
                mark_disp = "♠";
                break;
            case dia:
                mark_disp = "♦";
                break;
            case club:
                mark_disp = "♣";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + card_in.mark_use);
        }
        mark_disp.concat(card_in.num);

        return "[" + mark_disp + card_in.num + "]";
    }

    private String show_cards(ArrayList<Card> cards_in) {
        int localcnt;
        String cards_disp = "";
        for (localcnt = 0; localcnt < cards_in.size(); localcnt++) {
            cards_disp += show_card(cards_in.get(localcnt));
        }
        return cards_disp;
    }

    private void reset_all_pass() {
        int cnt;
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            p[cnt].is_pass = false;
            p_stat[cnt].setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.os.Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_avtivity);
        Intent get_intent = getIntent();
        char_li = get_intent.getStringExtra("selected_char_li");
        int cnt;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        pass_card = new Handler();
        trash_card = new Handler();
        field_entity.txt = findViewById(R.id.field);
        this.p_turn = findViewById(R.id.PLAYER_turn);
        for (cnt = 0; cnt < NUM_OF_CARDS / NUM_OF_PLAYERS + 1; cnt++) {
            p_card.add((TextView) findViewById(getResources().getIdentifier("p_card_in_hand_" + cnt, "id", getPackageName())));
        }
        for (cnt = 1; cnt < NUM_OF_PLAYERS; cnt++) {
            com_card[cnt] = findViewById(getResources().getIdentifier("COM" + cnt + "_card", "id", getPackageName()));
            com_turn[cnt] = findViewById(getResources().getIdentifier("COM" + cnt + "_turn", "id", getPackageName()));
        }
        init_array(used_li);
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            p[cnt] = new Player();
            p_name[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_name", "id", getPackageName()));
            p_stat[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_stat", "id", getPackageName()));
            p_stat[cnt].setText("");
            if (cnt == 0) {
                p[cnt].name = "user";
            } else {
                p[cnt].name = "COM" + cnt;
            }
        }
        p[0].name = char_li;
        try {
            if (char_li.contains("robot_full_sigmoid")) {
                p[0].algorhythm_to_choose_card = new NNBrain();
            } else if (char_li.contains("robot_full_relu")) {
                p[0].algorhythm_to_choose_card = new NNBrain_ReLu();
            } else if (char_li.contains("robot_select")) {
                p[0].algorhythm_to_choose_card = new NNBrain_Select();
            }else if (char_li.contains("robot_manyneurons")) {
                p[0].algorhythm_to_choose_card = new NNBrain_manyneurons();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        p[1].name = "ザコ";
        p[1].algorhythm_to_choose_card = new BasicBrain();
        p[2].name = "強い";
        p[2].algorhythm_to_choose_card = new StrongerBrain();
        try {
            p[3].algorhythm_to_choose_card = new NNBrain_Select();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        p[3].name = "NNB_Select";
        p[4].algorhythm_to_choose_card = new BasicBrain();
        p[4].name = "ザコ";
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            p_name[cnt].setText(p[cnt].name);
        }
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            p[cnt % 5].insert_card(gen_rand_card());
        }
        final boolean[] clicked = new boolean[p[0].card_li.size()];
        for (cnt = 0; cnt < p[0].card_li.size(); cnt++) {
            clicked[cnt] = false;
            p_card.get(cnt).setText(show_card(p[0].card_li.get(cnt)));
        }
        for (cnt = 1; cnt < NUM_OF_PLAYERS; cnt++) {
            com_card[cnt].setText("" + p[cnt].card_li.size() + "枚");
        }
        for (cnt = 0; cnt < NUM_OF_CARDS / NUM_OF_PLAYERS + 1; cnt++) {
            final int finalcnt = cnt;
            p_card.get(cnt).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!clicked[finalcnt]) {
                        p_card.get(finalcnt).setTextColor(Color.BLUE);
                        p_card.get(finalcnt).setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalcnt] = true;
                        p[0].players_sel_card_li.sel_card.add(p[0].card_li.get(finalcnt));
                        p[0].players_sel_card_li.card_id_for_txtview.add(p[0].card_li.indexOf(p[0].card_li.get(finalcnt)));
                    } else {
                        p_card.get(finalcnt).setTextColor(Color.BLACK);
                        p_card.get(finalcnt).setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt] = false;
                        p[0].players_sel_card_li.sel_card.remove(p[0].card_li.get(finalcnt));
                        p[0].players_sel_card_li.card_id_for_txtview.remove((Integer) finalcnt);
                    }
                }
            });
            p_card.get(cnt).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Check checker = new Check();
                    final int[] localcnt = new int[1];
                    if (checker.chk_if_decideable(p[0].players_sel_card_li.sel_card, field_entity.field_card)) {
                        builder.setTitle("COMの捨てたカード")
                                .setMessage(show_cards(p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card)))
                                .setPositiveButton("ラーン", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        p[0].algorhythm_to_choose_card.back_propagation(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card, p[0].players_sel_card_li.sel_card);
                                    }
                                })
                                .setNeutralButton("送る(自分の選択したカード)", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        field_entity.txt.setText(show_cards(p[0].players_sel_card_li.sel_card));
                                        field_entity.field_card = (ArrayList<Card>) p[0].players_sel_card_li.sel_card.clone();
                                        for (localcnt[0] = 0; localcnt[0] < p[0].players_sel_card_li.card_id_for_txtview.size(); localcnt[0]++) {
                                            p_card.remove(p[0].players_sel_card_li.card_id_for_txtview.get(localcnt[0]));
                                        }
                                        p[0].players_sel_card_li.card_id_for_txtview.clear();
                                        for (localcnt[0] = 0; localcnt[0] < p[0].players_sel_card_li.sel_card.size(); localcnt[0]++) {
                                            p[0].card_li.remove(p[0].players_sel_card_li.sel_card.get(localcnt[0]));
                                        }
                                        for (localcnt[0] = 0; localcnt[0] < p[0].card_li.size(); localcnt[0]++) {
                                            p_card.get(localcnt[0]).setText(show_card(p[0].card_li.get(localcnt[0])));
                                            clicked[localcnt[0]] = false;
                                            p_card.get(localcnt[0]).setTextColor(Color.BLACK);
                                            p_card.get(localcnt[0]).setTypeface(Typeface.DEFAULT);
                                        }
                                        for (localcnt[0] = p[0].card_li.size(); localcnt[0] < p[0].card_li.size() + p[0].players_sel_card_li.sel_card.size(); localcnt[0]++) {
                                            p_card.get(localcnt[0]).setText("");
                                        }
                                        p[0].players_sel_card_li.sel_card.clear();
                                        MyThread passing_card = new MyThread();
                                        passing_card.start();
                                    }
                                })
                                .setNegativeButton("送る(COMの選択したカード)", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        p[0].players_sel_card_li.card_id_for_txtview.clear();
                                        field_entity.txt.setText(show_cards(p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card)));
                                        field_entity.field_card = (ArrayList<Card>) p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card).clone();
                                        for (localcnt[0] = 0; localcnt[0] < p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card).size(); localcnt[0]++) {
                                            p_card.remove(p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card).get(localcnt[0]));
                                            p[0].card_li.remove(p[0].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(), p[2].card_li.size(), p[3].card_li.size(), p[4].card_li.size(), p[0].card_li, field_entity.field_card).get(localcnt[0]));
                                        }
                                        for (localcnt[0] = 0; localcnt[0] < p[0].card_li.size(); localcnt[0]++) {
                                            p_card.get(localcnt[0]).setText(show_card(p[0].card_li.get(localcnt[0])));
                                            clicked[localcnt[0]] = false;
                                            p_card.get(localcnt[0]).setTextColor(Color.BLACK);
                                            p_card.get(localcnt[0]).setTypeface(Typeface.DEFAULT);
                                        }
                                        for (localcnt[0] = p[0].card_li.size(); localcnt[0] < p[0].card_li.size() + p[0].players_sel_card_li.sel_card.size(); localcnt[0]++) {
                                            p_card.get(localcnt[0]).setText("");
                                        }
                                        p[0].players_sel_card_li.sel_card.clear();
                                        MyThread passing_card = new MyThread();
                                        passing_card.start();
                                    }
                                });
                        builder.show();

                    } else {
                        Toast.makeText(getApplicationContext(), "出せません！", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }

    class MyThread extends Thread {
        Button btn_pass = findViewById(R.id.pass);
        TextView rank_view = findViewById(R.id.p_card_in_hand_0);
        Check checker = new Check();

        @Override
        public void run() {
            final ArrayList<Card>[] chosen_card = new ArrayList[]{new ArrayList<>()};
            int person_num;
            final int[] inner_person_num = new int[1];
            do {
                try {
                    Thread.sleep(TRASH_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (person_num = 1; person_num < NUM_OF_PLAYERS; person_num++) {
                    final int finalperson_num = person_num;
                    if (!p[person_num].if_end) {
                        pass_card.post(new Runnable() {
                            public void run() {
                                btn_pass.setVisibility(View.INVISIBLE);
                                p_turn.setVisibility(View.INVISIBLE);
                                if (p[0].is_pass) {
                                    p_stat[0].setText("pass");
                                }
                                if (p[0].card_li.size() == 0 && !p[0].if_end) {
                                    p[0].if_end = true;
                                    rank_view.setText(rank_use.set_rank());
                                }
                                if (finalperson_num != 1) {
                                    com_turn[finalperson_num - 1].setVisibility(View.INVISIBLE);
                                }
                                com_turn[finalperson_num].setVisibility(View.VISIBLE);
                                if ((p[(finalperson_num + 1) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 1) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end)) {
                                    field_entity.txt.setText("");
                                    field_entity.field_card.clear();
                                    reset_all_pass();
                                }
                            }
                        });
                        try {
                            Thread.sleep(THINKING_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        trash_card.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!p[finalperson_num].is_pass && !p[finalperson_num].if_end) {
                                    chosen_card[0] = p[finalperson_num].algorhythm_to_choose_card.calc_card_to_put(p[(finalperson_num + 1) % 5].card_li.size(), p[(finalperson_num + 2) % 5].card_li.size(), p[(finalperson_num + 3) % 5].card_li.size(), p[(finalperson_num + 4) % 5].card_li.size(), p[finalperson_num].card_li, field_entity.field_card);
                                    if (checker.chk_if_decideable(chosen_card[0], field_entity.field_card)) {
                                        for (inner_person_num[0] = 0; inner_person_num[0] < chosen_card[0].size(); inner_person_num[0]++) {
                                            p[finalperson_num].card_li.remove(chosen_card[0].get(inner_person_num[0]));
                                        }
                                        field_entity.txt.setText(show_cards(chosen_card[0]));
                                        field_entity.field_card = chosen_card[0];
                                        if (p[finalperson_num].card_li.size() == 0 && !p[finalperson_num].if_end) {
                                            p[finalperson_num].if_end = true;
                                            com_card[finalperson_num].setText(rank_use.set_rank());
                                        } else {
                                            com_card[finalperson_num].setText("" + p[finalperson_num].card_li.size() + "枚");
                                        }
                                    } else {
                                        p[finalperson_num].is_pass = true;
                                        p_stat[finalperson_num].setText("pass");
                                    }
                                }
                                if (finalperson_num == 4) {
                                    com_turn[finalperson_num].setVisibility(View.INVISIBLE);
                                    p_turn.setVisibility(View.VISIBLE);
                                    btn_pass.setVisibility(View.VISIBLE);
                                    if ((p[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end) && (p[(finalperson_num) % NUM_OF_PLAYERS].is_pass || p[(finalperson_num) % NUM_OF_PLAYERS].if_end)) {
                                        field_entity.txt.setText("");
                                        field_entity.field_card.clear();
                                        reset_all_pass();
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(TRASH_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } while (p[0].is_pass || p[0].if_end || (p[1].if_end && p[2].if_end && p[3].if_end && p[4].if_end));
        }
    }

    public void turn_pass(View view) {
        p[0].is_pass = true;
        MyThread passing_card = new MyThread();
        passing_card.start();
    }

    public void save_neuron(View view) throws IOException {
        String filepath = "";
        if (char_li.contains("robot_full_sigmoid")) {
            filepath = FILE_PATH;
        } else if (char_li.contains("robot_full_relu")) {
            filepath = FILE_PATH_Relu;
        } else if (char_li.contains("robot_select")) {
            filepath = FILE_PATH_NNBSelect;
        } else if (char_li.contains("robot_manyneurons")) {
            filepath = FILE_PATH_manyneurons;
        }
        ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(filepath));
        file_param.writeObject(p[0].algorhythm_to_choose_card.rtn_nn());
        file_param.close();
    }
}