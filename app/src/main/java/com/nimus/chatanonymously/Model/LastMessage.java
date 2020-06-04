package com.nimus.chatanonymously.Model;

public class LastMessage {
    private String sender;
    private String receiver;
    private String lastMessage;

    public LastMessage(String sender, String receiver, String lastMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.lastMessage = lastMessage;
    }

    public LastMessage() {

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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
