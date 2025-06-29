package org.tcms.model;

import lombok.Getter;

@Getter
public class Communication {
    private final String communicationID;
    private final String message;
    private final String senderID;
    private final String receiverID;

    public Communication(String communicationID, String message, String senderID, String receiverID) {
        this.communicationID = communicationID;
        this.message = message;
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

}
