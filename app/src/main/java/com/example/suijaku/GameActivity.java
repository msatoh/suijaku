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

import java.util.ArrayList;
import java.util.Random;

enum mark{heart,spade,dia,club};

class SelectedCardList{
    private ArrayList<Integer> card_id_for_txtview= new ArrayList<>();
    private ArrayList<Card> select_card=new ArrayList<Card>();
    public ArrayList rtn_select_card(){
        return select_card;
    }
    public ArrayList rtn_card_id_for_txtview(){
        return card_id_for_txtview;
    }
}

class Player{
    private String name;
    private ArrayList<Card> card_lis= new ArrayList<>();
    private SelectedCardList players_select_card_lis=new SelectedCardList();
    private Brain algorhythm_to_choose_card=new Brain();
    private boolean if_end=false;
    private boolean is_pass=false;
    public ArrayList<Card> choose_card(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field){
        return algorhythm_to_choose_card.calculate_card_to_put(card_player1, card_player2, card_player3, card_player4, mycard,  card_field);
    }
    public void set_name(String name_in){
        name=name_in;
    }
    public String rtn_name(){return name;}
    public ArrayList show_and_lis(){
        return card_lis;
    }
    public SelectedCardList rtn_players_select_card_lis(){
        return players_select_card_lis;
    }
    public boolean if_pass(){
        return is_pass;
    }
    public void reg_pass(){
        is_pass=true;
    }
    public void reset_pass(){
        is_pass=false;
    }
    public boolean is_end(){return if_end;}
    public void reg_end(){
        if_end=true;
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
    public void give_value(ArrayList<Card> cards_in){
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
    final int NUM_OF_CARDS=52;
    final int NUM_OF_PLAYERS=5;
    final int TRASH_TIME=170;
    final int THINKING_TIME=370;
    final Field field_entity=new Field();
    final Player pussy[]=new Player[NUM_OF_PLAYERS];
    final TextView[] player_card=new TextView[NUM_OF_CARDS/NUM_OF_PLAYERS+1];
    final TextView[] com_card=new TextView[NUM_OF_PLAYERS];
    final TextView[] pussy_name=new TextView[NUM_OF_PLAYERS];
    final ImageView[] com_turn=new ImageView[NUM_OF_PLAYERS];
    final TextView[] pussy_status=new TextView[NUM_OF_PLAYERS];
    ImageView player_turn;
    Handler pass_card;
    Handler trash_card;
    final Rank rank_use=new Rank();
    private ArrayList<Integer> used_lis=new ArrayList<Integer>();

    private void init_array(ArrayList<Integer> used_lis_in){
        int cnt;
        for(cnt=0;cnt<NUM_OF_CARDS;cnt++) {
            used_lis_in.add(cnt);
        }
    }

    private Card gen_random_card(){
        mark mark_in;
        Card a_card=new Card();
        Random mark_num_seed=new Random();
        int random_seed=mark_num_seed.nextInt(used_lis.size());
        int random_param=used_lis.get(random_seed);
        int mark_num=random_param/13;
        int strength_num=random_param%13+1;
        used_lis.remove(used_lis.indexOf(random_param));
        switch(mark_num) {
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

    private String show_card(Card card_in){
        String mark_disp;
        switch(card_in.rtn_mark()){
            case heart:
                mark_disp="♥";
                break;
            case spade:
                mark_disp="♠";
                break;
            case dia:
                mark_disp="♦";
                break;
            case club:
                mark_disp="♣";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + card_in.rtn_mark());
        }mark_disp.concat(card_in.rtn_num());

        return "["+mark_disp+card_in.rtn_num()+"]";
    }
    private String show_cards(ArrayList<Card> cards_in){
        int localcnt;
        String cards_disp="";
        for(localcnt=0;localcnt<cards_in.size();localcnt++) {
            cards_disp+=show_card(cards_in.get(localcnt));
        }
        return cards_disp;
    }

    private void reset_all_pass(){
        int cnt;
        for(cnt=0;cnt<NUM_OF_PLAYERS;cnt++){
            pussy[cnt].reset_pass();
            pussy_status[cnt].setText("");
        }
    }

    class MyThread extends Thread {
        Button button_pass=findViewById(R.id.pass);
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
                    if(!pussy[person_num].is_end()) {
                        pass_card.post(new Runnable() {
                            public void run() {
                                button_pass.setVisibility(View.INVISIBLE);
                                if (finalperson_num == 1) {
                                    player_turn.setVisibility(View.INVISIBLE);
                                    if (pussy[0].if_pass()) {
                                        pussy_status[0].setText("pass");
                                    }
                                    if(pussy[0].show_and_lis().size()==0){
                                        pussy[0].reg_end();
                                        player_card[0].setText(rank_use.set_rank());
                                    }
                                } else {
                                    com_turn[finalperson_num - 1].setVisibility(View.INVISIBLE);
                                }
                                com_turn[finalperson_num].setVisibility(View.VISIBLE);
                                if ((pussy[(finalperson_num+1) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num+1) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num + 2) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 2) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num + 3) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 3) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num + 4) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 4) % NUM_OF_PLAYERS].is_end() )) {
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
                                if (!pussy[finalperson_num].if_pass()&&!pussy[finalperson_num].is_end()) {
                                    chosen_card[0] = pussy[finalperson_num].choose_card(pussy[(finalperson_num + 1) % 5].show_and_lis().size(), pussy[(finalperson_num + 2) % 5].show_and_lis().size(), pussy[(finalperson_num + 3) % 5].show_and_lis().size(), pussy[(finalperson_num + 4) % 5].show_and_lis().size(), pussy[finalperson_num].show_and_lis(), field_entity.rtn_value());
                                    if (chosen_card[0].size() > 0) {
                                        for (inner_person_num[0] = 0; inner_person_num[0] < chosen_card[0].size(); inner_person_num[0]++) {
                                            pussy[finalperson_num].show_and_lis().remove(pussy[finalperson_num].show_and_lis().indexOf(chosen_card[0].get(inner_person_num[0])));
                                        }
                                        field_entity.rtn_txtview().setText(show_cards(chosen_card[0]));
                                        field_entity.give_value(chosen_card[0]);
                                        if (pussy[finalperson_num].show_and_lis().size() == 0) {
                                            pussy[finalperson_num].reg_end();
                                            com_card[finalperson_num].setText(rank_use.set_rank());
                                        } else {
                                            com_card[finalperson_num].setText("" + pussy[finalperson_num].show_and_lis().size() + "枚");
                                        }
                                    } else {
                                        pussy[finalperson_num].reg_pass();
                                        pussy_status[finalperson_num].setText("pass");
                                    }
                                }
                                if (finalperson_num == 4) {
                                    com_turn[finalperson_num].setVisibility(View.INVISIBLE);
                                    player_turn.setVisibility(View.VISIBLE);
                                    button_pass.setVisibility(View.VISIBLE);
                                    if ((pussy[(finalperson_num + 2) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 2) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num + 3) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 3) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num + 4) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num + 4) % NUM_OF_PLAYERS].is_end() )&&(pussy[(finalperson_num) % NUM_OF_PLAYERS].if_pass()||pussy[(finalperson_num) % NUM_OF_PLAYERS].is_end() )){
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
            }while(pussy[0].if_pass()||pussy[0].is_end()||(pussy[1].is_end()&&pussy[2].is_end()&&pussy[3].is_end()&&pussy[4].is_end()));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        int cnt;
        pass_card =new Handler();
        trash_card=new Handler();
        field_entity.give_txtview((TextView) findViewById(R.id.field));
        this.player_turn=findViewById(R.id.PLAYER_turn);
        for(cnt=0;cnt<NUM_OF_CARDS/NUM_OF_PLAYERS+1;cnt++) {
            player_card[cnt] = findViewById(getResources().getIdentifier("player_card_in_hand_"+cnt, "id", getPackageName()));
        }
        for(cnt=1;cnt<NUM_OF_PLAYERS;cnt++){
            com_card[cnt]=findViewById(getResources().getIdentifier("COM"+cnt+"_card","id",getPackageName()));
            com_turn[cnt]=findViewById(getResources().getIdentifier("COM"+cnt+"_turn","id",getPackageName()));
        }

        init_array(used_lis);
        for(cnt=0;cnt<NUM_OF_PLAYERS;cnt++){
            pussy[cnt]=new Player();
            pussy_name[cnt]=findViewById(getResources().getIdentifier("man"+cnt+"_name","id",getPackageName()));
            pussy_status[cnt]=findViewById(getResources().getIdentifier("man"+cnt+"_status","id",getPackageName()));
            pussy_status[cnt].setText("");
            if(cnt==0){
                pussy[0].set_name("user");
            }else{
                pussy[cnt].set_name("COM"+cnt);
            }
            pussy_name[cnt].setText(pussy[cnt].rtn_name());
        }

        final boolean[] clicked=new boolean[NUM_OF_CARDS/NUM_OF_PLAYERS+1];
        for(cnt=0;cnt<NUM_OF_CARDS;cnt++) {
            pussy[cnt % 5].show_and_lis().add(gen_random_card());
        }
        for(cnt=0;cnt<NUM_OF_CARDS/NUM_OF_PLAYERS+1;cnt++){
            clicked[cnt] = false;
            player_card[cnt].setText(show_card(((Card) pussy[0].show_and_lis().get(cnt))));
        }
        for(cnt=1;cnt<NUM_OF_PLAYERS;cnt++){
            com_card[cnt].setText(""+pussy[cnt].show_and_lis().size()+"枚");
        }
        for(cnt=0;cnt<NUM_OF_CARDS/NUM_OF_PLAYERS+1;cnt++) {
            final int finalcnt = cnt;
            player_card[cnt].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!clicked[finalcnt]) {
                        player_card[finalcnt].setTextColor(Color.BLUE);
                        player_card[finalcnt].setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalcnt]=true;
                        pussy[0].rtn_players_select_card_lis().rtn_select_card().add((Card) pussy[0].show_and_lis().get(finalcnt));
                        pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().add(finalcnt);
                    }else {
                        player_card[finalcnt].setTextColor(Color.BLACK);
                        player_card[finalcnt].setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt]=false;
                        pussy[0].rtn_players_select_card_lis().rtn_select_card().remove((Card) pussy[0].show_and_lis().get(finalcnt));
                        pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().remove(pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().indexOf(finalcnt));
                    }
                }
            });
            player_card[cnt].setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    Check checker=new Check();
                    int localcnt,inner_localcnt;
                    if (checker.check_if_decideable(pussy[0].rtn_players_select_card_lis().rtn_select_card(),field_entity.rtn_value())) {
                        field_entity.rtn_txtview().setText(show_cards(pussy[0].rtn_players_select_card_lis().rtn_select_card()));
                        field_entity.give_value((pussy[0].rtn_players_select_card_lis().rtn_select_card()));
                        for(localcnt=0;localcnt<pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().size();localcnt++) {
                            player_card[(int) pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().get(localcnt)].setText("");
                        }
                        pussy[0].rtn_players_select_card_lis().rtn_select_card().clear();
                        pussy[0].rtn_players_select_card_lis().rtn_card_id_for_txtview().clear();
                        for(inner_localcnt=0;inner_localcnt<pussy[0].rtn_players_select_card_lis().rtn_select_card().size();inner_localcnt++) {
                            pussy[0].show_and_lis().remove(pussy[0].rtn_players_select_card_lis().rtn_select_card().indexOf(pussy[0].rtn_players_select_card_lis().rtn_select_card().get(inner_localcnt)));
                        }
                        MyThread passing_card=new MyThread();
                        passing_card.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "出せません！", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }
    public void turn_pass(View view){
        pussy[0].reg_pass();
        MyThread passing_card = new MyThread();
        passing_card.start();

    }


}