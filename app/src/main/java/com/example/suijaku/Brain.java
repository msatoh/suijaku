package com.example.suijaku;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static com.example.suijaku.Cst.NUM_OF_PLAYERS;
import static com.example.suijaku.Cst.NUM_OF_CARDS;
import static java.lang.Math.random;

public class Brain {
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        ArrayList<Card> empty_card = new ArrayList<>();
        return empty_card;
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
                        if(stronger||(!stronger&&candidate_card.get(0).rtn_strength()<12)) {
                            if (buf_card.size() == 0) {
                                buf_card = candidate_card;
                            } else if (stronger && buf_card.get(0).rtn_strength() < candidate_card.get(0).rtn_strength()) {
                                buf_card = candidate_card;
                            } else if (!stronger && buf_card.get(0).rtn_strength() > candidate_card.get(0).rtn_strength() && candidate_card.get(0).rtn_strength() < 12) {
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
                            if(stronger||(!stronger&&candidate_card.get(0).rtn_strength()<12)) {
                                if (buf_card.size() == 0) {
                                    buf_card = candidate_card;
                                } else if (stronger && buf_card.get(0).rtn_strength() < candidate_card.get(0).rtn_strength()) {
                                    buf_card = candidate_card;
                                } else if (!stronger && buf_card.get(0).rtn_strength() > candidate_card.get(0).rtn_strength() && candidate_card.get(0).rtn_strength() < 12) {
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
                                if(stronger||(!stronger&&candidate_card.get(0).rtn_strength()<12)) {
                                    if (buf_card.size() == 0) {
                                        buf_card = candidate_card;
                                    } else if (stronger && buf_card.get(0).rtn_strength() < candidate_card.get(0).rtn_strength()) {
                                        buf_card = candidate_card;
                                    } else if (!stronger && buf_card.get(0).rtn_strength() > candidate_card.get(0).rtn_strength() && candidate_card.get(0).rtn_strength() < 12) {
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
                                    if(stronger||(!stronger&&candidate_card.get(0).rtn_strength()<12)) {
                                        if (buf_card.size() == 0) {
                                            buf_card = candidate_card;
                                        } else if (stronger && buf_card.get(0).rtn_strength() < candidate_card.get(0).rtn_strength()) {
                                            buf_card = candidate_card;
                                        } else if (!stronger && buf_card.get(0).rtn_strength() > candidate_card.get(0).rtn_strength() && candidate_card.get(0).rtn_strength() < 12) {
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

class NNBrain extends Brain{
    Random random = new Random();
    public float sigmoid(float param){
        return max(0,param);
    }
    class Newron {
        float bias;
        float weight[];
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

        public void calc(float[] in_put) {
            int cnt;
            float sum = 0.0f;
            sum += bias;
            for (cnt = 0; cnt < in_put.length; cnt++) {
                sum += weight[cnt] * in_put[cnt];
            }
            out_put = sigmoid(sum);
        }
        public float rtn_output(){
            return out_put;
        }

        public void initialize() {
            int inner_cnt;
            bias = random.nextFloat() * 2 - 1;
            for (inner_cnt = 0; inner_cnt < weight.length; inner_cnt++) {
                weight[inner_cnt] = random.nextFloat() * 2 - 1;
            }
        }
        public void read_params(){

        }
    }

    class NN{
        Newron[] perceptron1st;
        Newron[] perceptron2nd;
        Newron[] perceptron3rd;
        float result_1st_layer[];
        float result_2nd_layer[];
        float finalbias;
        public ArrayList<Card> calc(float[] in_put,ArrayList<Card> mycard){
            int cnt;
            ArrayList<Card>out_put=new ArrayList<>();
            result_1st_layer=new float[13];
            result_2nd_layer=new float[12];
            for(cnt=0;cnt<13;cnt++){
                perceptron1st[cnt].calc(in_put);
                result_1st_layer[cnt]=perceptron1st[cnt].rtn_output();
            }
            for(cnt=0;cnt<12;cnt++){
                perceptron2nd[cnt].calc(result_1st_layer);
                result_2nd_layer[cnt]=perceptron2nd[cnt].rtn_output();
            }
            for(cnt=0;cnt<11;cnt++){
                perceptron3rd[cnt].calc(result_2nd_layer);
                if(perceptron3rd[cnt].rtn_output()>finalbias&&cnt<mycard.size()){
                    out_put.add(mycard.get(cnt));
                }
            }
            return out_put;
        }
    }
    NN nn;
    public NNBrain() throws IOException, ClassNotFoundException {
        int cnt;
        File file=new File("newron_param.bin");
        if(file.exists()){
            ObjectInputStream file_param=new ObjectInputStream(new FileInputStream("newron_param.bin"));
            nn= (NN) file_param.readObject();
            file_param.close();
        }else{
            nn=new NN();
            nn.perceptron1st=new Newron[13];
            nn.perceptron2nd=new Newron[12];
            nn.perceptron3rd=new Newron[11];
            for(cnt=0;cnt<13;cnt++){
                nn.perceptron1st[cnt]=new Newron();
                nn.perceptron1st[cnt].set_params(NUM_OF_PLAYERS-1+(NUM_OF_CARDS/NUM_OF_PLAYERS)*2);
                nn.perceptron1st[cnt].initialize();
            }
            for(cnt=0;cnt<12;cnt++){
                nn.perceptron2nd[cnt]=new Newron();
                nn.perceptron2nd[cnt].set_params(13);
                nn.perceptron2nd[cnt].initialize();
            }
            for(cnt=0;cnt<11;cnt++){
                nn.perceptron3rd[cnt]=new Newron();
                nn.perceptron3rd[cnt].set_params(12);
                nn.perceptron3rd[cnt].initialize();
            }
            nn.finalbias=random.nextFloat();
            ObjectOutputStream file_param=new ObjectOutputStream(new FileOutputStream("newron_param.bin"));
            file_param.writeObject(nn);
            file_param.close();
        }
    }
    @Override
    public ArrayList<Card> calculate_card_to_put(int card_player1, int card_player2, int card_player3, int card_player4, ArrayList<Card> mycard, ArrayList<Card> card_field) {
        float in_put[]=new float[NUM_OF_PLAYERS-1+(NUM_OF_CARDS/NUM_OF_PLAYERS)*2];
        int cnt;
        in_put[0]=card_player1;
        in_put[1]=card_player2;
        in_put[2]=card_player3;
        in_put[3]=card_player4;
        for(cnt=4;cnt<4+mycard.size();cnt++){
            in_put[cnt]=mycard.get(cnt-4).rtn_strength();
        }
        for(cnt=4+NUM_OF_CARDS/NUM_OF_PLAYERS;cnt<4+NUM_OF_CARDS/NUM_OF_PLAYERS+card_field.size();cnt++){
            in_put[cnt]=card_field.get(cnt-(4+NUM_OF_CARDS/NUM_OF_PLAYERS)).rtn_strength();
        }
        return nn.calc(in_put,mycard);
    }
}