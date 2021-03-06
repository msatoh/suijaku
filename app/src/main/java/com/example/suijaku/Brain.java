package com.example.suijaku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.example.suijaku.Cst.FILE_PATH;
import static com.example.suijaku.Cst.FILE_PATH_NNBSelect;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.tanh;

public class Brain {
    class NN implements Serializable {
    }

    public NN rtn_nn() {
        return new NN();
    }

    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        return new ArrayList<>();
    }

    public void back_propagation(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field, ArrayList<Card> answer_cards) {
        return;
    }

    public ArrayList<ArrayList<Card>> add_card_by_sheets(ArrayList<Card> mycard, ArrayList<Card> card_field, int size) {
        Check checker = new Check();
        ArrayList<Card> candidate_cards = new ArrayList<>();
        ArrayList<ArrayList<Card>> candidate_list = new ArrayList<>();
        int first = 1, second = 2, third = 3, fourth = 4;
        switch (size) {
            case 1:
                for (first = 0; first < mycard.size(); first++) {
                    candidate_cards.clear();
                    candidate_cards.add(mycard.get(first));
                    if (checker.chk_if_decideable(candidate_cards, card_field)) {
                        candidate_list.add((ArrayList<Card>) candidate_cards.clone());
                    }
                }
                break;
            case 2:
                candidate_cards.clear();
                for (first = 0; first < second; first++) {
                    candidate_cards.add(mycard.get(first));
                    for (second = first + 1; second < mycard.size(); second++) {
                        candidate_cards.add(mycard.get(second));
                        if (checker.chk_if_decideable(candidate_cards, card_field)) {
                            candidate_list.add((ArrayList<Card>) candidate_cards.clone());
                        }
                        candidate_cards.remove(mycard.get(second));
                    }
                    candidate_cards.remove(mycard.get(first));
                }
                break;
            case 3:
                candidate_cards.clear();
                for (first = 0; first < second; first++) {
                    candidate_cards.add(mycard.get(first));
                    for (second = first + 1; second < third; second++) {
                        candidate_cards.add(mycard.get(second));
                        for (third = second + 1; third < mycard.size(); third++) {
                            candidate_cards.add(mycard.get(third));
                            if (checker.chk_if_decideable(candidate_cards, card_field)) {
                                candidate_list.add((ArrayList<Card>) candidate_cards.clone());
                            }
                            candidate_cards.remove(mycard.get(third));
                        }
                        candidate_cards.remove(mycard.get(second));
                    }
                    candidate_cards.remove(mycard.get(first));
                }
                break;
            case 4:
                candidate_cards.clear();
                for (first = 0; first < second; first++) {
                    candidate_cards.add(mycard.get(first));
                    for (second = first + 1; second < third; second++) {
                        candidate_cards.add(mycard.get(second));
                        for (third = second + 1; third < fourth; third++) {
                            candidate_cards.add(mycard.get(third));
                            for (fourth = third + 1; fourth < mycard.size(); fourth++) {
                                candidate_cards.add(mycard.get(fourth));
                                if (checker.chk_if_decideable(candidate_cards, card_field)) {
                                    candidate_list.add((ArrayList<Card>) candidate_cards.clone());
                                }
                                candidate_cards.remove(mycard.get(fourth));
                            }
                            candidate_cards.remove(mycard.get(third));
                        }
                        candidate_cards.remove(mycard.get(second));
                    }
                    candidate_cards.remove(mycard.get(first));
                }
                break;
        }
        return candidate_list;
    }

    public ArrayList<ArrayList<Card>> return_candidate_lists(ArrayList<Card> mycard, ArrayList<Card> card_field) {
        ArrayList<ArrayList<Card>> candidate_list = new ArrayList<>();
        int cnt;
        if (card_field.size() == 0) {
            for (cnt = 1; cnt <= min(4, mycard.size()); cnt++) {
                candidate_list.addAll(add_card_by_sheets(mycard, card_field, cnt));
            }
        } else {
            for(cnt=1;cnt<=card_field.size();cnt++) {
                candidate_list.addAll(add_card_by_sheets(mycard, card_field, cnt));
            }
        }
        return candidate_list;
    }
}

class BasicBrain extends Brain{
    public ArrayList<Card> select_card_by_sheets(ArrayList<Card> mycard, ArrayList<Card> card_field,int size){
        Check checker = new Check();
        ArrayList<Card> empty_card = new ArrayList<>();
        int first = 1, second = 2, third = 3, fourth = 4;
        ArrayList<Card> candidate_card = new ArrayList<>();
        switch (size) {
            case 1:
                for (first = 0; first < mycard.size(); first++) {
                    candidate_card.clear();
                    candidate_card.add(mycard.get(first));
                    if (checker.chk_if_decideable(candidate_card, card_field)) {
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
                        if (checker.chk_if_decideable(candidate_card, card_field)) {
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
                            if (checker.chk_if_decideable(candidate_card, card_field)) {
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
                                if (checker.chk_if_decideable(candidate_card, card_field)) {
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
    @Override
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        int cnt;
        ArrayList<Card> empty_card = new ArrayList<>();
        if(card_field.size()==0){
            for(cnt=min(4,mycard.size());cnt>0;cnt--){
                if(select_card_by_sheets(mycard,card_field,cnt).size()!=0){
                    return select_card_by_sheets(mycard,card_field,cnt);
                }
            }
        }else if(mycard.size()>=card_field.size()){
            return select_card_by_sheets(mycard,card_field,card_field.size());
        }
        return empty_card;
    }
}

class StrongerBrain extends Brain{

    public ArrayList<Card> select_card_by_sheets(ArrayList<Card> mycard, ArrayList<Card> card_field,int size,boolean stronger){
        Check checker = new Check();
        int first, second = 2, third = 3, fourth = 4;
        ArrayList<Card> candidate_card = new ArrayList<>();
        ArrayList<Card> buf_card=new ArrayList<>();
        switch (size) {
            case 1:
                for (first = 0; first < mycard.size(); first++) {
                    candidate_card.clear();
                    candidate_card.add(mycard.get(first));
                    if (checker.chk_if_decideable(candidate_card, card_field)) {
                        if(stronger||(!stronger&&candidate_card.get(0).strength<12)) {
                            if (buf_card.size() == 0) {
                                buf_card = candidate_card;
                            } else if (stronger && buf_card.get(0).strength < candidate_card.get(0).strength) {
                                buf_card = candidate_card;
                            } else if (!stronger && buf_card.get(0).strength > candidate_card.get(0).strength && candidate_card.get(0).strength < 12) {
                                buf_card = candidate_card;
                            }
                        }
                    }
                }
                return buf_card;
            case 2:
                candidate_card.clear();
                for (first = 0; first < second; first++) {
                    candidate_card.add(mycard.get(first));
                    for (second = first + 1; second < mycard.size(); second++) {
                        candidate_card.add(mycard.get(second));
                        if (checker.chk_if_decideable(candidate_card, card_field)) {
                            if(stronger||(!stronger&&candidate_card.get(0).strength<12)) {
                                if (buf_card.size() == 0) {
                                    buf_card = candidate_card;
                                } else if (stronger && buf_card.get(0).strength < candidate_card.get(0).strength) {
                                    buf_card = candidate_card;
                                } else if (!stronger && buf_card.get(0).strength > candidate_card.get(0).strength && candidate_card.get(0).strength < 12) {
                                    buf_card = candidate_card;
                                }
                            }
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
                break;
            case 3:
                candidate_card.clear();
                for (first = 0; first < second; first++) {
                    candidate_card.add(mycard.get(first));
                    for (second = first + 1; second < third; second++) {
                        candidate_card.add(mycard.get(second));
                        for (third = second + 1; third < mycard.size(); third++) {
                            candidate_card.add(mycard.get(third));
                            if (checker.chk_if_decideable(candidate_card, card_field)) {
                                if(stronger||(!stronger&&candidate_card.get(0).strength<12)) {
                                    if (buf_card.size() == 0) {
                                        buf_card = candidate_card;
                                    } else if (stronger && buf_card.get(0).strength < candidate_card.get(0).strength) {
                                        buf_card = candidate_card;
                                    } else if (!stronger && buf_card.get(0).strength > candidate_card.get(0).strength && candidate_card.get(0).strength < 12) {
                                        buf_card = candidate_card;
                                    }
                                }
                            }
                            candidate_card.remove(mycard.get(third));
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
                break;
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
                                if (checker.chk_if_decideable(candidate_card, card_field)) {
                                    if(stronger||(!stronger&&candidate_card.get(0).strength<12)) {
                                        if (buf_card.size() == 0) {
                                            buf_card = candidate_card;
                                        } else if (stronger && buf_card.get(0).strength < candidate_card.get(0).strength) {
                                            buf_card = candidate_card;
                                        } else if (!stronger && buf_card.get(0).strength > candidate_card.get(0).strength && candidate_card.get(0).strength < 12) {
                                            buf_card = candidate_card;
                                        }
                                    }
                                }
                                candidate_card.remove(mycard.get(fourth));
                            }
                            candidate_card.remove(mycard.get(third));
                        }
                        candidate_card.remove(mycard.get(second));
                    }
                    candidate_card.remove(mycard.get(first));
                }
                break;
        }
        return buf_card;
    }
    @Override
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        Random random=new Random();
        int cnt;
        ArrayList<Card> empty_card = new ArrayList<>();
        if(card_field.size()==0){
            for(cnt=min(4,mycard.size());cnt>0;cnt--){
                if(select_card_by_sheets(mycard,card_field,cnt,false).size()!=0){
                    if (min(min(min(min(card_player1,card_player2),card_player3),card_player4),mycard.size())<=2) {
                        return select_card_by_sheets(mycard, card_field, cnt, true);
                    }else {
                        return select_card_by_sheets(mycard, card_field, cnt, false);
                    }
                }
            }
        }else{
            if(mycard.size()>=card_field.size()) {
                if (min(min(min(min(card_player1,card_player2),card_player3),card_player4),mycard.size())<=2) {
                    return select_card_by_sheets(mycard, card_field, card_field.size(), true);
                } else {
                    return select_card_by_sheets(mycard, card_field, card_field.size(), false);
                }
            }
        }
        return empty_card;
    }
}

class NNBrain extends Brain implements Serializable {
    Random random = new Random();
    final float eta = 0.001f;
    float[] in_put = new float[NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2];
    public float sigmoid(float param) {

        return (float) tanh(param);
    }

    public NNBrain() throws IOException, ClassNotFoundException {
        int cnt;
        File file = new File(FILE_PATH);
        if (file.exists()) {
            ObjectInputStream file_param = new ObjectInputStream(new FileInputStream(FILE_PATH));
            nn = (NN) file_param.readObject();
            file_param.close();
        } else {
            nn = new NN();
            nn.perceptron1st = new Neuron[13];
            nn.perceptron2nd = new Neuron[12];
            nn.perceptron3rd = new Neuron[11];
            for (cnt = 0; cnt < 13; cnt++) {
                nn.perceptron1st[cnt] = new Neuron();
                nn.perceptron1st[cnt].set_params(NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2);
                nn.perceptron1st[cnt].initialize();
            }
            for (cnt = 0; cnt < 12; cnt++) {
                nn.perceptron2nd[cnt] = new Neuron();
                nn.perceptron2nd[cnt].set_params(13);
                nn.perceptron2nd[cnt].initialize();
            }
            for (cnt = 0; cnt < 11; cnt++) {
                nn.perceptron3rd[cnt] = new Neuron();
                nn.perceptron3rd[cnt].set_params(12);
                nn.perceptron3rd[cnt].initialize();
            }
            nn.finalbias = random.nextFloat();
            ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            file_param.writeObject(nn);
            file_param.close();
        }
    }

    @Override
    public void back_propagation(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field, ArrayList<Card> answer_cards) {
        float[] answer_list = new float[11];
        float[] err = new float[11];
        int cnt, i, j, k;
        for (cnt = 0; cnt < answer_cards.size(); cnt++) {
            if (mycard.contains(answer_cards.get(cnt))) {
                answer_list[mycard.indexOf(answer_cards.get(cnt))] = 1.0f;
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (nn.rtn_3rd_layer(cnt) > nn.finalbias && answer_list[cnt] == 0.0) {
                err[cnt] = nn.rtn_3rd_layer(cnt);
            } else if (nn.rtn_3rd_layer(cnt) < nn.finalbias && answer_list[cnt] == 1.0) {
                err[cnt] = -nn.finalbias;
            }else{
                err[cnt] = 0;
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (err[cnt] != 0) {
                for (i = 0; i < 12; i++) {
                    nn.perceptron3rd[cnt].weight[i] -= eta * nn.perceptron2nd[i].out_put * err[cnt];
                }
                nn.perceptron3rd[cnt].bias -= eta * err[cnt];
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (err[cnt] != 0) {
                for (i = 0; i < 12; i++) {
                    for (j = 0; j < 13; j++) {
                        nn.perceptron2nd[i].weight[j] -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron1st[j].out_put;
                    }
                    nn.perceptron2nd[i].bias-=eta*nn.perceptron3rd[cnt].weight[i];
                }
            }
        }
        for(cnt=0;cnt<11;cnt++){
            if(err[cnt]!=0){
                for(i=0;i<12;i++){
                    for (j = 0; j < 13; j++) {
                        for (k = 0; k < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; k++) {
                            nn.perceptron1st[j].weight[k] -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron2nd[i].weight[j] * in_put[k];
                        }
                        nn.perceptron1st[j].bias -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron2nd[i].weight[j];
                    }
                }
            }
        }
    }

    NN nn;

    public NN rtn_nn() {
        return nn;
    }

    class Neuron implements Serializable {
        float bias;
        float[] weight;
        float out_put;

        public void set_params(int num_of_input) {
            weight = new float[num_of_input];
        }

        public void set_weight(int num_of_input, float param) {
            weight[num_of_input] = param;
        }

        public void set_bias(int cnt, float param) {
            bias = param;
        }

        public void calc(float[] input_param) {
            int cnt;
            float sum = 0.0f;
            sum += bias;
            for (cnt = 0; cnt < input_param.length; cnt++) {
                sum += weight[cnt] * input_param[cnt];
            }
            out_put = sigmoid(sum);
        }

        public void initialize() {
            int inner_cnt;
            bias = random.nextFloat() * 2 - 1;
            for (inner_cnt = 0; inner_cnt < weight.length; inner_cnt++) {
                weight[inner_cnt] = random.nextFloat() * 2 - 1;
            }
        }
    }

    @Override
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        int cnt;
        in_put[0] = card_player1;
        in_put[1] = card_player2;
        in_put[2] = card_player3;
        in_put[3] = card_player4;
        for (cnt = 4; cnt < 4 + mycard.size(); cnt++) {
            in_put[cnt] = mycard.get(mycard.size()-(cnt - 4)-1).strength;
        }
        for (cnt = 4 + NUM_OF_CARDS / NUM_OF_PLAYERS; cnt < 4 + NUM_OF_CARDS / NUM_OF_PLAYERS + card_field.size(); cnt++) {
            in_put[cnt] = card_field.get(cnt - (4 + NUM_OF_CARDS / NUM_OF_PLAYERS)).strength;
        }
        return nn.calc(in_put, mycard);
    }

    class NN extends Brain.NN implements Serializable {
        Neuron[] perceptron1st;
        Neuron[] perceptron2nd;
        Neuron[] perceptron3rd;
        float[] result_1st_layer = new float[13];
        float[] result_2nd_layer = new float[12];
        float[] result_3rd_layer = new float[11];
        float finalbias;

        public ArrayList<Card> calc(float[] input, ArrayList<Card> mycard) {
            int cnt;
            ArrayList<Card> out_put = new ArrayList<>();
            for (cnt = 0; cnt < 13; cnt++) {
                perceptron1st[cnt].calc(input);
                result_1st_layer[cnt] = perceptron1st[cnt].out_put;
            }
            for (cnt = 0; cnt < 12; cnt++) {
                perceptron2nd[cnt].calc(result_1st_layer);
                result_2nd_layer[cnt] = perceptron2nd[cnt].out_put;
            }
            for (cnt = 0; cnt < 11; cnt++) {
                perceptron3rd[cnt].calc(result_2nd_layer);
                result_3rd_layer[cnt] = perceptron3rd[cnt].out_put;
                if (result_3rd_layer[cnt] > finalbias && cnt < mycard.size()) {
                    out_put.add(mycard.get(cnt));
                }
            }
            return out_put;
        }

        public float rtn_1st_layer(int arg) {
            return result_1st_layer[arg];
        }

        public float rtn_2nd_layer(int arg) {
            return result_2nd_layer[arg];
        }

        public float rtn_3rd_layer(int arg) {
            return result_3rd_layer[arg];
        }
    }
}

class NNBrain_ReLu extends NNBrain implements Serializable {
    final float eta = 0.0001f;
    @Override
    public float sigmoid(float param) {
        return max(param,0);
    }
    public NNBrain_ReLu() throws IOException, ClassNotFoundException {
        int cnt;
        File file = new File(FILE_PATH);
        if (file.exists()) {
            ObjectInputStream file_param = new ObjectInputStream(new FileInputStream(FILE_PATH));
            nn = (NN) file_param.readObject();
            file_param.close();
        } else {
            nn = new NN();
            nn.perceptron1st = new Neuron[13];
            nn.perceptron2nd = new Neuron[12];
            nn.perceptron3rd = new Neuron[11];
            for (cnt = 0; cnt < 13; cnt++) {
                nn.perceptron1st[cnt] = new Neuron();
                nn.perceptron1st[cnt].set_params(NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2);
                nn.perceptron1st[cnt].initialize();
            }
            for (cnt = 0; cnt < 12; cnt++) {
                nn.perceptron2nd[cnt] = new Neuron();
                nn.perceptron2nd[cnt].set_params(13);
                nn.perceptron2nd[cnt].initialize();
            }
            for (cnt = 0; cnt < 11; cnt++) {
                nn.perceptron3rd[cnt] = new Neuron();
                nn.perceptron3rd[cnt].set_params(12);
                nn.perceptron3rd[cnt].initialize();
            }
            nn.finalbias = random.nextFloat();
            ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            file_param.writeObject(nn);
            file_param.close();
        }
    }

    @Override
    public void back_propagation(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field, ArrayList<Card> answer_cards) {
        float[] answer_list = new float[11];
        float[] err = new float[11];
        int cnt, i, j, k;
        for (cnt = 0; cnt < answer_cards.size(); cnt++) {
            if (mycard.contains(answer_cards.get(cnt))) {
                answer_list[mycard.indexOf(answer_cards.get(cnt))] = 1.0f;
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (nn.rtn_3rd_layer(cnt) > nn.finalbias && answer_list[cnt] == 0.0) {
                err[cnt] = nn.rtn_3rd_layer(cnt);
            } else if (nn.rtn_3rd_layer(cnt) < nn.finalbias && answer_list[cnt] == 1.0) {
                err[cnt] = -nn.finalbias;
            }else{
                err[cnt] = 0;
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (err[cnt] != 0) {
                for (i = 0; i < 12; i++) {
                    nn.perceptron3rd[cnt].weight[i] -= eta * nn.perceptron2nd[i].out_put * err[cnt];
                }
                nn.perceptron3rd[cnt].bias -= eta * err[cnt];
            }
        }
        for (cnt = 0; cnt < 11; cnt++) {
            if (err[cnt] != 0) {
                for (i = 0; i < 12; i++) {
                    for (j = 0; j < 13; j++) {
                        nn.perceptron2nd[i].weight[j] -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron1st[j].out_put;
                    }
                    nn.perceptron2nd[i].bias-=eta*nn.perceptron3rd[cnt].weight[i];
                }
            }
        }
        for(cnt=0;cnt<11;cnt++){
            if(err[cnt]!=0){
                for(i=0;i<12;i++){
                    for (j = 0; j < 13; j++) {
                        for (k = 0; k < NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2; k++) {
                            nn.perceptron1st[j].weight[k] -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron2nd[i].weight[j] * in_put[k];
                        }
                        nn.perceptron1st[j].bias -= eta * err[cnt] * nn.perceptron3rd[cnt].weight[i] * nn.perceptron2nd[i].weight[j];
                    }
                }
            }
        }
    }
}

class NNBrain_Select extends NNBrain implements Serializable {
    public NNBrain_Select() throws IOException, ClassNotFoundException {
        int cnt;
        File file = new File(FILE_PATH_NNBSelect);
        if (file.exists()) {
            ObjectInputStream file_param = new ObjectInputStream(new FileInputStream(FILE_PATH_NNBSelect));
            nn = (NN) file_param.readObject();
            file_param.close();
        } else {
            nn = new NN();
            nn.perceptron1st = new Neuron[13];
            nn.perceptron2nd = new Neuron[12];
            nn.perceptron3rd = new Neuron[11];
            for (cnt = 0; cnt < 13; cnt++) {
                nn.perceptron1st[cnt] = new Neuron();
                nn.perceptron1st[cnt].set_params(NUM_OF_PLAYERS - 1 + (NUM_OF_CARDS / NUM_OF_PLAYERS) * 2);
                nn.perceptron1st[cnt].initialize();
            }
            for (cnt = 0; cnt < 12; cnt++) {
                nn.perceptron2nd[cnt] = new Neuron();
                nn.perceptron2nd[cnt].set_params(13);
                nn.perceptron2nd[cnt].initialize();
            }
            for (cnt = 0; cnt < 11; cnt++) {
                nn.perceptron3rd[cnt] = new Neuron();
                nn.perceptron3rd[cnt].set_params(12);
                nn.perceptron3rd[cnt].initialize();
            }
            nn.finalbias = random.nextFloat();
            ObjectOutputStream file_param = new ObjectOutputStream(new FileOutputStream(FILE_PATH_NNBSelect));
            file_param.writeObject(nn);
            file_param.close();
        }
    }

    @Override
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        int cnt, pos = 4;
        in_put[0] = card_player1;
        in_put[1] = card_player2;
        in_put[2] = card_player3;
        in_put[3] = card_player4;
        for (cnt = 4; cnt < 4 + return_candidate_lists(mycard, card_field).size(); cnt++) {
            if (!(Arrays.asList(in_put).contains(return_candidate_lists(mycard, card_field).get(return_candidate_lists(mycard, card_field).size()-(cnt - 4)-1).get(0).strength))) {
                in_put[pos] = 12 * (return_candidate_lists(mycard, card_field).get(return_candidate_lists(mycard, card_field).size()-(cnt - 4)-1).size() - 1) + return_candidate_lists(mycard, card_field).get(return_candidate_lists(mycard, card_field).size()-(cnt - 4)-1).get(0).strength;
                pos++;
            }
        }
        return nn.calc(in_put, mycard);
    }
}