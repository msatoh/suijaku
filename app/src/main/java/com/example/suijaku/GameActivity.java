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
    ArrayList<Card> select_card=new ArrayList<>();
}

class Player{
    String name;
    ArrayList<Card> card_list= new ArrayList<>();
    SelectedCardList players_select_card_list=new SelectedCardList();
    Brain algorhythm_to_choose_card;
    boolean if_end=false;
    boolean is_pass=false;
    public void insert_card(Card card_in){
        int pos;
        if(card_list.size()==0){
            card_list.add(card_in);
            return;
        }else{
            for(pos=0;pos<card_list.size();pos++){
                if(card_in.strength<card_list.get(pos).strength){
                    card_list.add(pos,card_in);
                    return;
                }
            }
            card_list.add(card_in);
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
    final Player[] psn = new Player[NUM_OF_PLAYERS];
    final ArrayList<TextView> p_card = new ArrayList<>();
    final TextView[] com_card = new TextView[NUM_OF_PLAYERS];
    final TextView[] psn_name = new TextView[NUM_OF_PLAYERS];
    final ImageView[] com_turn = new ImageView[NUM_OF_PLAYERS];
    final TextView[] psn_stat = new TextView[NUM_OF_PLAYERS];
    ImageView p_turn;
    Handler pass_card;
    Handler trash_card;
    final Rank rank_use = new Rank();
    private ArrayList<Integer> used_list = new ArrayList<>();

    private void init_array(ArrayList<Integer> used_list_in) {
        int cnt;
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            used_list_in.add(cnt);
        }
    }

    private Card gen_rand_card() {
        mark mark_in;
        Card a_card = new Card();
        Random mark_num_seed = new Random();
        int rand_seed = mark_num_seed.nextInt(used_list.size());
        int rand_param = used_list.get(rand_seed);
        int mark_num = rand_param / 13;
        int strength_num = rand_param % 13 + 1;
        used_list.remove((Integer) rand_param);
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
            psn[cnt].is_pass=false;
            psn_stat[cnt].setText("");
        }
    }

    private void assign_com(ArrayList<String> char_list) throws IOException, ClassNotFoundException {
        int cnt=1;
        if(char_list.contains("robot_select")) {
            psn[cnt].algorhythm_to_choose_card = new NNBrain_Select();
            psn[cnt].name="NNselect";
            cnt++;
        }
        if(char_list.contains("robot_full_relu")){
            psn[cnt].algorhythm_to_choose_card = new NNBrain_ReLu();
            psn[cnt].name="ReLu子";
            cnt++;
        }
        if(char_list.contains("robot_full_sigmoid")){
            psn[cnt].algorhythm_to_choose_card = new NNBrain();
            psn[cnt].name="ニューラルネットワーク";
            cnt++;
        }
        if(char_list.contains("zako")){
            psn[cnt].algorhythm_to_choose_card=new BasicBrain();
            psn[cnt].name="ザコ";
            cnt++;
        }
        if(char_list.contains("strong")){
            psn[cnt].algorhythm_to_choose_card=new StrongerBrain();
            psn[cnt].name="強い";
        }
        if(char_list.contains("robot_manyneurons")){
            psn[cnt].algorhythm_to_choose_card=new NNBrain_manyneurons();
            psn[cnt].name="神経増やしたシグモイド";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent get_intent = getIntent();
        ArrayList<String> char_list = get_intent.getStringArrayListExtra("selected_char_list");
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
        init_array(used_list);
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            psn[cnt] = new Player();
            psn_name[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_name", "id", getPackageName()));
            psn_stat[cnt] = findViewById(getResources().getIdentifier("man" + cnt + "_stat", "id", getPackageName()));
            psn_stat[cnt].setText("");
        }
        psn[0].name="Masato";
        try {
            assign_com((ArrayList<String>) char_list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            psn_name[cnt].setText(psn[cnt].name);
        }
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            psn[cnt % 5].insert_card(gen_rand_card());
        }
        final boolean[] clicked = new boolean[psn[0].card_list.size()];
        for (cnt = 0; cnt < psn[0].card_list.size(); cnt++) {
            clicked[cnt] = false;
            p_card.get(cnt).setText(show_card(psn[0].card_list.get(cnt)));
        }
        for (cnt = 1; cnt < NUM_OF_PLAYERS; cnt++) {
            com_card[cnt].setText("" + psn[cnt].card_list.size() + "枚");
        }
        for (cnt = 0; cnt < NUM_OF_CARDS / NUM_OF_PLAYERS + 1; cnt++) {
            final int finalcnt = cnt;
            p_card.get(cnt).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!clicked[finalcnt]) {
                        p_card.get(finalcnt).setTextColor(Color.BLUE);
                        p_card.get(finalcnt).setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalcnt] = true;
                        psn[0].players_select_card_list.select_card.add(psn[0].card_list.get(finalcnt));
                        psn[0].players_select_card_list.card_id_for_txtview.add(psn[0].card_list.indexOf(psn[0].card_list.get(finalcnt)));
                    } else {
                        p_card.get(finalcnt).setTextColor(Color.BLACK);
                        p_card.get(finalcnt).setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt] = false;
                        psn[0].players_select_card_list.select_card.remove(psn[0].card_list.get(finalcnt));
                        psn[0].players_select_card_list.card_id_for_txtview.remove((Integer) finalcnt);
                    }
                }
            });
            p_card.get(cnt).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Check checker = new Check();
                    int localcnt;
                    if (checker.chk_if_decideable(psn[0].players_select_card_list.select_card, field_entity.field_card)) {
//                        Toast.makeText(getApplicationContext(), psn[0].players_select_card_list.select_card.equals(psn[3].algorhythm_to_choose_card.calculate_card_to_put(psn[1].card_list.size(),psn[2].card_list.size(),psn[3].card_list.size(),psn[4].card_list.size(),psn[0].card_list,field_entity.field_card))?"○":"×"+show_cards(psn[3].algorhythm_to_choose_card.calculate_card_to_put(psn[1].card_list.size(),psn[2].card_list.size(),psn[3].card_list.size(),psn[4].card_list.size(),psn[0].card_list,field_entity.field_card)), Toast.LENGTH_SHORT).show();
//                        psn[3].algorhythm_to_choose_card.back_propagation(psn[1].card_list.size(),psn[2].card_list.size(),psn[3].card_list.size(),psn[4].card_list.size(),psn[0].card_list,field_entity.field_card,psn[0].players_select_card_list.select_card);
                        field_entity.txt.setText(show_cards(psn[0].players_select_card_list.select_card));
                        field_entity.field_card = (ArrayList<Card>) psn[0].players_select_card_list.select_card.clone();
                        for (localcnt = 0; localcnt < psn[0].players_select_card_list.card_id_for_txtview.size(); localcnt++) {
                            p_card.remove(psn[0].players_select_card_list.card_id_for_txtview.get(localcnt));
                            psn[0].card_list.remove(psn[0].players_select_card_list.select_card.get(localcnt));
                        }
                        psn[0].players_select_card_list.card_id_for_txtview.clear();
                        for (localcnt = 0; localcnt < psn[0].card_list.size(); localcnt++) {
                            p_card.get(localcnt).setText(show_card(psn[0].card_list.get(localcnt)));
                            clicked[localcnt] = false;
                            p_card.get(localcnt).setTextColor(Color.BLACK);
                            p_card.get(localcnt).setTypeface(Typeface.DEFAULT);
                        }
                        for (localcnt = psn[0].card_list.size(); localcnt < psn[0].card_list.size() + psn[0].players_select_card_list.select_card.size(); localcnt++) {
                            p_card.get(localcnt).setText("");
                        }
                        psn[0].players_select_card_list.select_card.clear();

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
                    if (!psn[person_num].if_end) {
                        pass_card.post(new Runnable() {
                            public void run() {
                                btn_pass.setVisibility(View.INVISIBLE);
                                p_turn.setVisibility(View.INVISIBLE);
                                if (psn[0].is_pass) {
                                    psn_stat[0].setText("pass");
                                }
                                if (psn[0].card_list.size() == 0&&!psn[0].if_end) {
                                    psn[0].if_end = true;
                                    rank_view.setText(rank_use.set_rank());
                                }
                                if (finalperson_num != 1) {
                                    com_turn[finalperson_num - 1].setVisibility(View.INVISIBLE);
                                }
                                com_turn[finalperson_num].setVisibility(View.VISIBLE);
                                if ((psn[(finalperson_num + 1) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 1) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end)) {
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
                                if (!psn[finalperson_num].is_pass && !psn[finalperson_num].if_end) {
                                    chosen_card[0] = psn[finalperson_num].algorhythm_to_choose_card.calculate_card_to_put(psn[(finalperson_num + 1) % 5].card_list.size(), psn[(finalperson_num + 2) % 5].card_list.size(), psn[(finalperson_num + 3) % 5].card_list.size(), psn[(finalperson_num + 4) % 5].card_list.size(), psn[finalperson_num].card_list, field_entity.field_card);
                                    if (checker.chk_if_decideable(chosen_card[0], field_entity.field_card)) {
                                        for (inner_person_num[0] = 0; inner_person_num[0] < chosen_card[0].size(); inner_person_num[0]++) {
                                            psn[finalperson_num].card_list.remove(chosen_card[0].get(inner_person_num[0]));
                                        }
                                        field_entity.txt.setText(show_cards(chosen_card[0]));
                                        field_entity.field_card = chosen_card[0];
                                        if (psn[finalperson_num].card_list.size() == 0 && !psn[finalperson_num].if_end) {
                                            psn[finalperson_num].if_end = true;
                                            com_card[finalperson_num].setText(rank_use.set_rank());
                                        } else {
                                            com_card[finalperson_num].setText("" + psn[finalperson_num].card_list.size() + "枚");
                                        }
                                    } else {
                                        psn[finalperson_num].is_pass = true;
                                        psn_stat[finalperson_num].setText("pass");
                                    }
                                }
                                if (finalperson_num == 4) {
                                    com_turn[finalperson_num].setVisibility(View.INVISIBLE);
                                    p_turn.setVisibility(View.VISIBLE);
                                    btn_pass.setVisibility(View.VISIBLE);
                                    if ((psn[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end) && (psn[(finalperson_num) % NUM_OF_PLAYERS].is_pass || psn[(finalperson_num) % NUM_OF_PLAYERS].if_end)) {
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
            } while (psn[0].is_pass || psn[0].if_end || (psn[1].if_end && psn[2].if_end && psn[3].if_end && psn[4].if_end));
        }
    }

    public void turn_pass(View view) {
        psn[0].is_pass = true;
        MyThread passing_card = new MyThread();
        passing_card.start();
    }

    public void save_neuron(View view) throws IOException {
        ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
        file_param.writeObject(psn[3].algorhythm_to_choose_card.rtn_nn());
        file_param.close();
    }
}