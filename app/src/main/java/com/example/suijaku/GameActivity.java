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
    private ArrayList<Integer> card_id_for_textview= new ArrayList<>();
    private ArrayList<Card> selected_card=new ArrayList<Card>();
    public ArrayList return_selected_card(){
        return selected_card;
    }
    public ArrayList return_card_id_for_textwiew(){
        return card_id_for_textview;
    }
}

class Player{
    private String name;
    private ArrayList<Card> card_list=new ArrayList<Card>();
    private SelectedCardList players_selected_card_list;
    public void set_name(String name_in){
        name=name_in;
    }
    public void set_card(Card card_in){
        card_list.add(card_in);
    }
    public ArrayList show_and_list(){
        return card_list;
    }
    public SelectedCardList return_players_selected_card_list(){
        return players_selected_card_list;
    }
}

class Card{
    private mark mark_use;
    private String number;
    private int strength;
    public int return_strength(){
        return strength;
    }
    public mark return_mark(){
        return mark_use;
    }
    public String return_number(){
        return number;
    }
    public void give_mark(mark mark_in) {
        mark_use = mark_in;
    }
    public void give_strength(int strength_in) {
        int number_before_convertion;
        strength = strength_in;
        number_before_convertion = (strength_in + 14) % 13 + 1;
        switch (number_before_convertion) {
            case 11:
                number = "J";
                break;
            case 12:
                number = "Q";
                break;
            case 13:
                number = "K";
                break;
            case 1:
                number = "A";
                break;
            default:
                number = String.valueOf(number_before_convertion);
                break;
        }
    }

}

public class GameActivity extends AppCompatActivity {
    final int NUMBER_OF_CARDS=52;
    final int NUMBER_OF_PLAYERS=5;

    private ArrayList<Integer> already_used_list=new ArrayList<Integer>();

    private void initialize_array(ArrayList<Integer> already_used_list_in){
        int counter;
        for(counter=0;counter<NUMBER_OF_CARDS;counter++) {
            already_used_list_in.add(counter);
        }
    }

    private Card generate_random_card(){
        mark mark_in;
        Card one_card=new Card();
        Random mark_number_seed=new Random();
        int random_seed=mark_number_seed.nextInt(already_used_list.size());
        int random_parameter=already_used_list.get(random_seed);
        int mark_number=random_parameter/13;
        int strength_number=random_parameter%13+1;
        already_used_list.remove(already_used_list.indexOf(random_parameter));
        switch(mark_number) {
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
                throw new IllegalStateException("Unexpected value: " + mark_number);
        }
        one_card.give_mark(mark_in);
        one_card.give_strength(strength_number);
        return one_card;
    }

    private String show_card(Card card_in){
        String mark_display;
        switch(card_in.return_mark()){
            case heart:
                mark_display="♥";
                break;
            case spade:
                mark_display="♠";
                break;
            case dia:
                mark_display="◆";
                break;
            case club:
                mark_display="♣";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + card_in.return_mark());
        }mark_display.concat(card_in.return_number());

        return "["+mark_display+card_in.return_number()+"]";
    }

    private boolean check_if_decideable(ArrayList<Card> selected_card_in){
        int localcounter=0;
        for(localcounter=1;localcounter<selected_card_in.size();localcounter++){
            if(selected_card_in.get(localcounter-1).return_strength()!=selected_card_in.get(localcounter).return_strength()){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        // text_view： activity_main.xml の TextView の id
        String[] id_name= new String[NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1];
        String[] com_name=new String[NUMBER_OF_PLAYERS];
        int counter;
        final TextView[] player_card=new TextView[NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1];
        TextView[] com_card=new TextView[NUMBER_OF_PLAYERS];
        for(counter=0;counter<NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1;counter++) {
            id_name[counter]="player_card_in_hand_"+counter;
            player_card[counter] = findViewById(getResources().getIdentifier(id_name[counter], "id", getPackageName()));
        }
        for(counter=1;counter<NUMBER_OF_PLAYERS;counter++){
            com_name[counter]="COM"+counter+"_card";
            com_card[counter]=findViewById(getResources().getIdentifier(com_name[counter],"id",getPackageName()));
        }
        initialize_array(already_used_list);
        final Player man[]=new Player[NUMBER_OF_PLAYERS];
        for(counter=0;counter<NUMBER_OF_PLAYERS;counter++){
            man[counter]=new Player();
        }
        man[0].set_name("user");
        for(counter=1;counter<NUMBER_OF_PLAYERS;counter++){
            man[counter].set_name("COM"+counter);
        }
        final boolean[] clicked=new boolean[NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1];
        for(counter=0;counter<NUMBER_OF_CARDS;counter++) {
            man[counter % 5].set_card(generate_random_card());
        }
        for(counter=0;counter<NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1;counter++){
            clicked[counter] = false;
            player_card[counter].setText(show_card(((Card) man[0].show_and_list().get(counter))));
        }
        for(counter=1;counter<NUMBER_OF_PLAYERS;counter++){
            com_card[counter].setText(""+man[counter].show_and_list().size()+"枚");
        }
        for(counter=0;counter<NUMBER_OF_CARDS/NUMBER_OF_PLAYERS+1;counter++) {
            final int finalCounter = counter;
            player_card[counter].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!clicked[finalCounter]) {
                        player_card[finalCounter].setTextColor(Color.BLUE);
                        player_card[finalCounter].setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalCounter]=true;
                        man[0].return_players_selected_card_list().return_selected_card().add((Card) man[0].show_and_list().get(finalCounter));
                        man[0].return_players_selected_card_list().return_card_id_for_textwiew().add(finalCounter);
                    }else {
                        player_card[finalCounter].setTextColor(Color.BLACK);
                        player_card[finalCounter].setTypeface(Typeface.DEFAULT);
                        clicked[finalCounter]=false;
                        man[0].return_players_selected_card_list().return_selected_card().remove((Card) man[0].show_and_list().get(finalCounter));
                    }
                }
            });
            player_card[counter].setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    if (check_if_decideable(man[0].return_players_selected_card_list().return_selected_card())) {
                        player_card[finalCounter].setText("");
                        man[0].return_players_selected_card_list().return_selected_card().clear();
                    } else {
                        Toast.makeText(getApplicationContext(), "出せません！", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }}


}
