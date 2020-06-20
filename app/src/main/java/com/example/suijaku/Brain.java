package com.example.suijaku;

import java.util.ArrayList;
import java.util.Random;

public class Brain {
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field){
        Check checker=new Check();
        //Random num_seed=new Random();
        ArrayList<Card> empty_card=new ArrayList<>();
        int candidate;
        ArrayList<Card> candidate_card=new ArrayList<>();
        for(candidate=0;candidate<mycard.size();candidate++){
            candidate_card.clear();
            candidate_card.add(mycard.get(candidate));
            if(checker.check_if_decideable(candidate_card,card_field)){
                return candidate_card;
            }
        }
        return empty_card;
    }

}
