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
        return accountFile.readAll().stream()
                .map(row -> switch (row.get("Role")) {
                    case "Admin" -> new Admin(row.get("AccountID"), row.get("Name"), row.get("Password"), "Admin");
                    case "Student" -> new Student(row.get("AccountID"), row.get("Name"), row.get("Password"), "Student");
                    case "Tutor" -> new Tutor(row.get("AccountID"), row.get("Name"), row.get("Password"), "Tutor");
                    case "Receptionist" -> new Receptionist(row.get("AccountID"), row.get("Name"), row.get("Password"), "Receptionist");
                    default -> null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
