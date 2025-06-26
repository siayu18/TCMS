package org.tcms.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.tcms.model.Communication;
import org.tcms.model.User;
import org.tcms.service.CommunicationService;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.util.List;

public class CommunicationController {
    public Label emptyFieldError;
    @FXML private AnchorPane chatPane;
    @FXML private Label title;
    @FXML private ScrollPane scrollBar;
    @FXML private VBox chatBox;
    @FXML private TextField msgField;
    @FXML private JFXButton sendBtn;
    @FXML private JFXComboBox<User> chooseStudentBox;

    private CommunicationService comService;
    private String currentUserId;

    @FXML
    public void initialize() {
        sendBtn.setDefaultButton(true);

        currentUserId = Session.getCurrentUserId();
        comService = new CommunicationService();

        List<User> users = comService.getAllUsers();
        chooseStudentBox.getItems().setAll(users);

        // display "ID, name" in the dropdown
        chooseStudentBox.setCellFactory(cb ->
                new ListCell<User>() {
                    @Override
                    protected void updateItem(User u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null ? null : u.getAccountId() + "- " + u.getUsername());
                    }
                }
        );

        chooseStudentBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getAccountId() + "- " + u.getUsername());
            }
        });

        configureActions();
    }

    private void configureActions() {
        sendBtn.setOnAction(e -> {
            String text = msgField.getText().trim();
            User selectedUser = chooseStudentBox.getValue();

            boolean isEmpty = Helper.validateFieldNotEmpty(msgField, emptyFieldError, "Message Cannot be Empty!");
            if (isEmpty) {
                emptyFieldError.setVisible(true);
                return;
            }

            emptyFieldError.setVisible(false);

            // note: service expects senderId, receiverId, text
            comService.sendMessage(currentUserId, selectedUser.getAccountId(), text);
            msgField.clear();
            loadConversation(selectedUser);
        });

        chooseStudentBox.setOnAction(e -> {
            User selectedUser = chooseStudentBox.getValue();
            if (selectedUser != null) {
                loadConversation(selectedUser);
                chatPane.setVisible(true);
            }
        });
    }

    // loads history between currentUserId and otherUser //
    private void loadConversation(User selectedUser) {
        chatBox.getChildren().clear();
        title.setText("Chat with " + selectedUser.getUsername());

        List<Communication> conversation = comService.getConversationWith(selectedUser.getAccountId(), currentUserId);

        for (Communication line : conversation) {
            boolean isSender = line.getSenderID().equals(currentUserId);
            String message = line.getMessage();
            String senderName = isSender ? Session.getCurrentUserName() : selectedUser.getUsername();

            Label bubble = new Label(senderName + ": " + message);
            bubble.setWrapText(true);
            bubble.setMaxWidth(300);
            bubble.getStyleClass().add(isSender ? "sender-message" : "receiver-message");

            HBox container = new HBox(bubble);
            container.setPadding(new Insets(5));
            container.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            chatBox.getChildren().add(container);
        }

        // scroll to bottom
        Platform.runLater(() -> scrollBar.setVvalue(1.0));
    }
}
