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
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;

public class TrainActivity extends AppCompatActivity {
    final int TRASH_TIME = 170;
    final int THINKING_TIME = 370;
    final Field field_entity = new Field();
    final Player[] pus = new Player[NUM_OF_PLAYERS];
    final ArrayList<TextView> player_card = new ArrayList<>();
    final TextView[] com_card = new TextView[NUM_OF_PLAYERS];
    final TextView[] pus_name = new TextView[NUM_OF_PLAYERS];
    final ImageView[] com_turn = new ImageView[NUM_OF_PLAYERS];
    final TextView[] pus_status = new TextView[NUM_OF_PLAYERS];
    ImageView player_turn;
    Handler pass_card;
    Handler trash_card;
    final Rank rank_use = new Rank();
    private ArrayList<Integer> used_lis = new ArrayList<>();

    private void init_array(ArrayList<Integer> used_lis_in) {
        int cnt;
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            used_lis_in.add(cnt);
        }
    }

    private Card gen_random_card() {
        mark mark_in;
        Card a_card = new Card();
        Random mark_num_seed = new Random();
        int random_seed = mark_num_seed.nextInt(used_lis.size());
        int random_param = used_lis.get(random_seed);
        int mark_num = random_param / 13;
        int strength_num = random_param % 13 + 1;
        used_lis.remove((Integer) random_param);
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
            pus[cnt].is_pass = false;
            pus_status[cnt].setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent get_intent = getIntent();
        String char_list = get_intent.getStringExtra("selected_char_list");
        android.os.Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        int cnt;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        pass_card = new Handler();
        trash_card = new Handler();
        field_entity.txt = findViewById(R.id.field);
        this.player_turn = findViewById(R.id.PLAYER_turn);
        for (cnt = 0; cnt < NUM_OF_CARDS / NUM_OF_PLAYERS + 1; cnt++) {
            player_card.add((TextView) findViewById(getResources().getIdentifier("player_card_in_hand_" + cnt, "id", getPackageName())));
        }
        for (cnt = 1; cnt < NUM_OF_PLAYERS; cnt++) {
            com_card[cnt] = findViewById(getResources().getIdentifier("COM" + cnt + "_card", "id", getPackageName()));
            com_turn[cnt] = findViewById(getResources().getIdentifier("COM" + cnt + "_turn", "id", getPackageName()));
        }
        init_array(used_lis);
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            pus[cnt] = new Player();
            pus_name[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_name", "id", getPackageName()));
            pus_status[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_status", "id", getPackageName()));
            pus_status[cnt].setText("");
            if (cnt == 0) {
                pus[cnt].name = "user";
            } else {
                pus[cnt].name = "COM" + cnt;
            }
        }
        pus[0].name = "Masato";
        try {
            pus[0].algorhythm_to_choose_card=new NNBrain();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        pus[1].name = "ザコ";
        pus[1].algorhythm_to_choose_card = new BasicBrain();
        pus[2].name = "強い";
        pus[2].algorhythm_to_choose_card = new StrongerBrain();
        pus[3].algorhythm_to_choose_card = new StrongerBrain();
        pus[3].name = "ニューラルネットワーク";
        pus[4].algorhythm_to_choose_card = new BasicBrain();
        pus[4].name = "ふつう";
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            pus_name[cnt].setText(pus[cnt].name);
        }
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            pus[cnt % 5].insert_card(gen_random_card());
        }
        final boolean[] clicked = new boolean[pus[0].card_lis.size()];
        for (cnt = 0; cnt < pus[0].card_lis.size(); cnt++) {
            clicked[cnt] = false;
            player_card.get(cnt).setText(show_card(pus[0].card_lis.get(cnt)));
        }
        for (cnt = 1; cnt < NUM_OF_PLAYERS; cnt++) {
            com_card[cnt].setText("" + pus[cnt].card_lis.size() + "枚");
        }
        for (cnt = 0; cnt < NUM_OF_CARDS / NUM_OF_PLAYERS + 1; cnt++) {
            final int finalcnt = cnt;
            player_card.get(cnt).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!clicked[finalcnt]) {
                        player_card.get(finalcnt).setTextColor(Color.BLUE);
                        player_card.get(finalcnt).setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalcnt] = true;
                        pus[0].players_select_card_lis.select_card.add(pus[0].card_lis.get(finalcnt));
                        pus[0].players_select_card_lis.card_id_for_txtview.add(pus[0].card_lis.indexOf(pus[0].card_lis.get(finalcnt)));
                    } else {
                        player_card.get(finalcnt).setTextColor(Color.BLACK);
                        player_card.get(finalcnt).setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt] = false;
                        pus[0].players_select_card_lis.select_card.remove(pus[0].card_lis.get(finalcnt));
                        pus[0].players_select_card_lis.card_id_for_txtview.remove((Integer) finalcnt);
                    }
                }
            });
            player_card.get(cnt).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Check checker = new Check();
                    final int[] localcnt = new int[1];
                    if (checker.chk_if_decideable(pus[0].players_select_card_lis.select_card, field_entity.field_card)) {
                        builder.setTitle("COMの捨てたカード")
                                .setMessage(show_cards(pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card)))
                                .setPositiveButton("ラーン", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pus[0].algorhythm_to_choose_card.back_propagation(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card, pus[0].players_select_card_lis.select_card);
                                    }
                                })
                                .setNeutralButton("送る(自分の選択したカード)", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        field_entity.txt.setText(show_cards(pus[0].players_select_card_lis.select_card));
                                        field_entity.field_card = (ArrayList<Card>) pus[0].players_select_card_lis.select_card.clone();
                                        for (localcnt[0] = 0; localcnt[0] < pus[0].players_select_card_lis.card_id_for_txtview.size(); localcnt[0]++) {
                                            player_card.remove(pus[0].players_select_card_lis.card_id_for_txtview.get(localcnt[0]));
                                        }
                                        pus[0].players_select_card_lis.card_id_for_txtview.clear();
                                        for (localcnt[0] = 0; localcnt[0] < pus[0].players_select_card_lis.select_card.size(); localcnt[0]++) {
                                            pus[0].card_lis.remove(pus[0].players_select_card_lis.select_card.get(localcnt[0]));
                                        }
                                        for (localcnt[0] = 0; localcnt[0] < pus[0].card_lis.size(); localcnt[0]++) {
                                            player_card.get(localcnt[0]).setText(show_card(pus[0].card_lis.get(localcnt[0])));
                                            clicked[localcnt[0]] = false;
                                            player_card.get(localcnt[0]).setTextColor(Color.BLACK);
                                            player_card.get(localcnt[0]).setTypeface(Typeface.DEFAULT);
                                        }
                                        for (localcnt[0] = pus[0].card_lis.size(); localcnt[0] < pus[0].card_lis.size() + pus[0].players_select_card_lis.select_card.size(); localcnt[0]++) {
                                            player_card.get(localcnt[0]).setText("");
                                        }
                                        pus[0].players_select_card_lis.select_card.clear();
                                        MyThread passing_card = new MyThread();
                                        passing_card.start();
                                    }
                                })
                                .setNegativeButton("送る(COMの選択したカード)", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pus[0].players_select_card_lis.card_id_for_txtview.clear();
                                        field_entity.txt.setText(show_cards(pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card)));
                                        field_entity.field_card = (ArrayList<Card>) pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card).clone();
                                        for (localcnt[0] = 0; localcnt[0] < pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card).size(); localcnt[0]++) {
                                            player_card.remove(pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card).get(localcnt[0]));
                                            pus[0].card_lis.remove(pus[0].algorhythm_to_choose_card.calculate_card_to_put(pus[1].card_lis.size(), pus[2].card_lis.size(), pus[3].card_lis.size(), pus[4].card_lis.size(), pus[0].card_lis, field_entity.field_card).get(localcnt[0]));
                                        }
                                        for (localcnt[0] = 0; localcnt[0] < pus[0].card_lis.size(); localcnt[0]++) {
                                            player_card.get(localcnt[0]).setText(show_card(pus[0].card_lis.get(localcnt[0])));
                                            clicked[localcnt[0]] = false;
                                            player_card.get(localcnt[0]).setTextColor(Color.BLACK);
                                            player_card.get(localcnt[0]).setTypeface(Typeface.DEFAULT);
                                        }
                                        for (localcnt[0] = pus[0].card_lis.size(); localcnt[0] < pus[0].card_lis.size() + pus[0].players_select_card_lis.select_card.size(); localcnt[0]++) {
                                            player_card.get(localcnt[0]).setText("");
                                        }
                                        pus[0].players_select_card_lis.select_card.clear();
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
        TextView rank_view = findViewById(R.id.player_card_in_hand_0);
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
                    if (!pus[person_num].if_end) {
                        pass_card.post(new Runnable() {
                            public void run() {
                                btn_pass.setVisibility(View.INVISIBLE);
                                if (finalperson_num == 1) {
                                    player_turn.setVisibility(View.INVISIBLE);
                                    if (pus[0].is_pass) {
                                        pus_status[0].setText("pass");
                                    }
                                    if (pus[0].card_lis.size() == 0 && !pus[0].if_end) {
                                        pus[0].if_end = true;
                                        rank_view.setText(rank_use.set_rank());
                                    }
                                } else {
                                    com_turn[finalperson_num - 1].setVisibility(View.INVISIBLE);
                                }
                                com_turn[finalperson_num].setVisibility(View.VISIBLE);
                                if ((pus[(finalperson_num + 1) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 1) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end)) {
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
                                if (!pus[finalperson_num].is_pass && !pus[finalperson_num].if_end) {
                                    chosen_card[0] = pus[finalperson_num].algorhythm_to_choose_card.calculate_card_to_put(pus[(finalperson_num + 1) % 5].card_lis.size(), pus[(finalperson_num + 2) % 5].card_lis.size(), pus[(finalperson_num + 3) % 5].card_lis.size(), pus[(finalperson_num + 4) % 5].card_lis.size(), pus[finalperson_num].card_lis, field_entity.field_card);
                                    if (checker.chk_if_decideable(chosen_card[0], field_entity.field_card)) {
                                        for (inner_person_num[0] = 0; inner_person_num[0] < chosen_card[0].size(); inner_person_num[0]++) {
                                            pus[finalperson_num].card_lis.remove(chosen_card[0].get(inner_person_num[0]));
                                        }
                                        field_entity.txt.setText(show_cards(chosen_card[0]));
                                        field_entity.field_card = chosen_card[0];
                                        if (pus[finalperson_num].card_lis.size() == 0 && !pus[finalperson_num].if_end) {
                                            pus[finalperson_num].if_end = true;
                                            com_card[finalperson_num].setText(rank_use.set_rank());
                                        } else {
                                            com_card[finalperson_num].setText("" + pus[finalperson_num].card_lis.size() + "枚");
                                        }
                                    } else {
                                        pus[finalperson_num].is_pass = true;
                                        pus_status[finalperson_num].setText("pass");
                                    }
                                }
                                if (finalperson_num == 4) {
                                    com_turn[finalperson_num].setVisibility(View.INVISIBLE);
                                    player_turn.setVisibility(View.VISIBLE);
                                    btn_pass.setVisibility(View.VISIBLE);
                                    if ((pus[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num) % NUM_OF_PLAYERS].if_end)) {
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
            } while (pus[0].is_pass || pus[0].if_end || (pus[1].if_end && pus[2].if_end && pus[3].if_end && pus[4].if_end));
        }
    }

    public void turn_pass(View view) {
        pus[0].is_pass = true;
        MyThread passing_card = new MyThread();
        passing_card.start();
    }

    public void save_neuron(View view) throws IOException {
        ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
        file_param.writeObject(pus[3].algorhythm_to_choose_card.rtn_nn());
        file_param.close();
    }
}