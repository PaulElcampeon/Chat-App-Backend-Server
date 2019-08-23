package com.P.G.chatappbackend.models;

import lombok.Data;

@Data
public class MailId {

    private String roomId;
    private int timestamp;
    private int machineIdentifier;
    private short processIdentifier;
    private int counter;

}
