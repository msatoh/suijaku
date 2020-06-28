package com.example.suijaku;

import java.util.ArrayList;

public class Brain {
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        Check checker = new Check();
        ArrayList<Card> empty_card = new ArrayList<>();
        int first = 1, second = 2, third = 3, fourth = 4;
        ArrayList<Card> candidate_card = new ArrayList<>();
        switch (card_field.size()) {
            case 1:
                for (first = 0; first < mycard.size(); first++) {
                    candidate_card.clear();
                    candidate_card.add(mycard.get(first));
                    if (checker.check_if_decideable(candidate_card, card_field)) {
                        return candidate_card;
                    }
                }
                return empty_card;
            case 2:
                candidate_card.clear();
                for (first = 0; first < second; first++) {
                    candidate_card.add(mycard.get(first));
                    for (second = first + 1; second < mycard.size(); second++) {
                        candidate_card.add(mycard.get(second));
                        if (checker.check_if_decideable(candidate_card, card_field)) {
                            return candidate_card;
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
                return candidate_card;
            case 3:
                candidate_card.clear();
                for (first = 0; first < second; first++) {
                    candidate_card.add(mycard.get(first));
                    for (second = first + 1; second < third; second++) {
                        candidate_card.add(mycard.get(second));
                        for (third = second + 1; third < mycard.size(); third++) {
                            candidate_card.add(mycard.get(third));
                            if (checker.check_if_decideable(candidate_card, card_field)) {
                                return candidate_card;
                            }
                            candidate_card.remove(mycard.get(third));
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
            case 4:
                candidate_card.clear();
                for (first = 0; first < second; first++) {
                    candidate_card.add(mycard.get(first));
                    for (second = first + 1; second < third; second++) {
                        candidate_card.add(mycard.get(second));
                        for (third = second + 1; third < fourth; third++) {
                            candidate_card.add(mycard.get(third));
                            for(fourth=third+1;fourth<mycard.size();fourth++) {
                                candidate_card.add(mycard.get(fourth));
                                if (checker.check_if_decideable(candidate_card, card_field)) {
                                    return candidate_card;
                                }
                                candidate_card.remove(mycard.get(fourth));
                            }
                            candidate_card.remove(mycard.get(third));
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
        }
        return empty_card;
    }
}