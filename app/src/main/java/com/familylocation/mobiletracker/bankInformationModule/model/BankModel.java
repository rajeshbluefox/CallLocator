package com.familylocation.mobiletracker.bankInformationModule.model;


public class BankModel {
    int img;
    String title, balance, customer;

    public BankModel(String title, String balance, String customer, int img) {
        this.title = title;
        this.balance = balance;
        this.customer = customer;
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
