package com.example.suijaku;

import java.util.ArrayList;

public class Check {
    boolean chk_if_decideable(ArrayList<Card> select_card_in, ArrayList<Card> field_card_in){
        int localcnt=0;
        if(select_card_in.size()==0){
            return false;
        }
        if(field_card_in.size()!=0) {
            if (select_card_in.size() != field_card_in.size()) {
                return false;
            }
            if(select_card_in.get(0).rtn_strength()<=field_card_in.get(0).rtn_strength()){
                return false;
            }
        }
        for (localcnt = 1; localcnt < select_card_in.size(); localcnt++) {
            if (select_card_in.get(localcnt - 1).rtn_strength() != select_card_in.get(localcnt).rtn_strength()) {
                return false;
            }
        }

        return true;
    }
}
