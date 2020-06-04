package com.nimus.chatanonymously.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private User userReceiver;
    private User userSender;
    private String firstBy;
    private String options;
    private boolean isSeen;

    public Chat(String sender, String receiver, String message, User userReceiver, User userSender, String firstBy, String options, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.userReceiver = userReceiver;
        this.userSender = userSender;
        this.firstBy =  firstBy;
        this.options = options;
        this.isSeen = isSeen;
    }

    public Chat() {

    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public String getFirstBy() {
        return firstBy;
    }

    public void setFirstBy(String firstBy) {
        this.firstBy = firstBy;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
