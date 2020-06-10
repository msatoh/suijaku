package com.example.suijaku;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Card{
    private enum mark{heart,spade,dia,club};
    private mark mark_use;
    private String number;
    private int strength;
    private int serial_number;
    public class card_for_display{
        private mark mark_for_display;
        private String number_for_display;
    }
    public int return_strength(){
        return strength;
    }
    public card_for_display display_card(int in_serial_number) {
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
    public void give_serial_number(int serial_number_in){
        serial_number=serial_number_in;
    }
}

public void shuffle(){

}

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);

    }
}
