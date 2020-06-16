package com.example.suijaku;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

enum mark{heart,spade,dia,club}

class Player{
    private String name;
    private ArrayList<Card> card_list=new ArrayList<Card>();
    public int number_of_cards(){
        return card_list.size();
    }
    public void set_name(String name_in){
        name=name_in;
    }
    public void set_card(Card card_in){
        card_list.add(card_in);
    }
    public ArrayList show_and_list(){
        return card_list;
    }
}

class Card{
    private mark mark_use;
    private String number;
    private int strength;
//    private int serial_number;
//    public class card_for_display{
//        private mark mark_for_display;
//        private String number_for_display;
//    }
    public int return_strength(){
        return strength;
    }
    public mark return_mark(){
        return mark_use;
    }
    public String return_number(){
        return number;
    }
//    public card_for_display display_card() {
//        card_for_display card_for_display_class = new card_for_display();
//        card_for_display_class.mark_for_display = mark_use;
//        card_for_display_class.number_for_display = number;
//        return card_for_display_class;
//    }
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

    private boolean[] already_used=new boolean[52];
    private ArrayList<Integer> already_used_list=new ArrayList<Integer>();

    private void initialize_array(boolean[] already_used_in,ArrayList<Integer> already_used_list_in){
        int counter;
        for(counter=0;counter<52;counter++) {
            already_used_in[counter]=false;
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
        already_used[random_parameter]=true;
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

    private void devide_card(Player man_in,Card card_in){
        man_in.set_card(card_in);
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
        }mark_display.concat((String)card_in.return_number());

        return "["+mark_display+card_in.return_number()+"]";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        // text_view： activity_main.xml の TextView の id
        String[] id_name= new String[11];
        String[] com_name=new String[5];
        int counter;
        final TextView[] player_card=new TextView[11];
        TextView[] com_card=new TextView[5];
        for(counter=0;counter<11;counter++) {
            id_name[counter]="player_card_in_hand_"+counter;
            player_card[counter] = findViewById(getResources().getIdentifier(id_name[counter], "id", getPackageName()));
        }
        for(counter=1;counter<5;counter++){
            com_name[counter]="COM"+counter+"_card";
            com_card[counter]=findViewById(getResources().getIdentifier(com_name[counter],"id",getPackageName()));
        }
        initialize_array(already_used,already_used_list);
        Player man[]=new Player[5];
        for(counter=0;counter<5;counter++){
            man[counter]=new Player();
        }
        man[0].set_name("user");
        for(counter=1;counter<5;counter++){
            man[counter].set_name("COM"+counter);
        }
        final boolean[] clicked=new boolean[52/5+1];
        for(counter=0;counter<52;counter++) {
            devide_card(man[counter % 5], generate_random_card());
        }
        for(counter=0;counter<52/5+1;counter++){
            clicked[counter] = false;
            player_card[counter].setText(show_card(((Card) man[0].show_and_list().get(counter))));
        }
        for(counter=1;counter<5;counter++){
            com_card[counter].setText(""+man[counter].number_of_cards());
        }
        for(counter=0;counter<52/5+1;counter++) {
            final int finalCounter = counter;
            player_card[counter].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!clicked[finalCounter]) {
                        player_card[finalCounter].setTextColor(Color.BLUE);
                        player_card[finalCounter].setTypeface(Typeface.DEFAULT_BOLD);
                        clicked[finalCounter]=true;
                    }else {
                        player_card[finalCounter].setTextColor(Color.BLACK);
                        player_card[finalCounter].setTypeface(Typeface.DEFAULT);
                        clicked[finalCounter]=false;
                    }
                }
            });
        }}


}
