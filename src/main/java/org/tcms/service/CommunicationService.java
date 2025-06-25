package org.tcms.service;

import org.tcms.model.*;
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

    public List<User> getAllUsers() {
        List<Map<String, String>> rows = accountFile.readAll();
        List<User> users = new ArrayList<>();

        for (Map<String, String> row : rows) {
            if ("Student".equalsIgnoreCase(row.get("Role"))) {
                users.add(new Student(
                        row.get("AccountID"),
                        row.get("Name"),
                        row.get("Password"),
                        row.get("Role")
                ));
            } else if ("Admin".equalsIgnoreCase(row.get("Role"))) {
                users.add(new Admin(
                        row.get("AccountID"),
                        row.get("Name"),
                        row.get("Password"),
                        row.get("Role")
                ));
            } else if ("Tutor".equalsIgnoreCase(row.get("Role"))) {
                users.add(new Tutor(
                        row.get("AccountID"),
                        row.get("Name"),
                        row.get("Password"),
                        row.get("Role")
                ));
            } else if ("Receptionist".equalsIgnoreCase(row.get("Role"))) {
                users.add(new Receptionist(
                        row.get("AccountID"),
                        row.get("Name"),
                        row.get("Password"),
                        row.get("Role")
                ));
            }
        }
        return users;
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
                    String currentRowSender = row.get("SenderID"), currentRowReceiver = row.get("ReceiverID");
                    return (currentRowSender.equals(targetUserID) && currentRowReceiver.equals(currentUserID)) || (currentRowSender.equals(currentUserID) && currentRowReceiver.equals(targetUserID));
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
