package org.tcms.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.tcms.service.CommunicationService;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.util.List;
import java.util.Map;

public class CommunicationController {
    public Label emptyFieldError;
    @FXML private AnchorPane chatPane;
    @FXML private Label title;
    @FXML private ScrollPane scrollBar;
    @FXML private VBox chatBox;
    @FXML private TextField msgField;
    @FXML private JFXButton sendBtn;
    @FXML private JFXComboBox<Map<String,String>> chooseStudentBox;

    private CommunicationService comService;
    private String currentUserId;

    @FXML
    public void initialize() {
        sendBtn.setDefaultButton(true);

        // grab the logged-in user
        currentUserId = Session.getCurrentUserId();
        comService = new CommunicationService();

        // 1) populate combo with all users
        List<Map<String,String>> users = comService.getAllUsers();
        chooseStudentBox.getItems().setAll(users);

        // display "ID, name" in the dropdown
        chooseStudentBox.setCellFactory(cb ->
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(Map<String,String> u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null
                                ? null
                                : u.get("AccountID") + ", " + u.get("Name"));
                    }
                }
        );
        chooseStudentBox.setButtonCell(chooseStudentBox.getCellFactory().call(null));

        // 2) when they pick someone, load the pane
        chooseStudentBox.setOnAction(e -> {
            Map<String,String> other = chooseStudentBox.getValue();
            if (other != null) {
                loadConversation(other);
                chatPane.setVisible(true);
            }
        });

        // 3) send‐button handler
        sendBtn.setOnAction(e -> {
            String text = msgField.getText().trim();
            Map<String,String> other = chooseStudentBox.getValue();

            boolean isEmpty = Helper.validateNotEmpty(msgField, emptyFieldError, "Message Cannot be Empty!");
            if (isEmpty) {
                emptyFieldError.setVisible(true);
                return;
            }

            emptyFieldError.setVisible(false);

            // note: service expects senderId, receiverId, text
            comService.sendMessage(currentUserId, other.get("AccountID"), text);
            msgField.clear();
            loadConversation(other);
        });
    }

    /** loads history between currentUserId and otherUser */
    private void loadConversation(Map<String,String> other) {
        chatBox.getChildren().clear();
        title.setText("Chat with " + other.get("Name"));

        comService.getConversationWith(other.get("AccountID"), currentUserId)
                .forEach(row -> {
                    boolean isSender = row.get("SenderID").equals(currentUserId);
                    String text = row.get("Message");
                    String who = isSender ? Session.getCurrentUserName() : other.get("Name");

                    Label bubble = new Label(who + ": " + text);
                    bubble.setWrapText(true);
                    bubble.setMaxWidth(300);
                    bubble.getStyleClass().add(isSender ? "sender-message" : "receiver-message");

                    HBox container = new HBox(bubble);
                    container.setPadding(new Insets(5));
                    container.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                    chatBox.getChildren().add(container);
                });

        // scroll to bottom
        Platform.runLater(() -> scrollBar.setVvalue(1.0));
    }
}
