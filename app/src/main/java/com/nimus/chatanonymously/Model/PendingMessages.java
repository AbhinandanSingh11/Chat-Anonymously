package com.nimus.chatanonymously.Model;

public class PendingMessages {
    private String sender;
    private String receiver;
    private int count;

    public PendingMessages(String sender, String receiver, int count) {
        this.sender = sender;
        this.receiver = receiver;
        this.count = count;
    }

    public PendingMessages() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
