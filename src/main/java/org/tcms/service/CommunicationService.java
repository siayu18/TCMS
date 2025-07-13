package org.tcms.service;

import org.tcms.model.*;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CommunicationService {
    private final FileHandler comFile;

    public CommunicationService() throws IOException {
        comFile = new FileHandler("communication.csv", Arrays.asList("CommunicationID","Message","SenderID","ReceiverID"));
    }

    public void sendMessage(String senderID, String receiverID, String text) {
        Map<String,String> row = new LinkedHashMap<>();
        row.put("CommunicationID", UUID.randomUUID().toString());
        row.put("Message", text);
        row.put("SenderID", senderID);
        row.put("ReceiverID", receiverID);
        comFile.append(row);
    }

    public List<Communication> getConversationWith(String targetUserID, String currentUserID) {
        return comFile.readAll().stream()
                .filter(row -> {
                    String currentRowSender = row.get("SenderID");
                    String currentRowReceiver = row.get("ReceiverID");
                    return (currentRowSender.equals(targetUserID) && currentRowReceiver.equals(currentUserID)) ||
                            (currentRowSender.equals(currentUserID) && currentRowReceiver.equals(targetUserID));
                })
                .map(row -> new Communication(
                        row.get("CommunicationID"),
                        row.get("Message"),
                        row.get("SenderID"),
                        row.get("ReceiverID")
                ))
                .collect(Collectors.toList());
    }
}
