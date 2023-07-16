package com.example.suijaku;

import java.util.ArrayList;

public class Check {
    boolean chk_kaidan(ArrayList<Card> card) {
        int cnt;
        if (card.size() < 3) {
            return false;
        }
        for (cnt = 1; cnt < card.size(); cnt++) {
            if (card.get(cnt - 1).mark_use != card.get(cnt).mark_use) {
                return false;
            }
            if (card.get(cnt - 1).strength + 1 != card.get(cnt).strength) {
                return false;
            }
        }
        return true;
    }

    boolean chk_if_decideable(ArrayList<Card> select_card_in, ArrayList<Card> field_card_in) {
        int localcnt;
        if (select_card_in.size() == 0) {
            return false;
        }
        if (field_card_in.size() != 0) {
            if (select_card_in.size() != field_card_in.size()) {
                return false;
            }
            if (select_card_in.get(0).strength <= field_card_in.get(0).strength) {
                return false;
            }
        }
        if (!chk_kaidan(select_card_in) && !chk_kaidan(field_card_in)) {
            for (localcnt = 1; localcnt < select_card_in.size(); localcnt++) {
                if (select_card_in.get(localcnt - 1).strength != select_card_in.get(localcnt).strength) {
                    return false;
                }
            }
        } else if (chk_kaidan(select_card_in) && chk_kaidan(field_card_in)) {
            return true;
        } else if (field_card_in.size() == 0) {
            return true;
        } else {
            return false;
        }
        return true;
    }
}
