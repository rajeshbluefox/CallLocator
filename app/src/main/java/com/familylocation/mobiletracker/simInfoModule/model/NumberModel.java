package com.familylocation.mobiletracker.simInfoModule.model;


import java.io.Serializable;

public class NumberModel implements Serializable {
    String cardN_name,recharge, main_balance, msg_balance,net_balance,your_number,cc_number;
    int img;

    public NumberModel(String cardN_name, String recharge, String main_balance, String msg_balance, String net_balance, String your_number, String cc_number, int img) {
        this.cardN_name = cardN_name;
        this.recharge = recharge;
        this.main_balance = main_balance;
        this.msg_balance = msg_balance;
        this.net_balance = net_balance;
        this.your_number = your_number;
        this.cc_number = cc_number;
        this.img = img;
    }


    public String getCardN_name() {
        return cardN_name;
    }

    public void setCardN_name(String cardN_name) {
        this.cardN_name = cardN_name;
    }

    public String getRecharge() {
        return recharge;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getMain_balance() {
        return main_balance;
    }

    public void setMain_balance(String main_balance) {
        this.main_balance = main_balance;
    }

    public String getMsg_balance() {
        return msg_balance;
    }

    public void setMsg_balance(String msg_balance) {
        this.msg_balance = msg_balance;
    }

    public String getNet_balance() {
        return net_balance;
    }

    public void setNet_balance(String net_balance) {
        this.net_balance = net_balance;
    }

    public String getYour_number() {
        return your_number;
    }

    public void setYour_number(String your_number) {
        this.your_number = your_number;
    }

    public String getCc_number() {
        return cc_number;
    }

    public void setCc_number(String cc_number) {
        this.cc_number = cc_number;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}

