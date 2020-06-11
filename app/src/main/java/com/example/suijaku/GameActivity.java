package com.example.suijaku;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

enum mark{heart,spade,dia,club}

class Player{
    private String name;
    private ArrayList card_list;
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
    public class card_for_display{
        private mark mark_for_display;
        private String number_for_display;
    }
    public int return_strength(){
        return strength;
    }
    public card_for_display display_card() {
        card_for_display card_for_display_class = new card_for_display();
        card_for_display_class.mark_for_display = mark_use;
        card_for_display_class.number_for_display = number;
        return card_for_display_class;
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

    private boolean[] already_used=new boolean[52];

    private void initialize_array(boolean[] already_used_in){
        int counter;
        for(counter=0;counter<52;counter++) {
            already_used_in[counter]=false;
        }
    }

    private Card generate_random_card(){
        mark mark_in;
        Card one_card=new Card();
        Random mark_number_seed=new Random();
        int mark_number=mark_number_seed.nextInt(4);
        int strength_number=mark_number_seed.nextInt(13)+1;
        while(already_used[mark_number * 4 + strength_number - 1]){
            mark_number=mark_number_seed.nextInt(4);
            strength_number=mark_number_seed.nextInt(13)+1;
        }already_used[mark_number * 4 + strength_number - 1]=true;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        initialize_array(already_used);
        Player man=new Player();
        int counter;
        for(counter=0;counter<7;counter++) {
            devide_card(man, generate_random_card());
        }
        for(counter=0;counter<7;counter++){
            man.show_and_list().get(counter);
        }
    }

}
