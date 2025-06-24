package org.tcms.service;

import org.tcms.model.User;
import org.tcms.utils.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class CommunicationService {
    private final FileHandler comFile;
    private final FileHandler accountFile;

    public CommunicationService() {
        comFile = new FileHandler("communication.csv", Arrays.asList("CommunicationID","Message","SenderID","ReceiverID"));
        accountFile = new FileHandler("account.csv", Arrays.asList("AccountID","Name", "Password", "Role"));
    }

    public List<Map<String,String>> getAllUsers() {
        return accountFile.readAll();
    }

    public void sendMessage(String senderId, String receiverId, String text) {
        Map<String,String> row = new LinkedHashMap<>();
        row.put("CommunicationID", UUID.randomUUID().toString());
        row.put("Message", text);
        row.put("SenderID", senderId);
        row.put("ReceiverID", receiverId);
        comFile.append(row);
    }

    public List<Map<String,String>> getConversationWith(String userA, String userB) {
        return comFile.readAll().stream()
                .filter(r -> {
                    String s = r.get("SenderID"), t = r.get("ReceiverID");
                    return (s.equals(userA) && t.equals(userB))
                            || (s.equals(userB) && t.equals(userA));
                })
                .collect(Collectors.toList());
    }
}
