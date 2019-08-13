package com.P.G.chatappbackend.models;

import lombok.Data;

@Data
public class MessageId {
    private int timestamp;
    private int machineIdentifier;
    private short processIdentifier;
    private int counter;
    private String date;
    private long time;
    private long timeSecond;
}
