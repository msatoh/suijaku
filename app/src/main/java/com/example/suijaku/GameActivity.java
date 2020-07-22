package com.example.suijaku;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;

enum mark{heart,spade,dia,club};

class SelectedCardList{
    ArrayList<Integer> card_id_for_txtview= new ArrayList<>();
    ArrayList<Card> select_card=new ArrayList<Card>();
}

class Player{
    String name;
    ArrayList<Card> card_lis= new ArrayList<>();
    SelectedCardList players_select_card_lis=new SelectedCardList();
    Brain algorhythm_to_choose_card;
    boolean if_end=false;
    boolean is_pass=false;
    public ArrayList<Card> choose_card(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field){
        return algorhythm_to_choose_card.calculate_card_to_put(card_player1, card_player2, card_player3, card_player4, mycard,  card_field);
    }
    public void set_brain(Brain brain_in){
        algorhythm_to_choose_card=brain_in;
    }
    public void insert_card(Card card_in){
        int pos;
        if(card_lis.size()==0){
            card_lis.add(card_in);
            return;
        }else{
            for(pos=0;pos<card_lis.size();pos++){
                if(card_in.rtn_strength()<card_lis.get(pos).rtn_strength()){
                    card_lis.add(pos,card_in);
                    return;
                }
            }
            card_lis.add(card_in);
            return;
        }
    }
}

class Card{
    private mark mark_use;
    private String num;
    private int strength;
    public int rtn_strength(){
        return strength;
    }
    public mark rtn_mark(){
        return mark_use;
    }
    public String rtn_num(){
        return num;
    }
    public void give_mark(mark mark_in) {
        mark_use = mark_in;
    }
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
    int iterator=0;
    String rank[]={"","大富豪","富豪","平民","貧民","大貧民"};
    public String set_rank(){
        iterator++;
        return rank[iterator];
    }
}

class Field{
    TextView txt;
    ArrayList<Card> field_card=new ArrayList<Card>();
    public ArrayList<Card> rtn_value(){
        return field_card;
    }
    public void give_val(ArrayList<Card> cards_in){
        field_card= (ArrayList<Card>) cards_in.clone();
    }
    public TextView rtn_txtview(){
        return txt;
    }
    public void give_txtview(TextView txtview_in){
        txt=txtview_in;
    }
}

public class GameActivity extends AppCompatActivity {
    final int TRASH_TIME = 170;
    final int THINKING_TIME = 370;
    final Field field_entity = new Field();
    final Player pus[] = new Player[NUM_OF_PLAYERS];
    final ArrayList<TextView> player_card = new ArrayList<>();
    final TextView[] com_card = new TextView[NUM_OF_PLAYERS];
    final TextView[] pus_name = new TextView[NUM_OF_PLAYERS];
    final ImageView[] com_turn = new ImageView[NUM_OF_PLAYERS];
    final TextView[] pus_status = new TextView[NUM_OF_PLAYERS];
    ImageView player_turn;
    Handler pass_card;
    Handler trash_card;
    final Rank rank_use = new Rank();
    private ArrayList<Integer> used_lis = new ArrayList<Integer>();

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
        used_lis.remove(used_lis.indexOf(random_param));
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
        a_card.give_mark(mark_in);
        a_card.give_strength(strength_num);
        return a_card;
    }

    private String show_card(Card card_in) {
        String mark_disp;
        switch (card_in.rtn_mark()) {
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
                throw new IllegalStateException("Unexpected value: " + card_in.rtn_mark());
        }
        mark_disp.concat(card_in.rtn_num());

        return "[" + mark_disp + card_in.rtn_num() + "]";
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
            pus[cnt].is_pass=false;
            pus_status[cnt].setText("");
        }
    }

    class MyThread extends Thread {
        Button btn_pass = findViewById(R.id.pass);
        TextView rank_view = findViewById(R.id.player_card_in_hand_0);
        Check checker=new Check();
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
                                        pus[0].if_end=true;
                                        rank_view.setText(rank_use.set_rank());
                                    }
                                } else {
                                    com_turn[finalperson_num - 1].setVisibility(View.INVISIBLE);
                                }
                                com_turn[finalperson_num].setVisibility(View.VISIBLE);
                                if ((pus[(finalperson_num + 1) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 1) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end)) {
                                    field_entity.rtn_txtview().setText("");
                                    field_entity.rtn_value().clear();
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
                                    chosen_card[0] = pus[finalperson_num].choose_card(pus[(finalperson_num + 1) % 5].card_lis.size(), pus[(finalperson_num + 2) % 5].card_lis.size(), pus[(finalperson_num + 3) % 5].card_lis.size(), pus[(finalperson_num + 4) % 5].card_lis.size(), pus[finalperson_num].card_lis, field_entity.rtn_value());
                                    if (checker.chk_if_decideable(chosen_card[0],field_entity.rtn_value())) {
                                        for (inner_person_num[0] = 0; inner_person_num[0] < chosen_card[0].size(); inner_person_num[0]++) {
                                            pus[finalperson_num].card_lis.remove(pus[finalperson_num].card_lis.indexOf(chosen_card[0].get(inner_person_num[0])));
                                        }
                                        field_entity.rtn_txtview().setText(show_cards(chosen_card[0]));
                                        field_entity.give_val(chosen_card[0]);
                                        if (pus[finalperson_num].card_lis.size() == 0 && !pus[finalperson_num].if_end) {
                                            pus[finalperson_num].if_end=true;
                                            com_card[finalperson_num].setText(rank_use.set_rank());
                                        } else {
                                            com_card[finalperson_num].setText("" + pus[finalperson_num].card_lis.size() + "枚");
                                        }
                                    } else {
                                        pus[finalperson_num].is_pass=true;
                                        pus_status[finalperson_num].setText("pass");
                                    }
                                }
                                if (finalperson_num == 4) {
                                    com_turn[finalperson_num].setVisibility(View.INVISIBLE);
                                    player_turn.setVisibility(View.VISIBLE);
                                    btn_pass.setVisibility(View.VISIBLE);
                                    if ((pus[(finalperson_num + 2) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 2) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 3) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 3) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num + 4) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num + 4) % NUM_OF_PLAYERS].if_end) && (pus[(finalperson_num) % NUM_OF_PLAYERS].is_pass || pus[(finalperson_num) % NUM_OF_PLAYERS].if_end)) {
                                        field_entity.rtn_txtview().setText("");
                                        field_entity.rtn_value().clear();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.os.Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        int cnt;
        pass_card = new Handler();
        trash_card = new Handler();
        field_entity.give_txtview((TextView) findViewById(R.id.field));
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
                pus[cnt].name="user";
            } else {
                pus[cnt].name="COM" + cnt;
            }
        }
        pus[0].name="Masato";
        try {
            pus[3].set_brain(new NNBrain());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        pus[3].name="ニューラルネットワーク";
        pus[4].set_brain(new BasicBrain());
        pus[4].name="ふつう";
        pus[1].name="ザコ";
        pus[2].name="強い";
        pus[1].set_brain(new BasicBrain());
        pus[2].set_brain(new StrongerBrain());
        for (cnt = 0; cnt < NUM_OF_PLAYERS; cnt++) {
            pus_name[cnt].setText(pus[cnt].name);
        }
        for (cnt = 0; cnt < NUM_OF_CARDS; cnt++) {
            pus[cnt % 5].insert_card(gen_random_card());
        }
        final boolean[] clicked = new boolean[pus[0].card_lis.size()];
        for (cnt = 0; cnt < pus[0].card_lis.size(); cnt++) {
            clicked[cnt] = false;
            player_card.get(cnt).setText(show_card(((Card) pus[0].card_lis.get(cnt))));
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
                        pus[0].players_select_card_lis.select_card.add((Card) pus[0].card_lis.get(finalcnt));
                        pus[0].players_select_card_lis.card_id_for_txtview.add(pus[0].card_lis.indexOf(pus[0].card_lis.get(finalcnt)));
                    } else {
                        player_card.get(finalcnt).setTextColor(Color.BLACK);
                        player_card.get(finalcnt).setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt] = false;
                        pus[0].players_select_card_lis.select_card.remove(pus[0].card_lis.get(finalcnt));
                        pus[0].players_select_card_lis.card_id_for_txtview.remove(pus[0].players_select_card_lis.card_id_for_txtview.indexOf(finalcnt));
                    }
                }
            });
            player_card.get(cnt).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Check checker = new Check();
                    int localcnt, inner_localcnt;
                    if (checker.chk_if_decideable(pus[0].players_select_card_lis.select_card, field_entity.rtn_value())) {
                        Toast.makeText(getApplicationContext(), pus[0].players_select_card_lis.select_card.equals(pus[3].choose_card(pus[1].card_lis.size(),pus[2].card_lis.size(),pus[3].card_lis.size(),pus[4].card_lis.size(),pus[0].card_lis,field_entity.rtn_value()))?"○":"×"+show_cards(pus[3].choose_card(pus[1].card_lis.size(),pus[2].card_lis.size(),pus[3].card_lis.size(),pus[4].card_lis.size(),pus[0].card_lis,field_entity.rtn_value())), Toast.LENGTH_SHORT).show();
                        //back_propagation(pus[1].card_lis.size(),pus[2].card_lis.size(),pus[3].card_lis.size(),pus[4].card_lis.size(),pus[0].card_lis,field_entity.rtn_value(),pus[0].players_select_card_lis.select_card);
                        field_entity.rtn_txtview().setText(show_cards(pus[0].players_select_card_lis.select_card));
                        field_entity.give_val((pus[0].players_select_card_lis.select_card));
                        for (localcnt = 0; localcnt < pus[0].players_select_card_lis.card_id_for_txtview.size(); localcnt++) {
                            player_card.remove((Integer) pus[0].players_select_card_lis.card_id_for_txtview.get(localcnt));
                        }
                        pus[0].players_select_card_lis.card_id_for_txtview.clear();
                        for (inner_localcnt = 0; inner_localcnt < pus[0].players_select_card_lis.select_card.size(); inner_localcnt++) {
                            pus[0].card_lis.remove(pus[0].players_select_card_lis.select_card.get(inner_localcnt));
                        }
                        for (inner_localcnt = 0; inner_localcnt < pus[0].card_lis.size(); inner_localcnt++) {
                            player_card.get(inner_localcnt).setText(show_card(((Card) pus[0].card_lis.get(inner_localcnt))));
                            clicked[inner_localcnt] = false;
                            player_card.get(inner_localcnt).setTextColor(Color.BLACK);
                            player_card.get(inner_localcnt).setTypeface(Typeface.DEFAULT);
                        }
                        for (inner_localcnt = pus[0].card_lis.size(); inner_localcnt < pus[0].card_lis.size() + pus[0].players_select_card_lis.select_card.size(); inner_localcnt++) {
                            player_card.get(inner_localcnt).setText("");
                        }
                        pus[0].players_select_card_lis.select_card.clear();

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

    public void turn_pass(View view) {
        pus[0].is_pass=true;
        MyThread passing_card = new MyThread();
        passing_card.start();
    }
    public void save_neuron(View view) throws IOException {
        ObjectOutputStream file_param=new ObjectOutputStream(new FileOutputStream("/data/data/com.example.suijaku/files/newron_param.bin"));
        file_param.writeObject(pus[3].algorhythm_to_choose_card.rtn_nn());
        file_param.close();
    }
}