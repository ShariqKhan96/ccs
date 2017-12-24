package com.example.shariqkhan.chatnsnap;

/**
 * Created by ShariqKhan on 8/16/2017.
 */

public class ChatModel {
    boolean seen;
    long timeago;

    public ChatModel() {
    }

    public ChatModel(boolean seen, long timeago) {
        this.seen = seen;
        this.timeago = timeago;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimeago() {
        return timeago;
    }

    public void setTimeago(long timeago) {
        this.timeago = timeago;
    }
}
