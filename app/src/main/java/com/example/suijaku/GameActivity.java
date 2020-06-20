package com.example.suijaku;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

enum mark{heart,spade,dia,club};

class SelectedCardList{
    private ArrayList<Integer> card_id_for_txtview= new ArrayList<>();
    private ArrayList<Card> selected_card=new ArrayList<Card>();
    public ArrayList rtn_selected_card(){
        return selected_card;
    }
    public ArrayList rtn_card_id_for_txtview(){
        return card_id_for_txtview;
    }
}

class Player{
    private String name;
    private ArrayList<Card> card_lis=new ArrayList<Card>();
    private SelectedCardList players_selected_card_lis=new SelectedCardList();
    public void set_name(String name_in){
        name=name_in;
    }
    public ArrayList show_and_lis(){
        return card_lis;
    }
    public SelectedCardList rtn_players_selected_card_lis(){
        return players_selected_card_lis;
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

class Field{
    TextView txt;
    ArrayList<Card> field_card=new ArrayList<Card>();
    public ArrayList<Card> rtn_value(){
        return field_card;
    }
    public void give_value(ArrayList<Card> cards_in){
        field_card=cards_in;
    }
    public TextView rtn_txtview(){
        return txt;
    }
    public void give_txtview(TextView txtview_in){
        txt=txtview_in;
    }
}

public class GameActivity extends AppCompatActivity {
    final int num_OF_CARDS=52;
    final int num_OF_PLAYERS=5;

    private ArrayList<Integer> used_lis=new ArrayList<Integer>();

    private void init_array(ArrayList<Integer> used_lis_in){
        int cnt;
        for(cnt=0;cnt<num_OF_CARDS;cnt++) {
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
                mark_disp="◆";
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

    private boolean check_if_decideable(ArrayList<Card> selected_card_in,ArrayList<Card> field_card_in){
        int localcnt=0;
        if(field_card_in.size()!=0) {
            if (selected_card_in.size() != field_card_in.size()) {
                return false;
            }
        }
            for (localcnt = 1; localcnt < selected_card_in.size(); localcnt++) {
                if (selected_card_in.get(localcnt - 1).rtn_strength() != selected_card_in.get(localcnt).rtn_strength()) {
                    return false;
                }
            }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        String[] id_name= new String[num_OF_CARDS/num_OF_PLAYERS+1];
        String[] com_name=new String[num_OF_PLAYERS];
        int cnt;
        final TextView[] player_card=new TextView[num_OF_CARDS/num_OF_PLAYERS+1];
        TextView[] com_card=new TextView[num_OF_PLAYERS];
        final Field field_entity=new Field();
        field_entity.give_txtview((TextView) findViewById(getResources().getIdentifier("field","id",getPackageName())));
        for(cnt=0;cnt<num_OF_CARDS/num_OF_PLAYERS+1;cnt++) {
            id_name[cnt]="player_card_in_hand_"+cnt;
            player_card[cnt] = findViewById(getResources().getIdentifier(id_name[cnt], "id", getPackageName()));
        }
        for(cnt=1;cnt<num_OF_PLAYERS;cnt++){
            com_name[cnt]="COM"+cnt+"_card";
            com_card[cnt]=findViewById(getResources().getIdentifier(com_name[cnt],"id",getPackageName()));
        }

        init_array(used_lis);
        final Player man[]=new Player[num_OF_PLAYERS];
        for(cnt=0;cnt<num_OF_PLAYERS;cnt++){
            man[cnt]=new Player();
        }
        man[0].set_name("user");
        for(cnt=1;cnt<num_OF_PLAYERS;cnt++){
            man[cnt].set_name("COM"+cnt);
        }
        final boolean[] clicked=new boolean[num_OF_CARDS/num_OF_PLAYERS+1];
        for(cnt=0;cnt<num_OF_CARDS;cnt++) {
            man[cnt % 5].show_and_lis().add(gen_random_card());
        }
        for(cnt=0;cnt<num_OF_CARDS/num_OF_PLAYERS+1;cnt++){
            clicked[cnt] = false;
            player_card[cnt].setText(show_card(((Card) man[0].show_and_lis().get(cnt))));
        }
        for(cnt=1;cnt<num_OF_PLAYERS;cnt++){
            com_card[cnt].setText(""+man[cnt].show_and_lis().size()+"枚");
        }
        for(cnt=0;cnt<num_OF_CARDS/num_OF_PLAYERS+1;cnt++) {
            final int finalcnt = cnt;
            player_card[cnt].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!clicked[finalcnt]) {
                        player_card[finalcnt].setTextColor(Color.BLUE);
                        player_card[finalcnt].setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalcnt]=true;
                        man[0].rtn_players_selected_card_lis().rtn_selected_card().add((Card) man[0].show_and_lis().get(finalcnt));
                        man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().add(finalcnt);
                    }else {
                        player_card[finalcnt].setTextColor(Color.BLACK);
                        player_card[finalcnt].setTypeface(Typeface.DEFAULT);
                        clicked[finalcnt]=false;
                        man[0].rtn_players_selected_card_lis().rtn_selected_card().remove((Card) man[0].show_and_lis().get(finalcnt));
                        man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().remove(man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().indexOf(finalcnt));
                    }
                }
            });
            player_card[cnt].setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    int localcnt;
                    if (check_if_decideable(man[0].rtn_players_selected_card_lis().rtn_selected_card(),field_entity.rtn_value())) {
                        field_entity.rtn_txtview().setText(show_cards(man[0].rtn_players_selected_card_lis().rtn_selected_card()));
                        field_entity.give_value((man[0].rtn_players_selected_card_lis().rtn_selected_card()));
                        for(localcnt=0;localcnt<man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().size();localcnt++) {
                            player_card[(int) man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().get(localcnt)].setText("");
                        }

                        man[0].rtn_players_selected_card_lis().rtn_selected_card().clear();
                        man[0].rtn_players_selected_card_lis().rtn_card_id_for_txtview().clear();
                    } else {
                        Toast.makeText(getApplicationContext(), "出せません！", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }}


}
