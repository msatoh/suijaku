package com.example.suijaku;

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

enum mark {heart, spade, dia, club}

class SelectedCardList{
    ArrayList<Integer> card_id_for_txtview= new ArrayList<>();
    ArrayList<Card> sel_card=new ArrayList<>();
}

class Player{
    String name;
    ArrayList<Card> card_li= new ArrayList<>();
    SelectedCardList players_sel_card_li=new SelectedCardList();
    Brain algorhythm_to_choose_card;
    boolean if_end=false;
    boolean is_pass=false;
    public void insert_card(Card card_in){
        int pos;
        if(card_li.size()==0){
            card_li.add(card_in);
            return;
        }else{
            for(pos=0;pos<card_li.size();pos++){
                if(card_in.strength<card_li.get(pos).strength){
                    card_li.add(pos,card_in);
                    return;
                }
            }
            card_li.add(card_in);
            return;
        }
    }
}
class Card{
    mark mark_use;
    String num;
    int strength;
    public void give_strength(int strength_in) {
        int num_before_conv;
        strength = strength_in;
        num_before_conv = (strength_in + 14) % 13 + 1;
        switch (num_before_conv) {
            case 11:
                num = "J";
                break;
            case 12:
                num = "Q";
                break;
            case 13:
                num = "K";
                break;
            case 1:
                num = "A";
                break;
            default:
                num = String.valueOf(num_before_conv);
                break;
        }
    }

}
class Rank{
    int iterator = 0;
    String[] rank = {"", "大富豪", "富豪", "平民", "貧民", "大貧民"};
    public String set_rank(){
        iterator++;
        return rank[iterator];
    }
}
class Field{
    TextView txt;
    ArrayList<Card> field_card=new ArrayList<>();
}

public class GameActivity extends AppCompatActivity {
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
        a_card.mark_use=mark_in;
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
            p[cnt].is_pass=false;
            p_stat[cnt].setText("");
        }
    }

    private void assign_com(ArrayList<String> char_li) throws IOException, ClassNotFoundException {
        int cnt=1;
        if(char_li.contains("robot_select")) {
            p[cnt].algorhythm_to_choose_card = new NNBrain_Select();
            p[cnt].name="NNselect";
            cnt++;
        }
        if(char_li.contains("robot_full_relu")){
            p[cnt].algorhythm_to_choose_card = new NNBrain_ReLu();
            p[cnt].name="ReLu子";
            cnt++;
        }
        if(char_li.contains("robot_full_sigmoid")){
            p[cnt].algorhythm_to_choose_card = new NNBrain();
            p[cnt].name="ニューラルネットワーク";
            cnt++;
        }
        if(char_li.contains("zako")){
            p[cnt].algorhythm_to_choose_card=new BasicBrain();
            p[cnt].name="ザコ";
            cnt++;
        }
        if(char_li.contains("strong")){
            p[cnt].algorhythm_to_choose_card=new StrongerBrain();
            p[cnt].name="強い";
            cnt++;
        }
        if(char_li.contains("robot_manyneurons")){
            p[cnt].algorhythm_to_choose_card=new NNBrain_manyneurons();
            p[cnt].name="神経増やしたシグモイド";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent get_intent = getIntent();
        ArrayList<String> char_li = get_intent.getStringArrayListExtra("selected_char_li");
        android.os.Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        int cnt;
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
        }
        p[0].name="Masato";
        try {
            assign_com(char_li);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                    int localcnt;
                    if (checker.chk_if_decideable(p[0].players_sel_card_li.sel_card, field_entity.field_card)) {
//                        Toast.makeText(getApplicationContext(), p[0].players_sel_card_li.sel_card.equals(p[3].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(),p[2].card_li.size(),p[3].card_li.size(),p[4].card_li.size(),p[0].card_li,field_entity.field_card))?"○":"×"+show_cards(p[3].algorhythm_to_choose_card.calc_card_to_put(p[1].card_li.size(),p[2].card_li.size(),p[3].card_li.size(),p[4].card_li.size(),p[0].card_li,field_entity.field_card)), Toast.LENGTH_SHORT).show();
//                        p[3].algorhythm_to_choose_card.back_propagation(p[1].card_li.size(),p[2].card_li.size(),p[3].card_li.size(),p[4].card_li.size(),p[0].card_li,field_entity.field_card,p[0].players_sel_card_li.sel_card);
                        field_entity.txt.setText(show_cards(p[0].players_sel_card_li.sel_card));
                        field_entity.field_card = (ArrayList<Card>) p[0].players_sel_card_li.sel_card.clone();
                        for (localcnt = 0; localcnt < p[0].players_sel_card_li.card_id_for_txtview.size(); localcnt++) {
                            p_card.remove(p[0].players_sel_card_li.card_id_for_txtview.get(localcnt));
                            p[0].card_li.remove(p[0].players_sel_card_li.sel_card.get(localcnt));
                        }
                        p[0].players_sel_card_li.card_id_for_txtview.clear();
                        for (localcnt = 0; localcnt < p[0].card_li.size(); localcnt++) {
                            p_card.get(localcnt).setText(show_card(p[0].card_li.get(localcnt)));
                            clicked[localcnt] = false;
                            p_card.get(localcnt).setTextColor(Color.BLACK);
                            p_card.get(localcnt).setTypeface(Typeface.DEFAULT);
                        }
                        for (localcnt = p[0].card_li.size(); localcnt < p[0].card_li.size() + p[0].players_sel_card_li.sel_card.size(); localcnt++) {
                            p_card.get(localcnt).setText("");
                        }
                        p[0].players_sel_card_li.sel_card.clear();

                        MyThread passing_card = new MyThread();
                        passing_card.start();
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
                                if (p[0].card_li.size() == 0&&!p[0].if_end) {
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
}