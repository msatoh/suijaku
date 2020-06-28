package com.example.suijaku;

import java.util.ArrayList;
import java.util.Random;

public class Brain {
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field){
        Check checker=new Check();
        //Random num_seed=new Random();
        ArrayList<Card> empty_card=new ArrayList<>();
        int candidate;
        int first=1,second=mycard.size()-1,third=mycard.size()-1,fourth=mycard.size()-1;
        ArrayList<Card> candidate_card=new ArrayList<>();
        if(card_field.size()>1){
            second=2;
        }
        if(card_field.size()>2){
            third=3;
        }
        if((card_field.size()>3)){
            fourth=4;
        }
        candidate_card.clear();
        for(first=0;first<mycard.size();first++) {
            candidate_card.add(mycard.get(first));
            if(checker.check_if_decideable(candidate_card,card_field)){
                return candidate_card;
            }
            for(second=second+first-1;second<mycard.size();second++){
                if(candidate_card.indexOf(mycard.get(second))==-1) {
                    candidate_card.add(mycard.get(second));
                }
                if(checker.check_if_decideable(candidate_card,card_field)){
                    return candidate_card;
                }
                for(third=third+second-2;third<mycard.size();third++){
                    if(candidate_card.indexOf(mycard.get(third))==-1) {
                        candidate_card.add(mycard.get(third));
                    }
                    if(checker.check_if_decideable(candidate_card,card_field)){
                        return candidate_card;
                    }
                    for(fourth=fourth+third-3;fourth<mycard.size();fourth++){
                        if(candidate_card.indexOf(mycard.get(fourth))==-1) {
                            candidate_card.add(mycard.get(fourth));
                        }
                        if(checker.check_if_decideable(candidate_card,card_field)){
                            return candidate_card;
                        }
                        if(candidate_card.indexOf(mycard.get(fourth))!=-1) {
                            candidate_card.remove(mycard.get(fourth));
                        }
                    }
                    if(candidate_card.indexOf(mycard.get(third))!=-1) {
                        candidate_card.remove(mycard.get(third));
                    }
                }
                if(candidate_card.indexOf(mycard.get(second))!=-1) {
                    candidate_card.remove(mycard.get(second));
                }
            }
            if(candidate_card.indexOf(mycard.get(first))!=-1) {
                candidate_card.remove(mycard.get(first));
            }
        }
//        for(candidate=0;candidate<mycard.size();candidate++){
//            candidate_card.clear();
//            candidate_card.add(mycard.get(candidate));
//            if(checker.check_if_decideable(candidate_card,card_field)){
//                return candidate_card;
//            }
//        }
        return empty_card;
    }

}