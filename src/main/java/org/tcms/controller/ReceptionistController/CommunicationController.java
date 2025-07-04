package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.tcms.exception.EmptyFieldException;
import org.tcms.model.Communication;
import org.tcms.model.User;
import org.tcms.service.CommunicationService;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;

public class CommunicationController {
    public Label errorLabel;
    @FXML private AnchorPane chatPane;
    @FXML private Label title;
    @FXML private ScrollPane scrollBar;
    @FXML private VBox chatBox;
    @FXML private TextField msgField;
    @FXML private JFXButton sendBtn;
    @FXML private JFXComboBox<User> chooseUserBox;

    private UserService userService;
    private CommunicationService comService;
    private User selectedUser;
    private String currentUserId;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            comService = new CommunicationService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        currentUserId = Session.getCurrentUserID();
        setupUserBox();
        sendBtn.setDefaultButton(true);
        configureActions();
    }

    private void setupUserBox() {
        List<User> users = userService.getAllUsers();
        chooseUserBox.getItems().setAll(users);

        // display "ID - name" in the dropdown
        chooseUserBox.setCellFactory(cb ->
                new ListCell<User>() {
                    @Override
                    protected void updateItem(User u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null ? null : u.getAccountId() + "- " + u.getUsername());
                    }
                }
        );

        chooseUserBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getAccountId() + "- " + u.getUsername());
            }
        });
    }

    private void configureActions() {
        sendBtn.setOnAction(e -> {
            try {
                String text = msgField.getText().trim();
                if (selectedUser == null)
                    return;

                isMessageEmpty();

                errorLabel.setVisible(false);
                comService.sendMessage(currentUserId, selectedUser.getAccountId(), text);
                msgField.clear();
                // load new conversation after new message is added (to update)
                loadConversation(selectedUser);

            } catch (EmptyFieldException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        chooseUserBox.setOnAction(e -> {
            selectedUser = chooseUserBox.getValue();
            if (selectedUser != null) {
                loadConversation(selectedUser);
                chatPane.setVisible(true);
            }
        });
    }

    // loads history between currentUserId and otherUser
    private void loadConversation(User selectedUser) {
        chatBox.getChildren().clear();
        title.setText("Chat with " + selectedUser.getUsername());

        // get all conversation between selected user and current user
        List<Communication> conversation = comService.getConversationWith(selectedUser.getAccountId(), currentUserId);

        for (Communication line : conversation) {
            // check if current user is sender or receiver
            boolean isSender = line.getSenderID().equals(currentUserId);
            String message = line.getMessage();
            // get the name of sender
            String senderName = isSender ? Session.getCurrentUserName() : selectedUser.getUsername();

            Label bubble = new Label(senderName + ": " + message);
            bubble.setWrapText(true);
            bubble.setMaxWidth(300);
            // set style according to sender and receiver
            bubble.getStyleClass().add(isSender ? "sender-message" : "receiver-message");

            HBox container = new HBox(bubble);
            container.setPadding(new Insets(5));
            // alignment according to sender and receiver
            container.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            chatBox.getChildren().add(container);
        }

        // scroll to bottom as default (to the newest message)
        Platform.runLater(() -> scrollBar.setVvalue(1.0));
    }

    private void isMessageEmpty() throws EmptyFieldException {
        if (Helper.validateFieldNotEmpty(msgField)) {
            throw new EmptyFieldException("Message Cannot be Empty!");
        }
    }
}
