package com.example.laborator.controller;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.service.MessageService;
import com.example.laborator.service.UtilizatorService;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Mesaj1Controller implements Observer<UtilizatorChangeEventType> {
    UtilizatorService userService;
    MessageService messageService;
    ObservableList<Message> model = FXCollections.observableArrayList();
    @FXML
    TableView<Message> tableView;
    @FXML
    TableColumn<Utilizator, String> tableColumnFrom;
    @FXML
    TableColumn<Message, String> tableColumnMessage;
    @FXML
    Label chatLabel;
    @FXML
    TextField textFieldMessage;
    Utilizator selectedUser1;
    Utilizator selectedUser2;
    ArrayList<Utilizator> users;
    Stage dialogStage;

    public void setMessageService1(UtilizatorService userService, MessageService messageService, Stage stage, Utilizator user1, Utilizator user2) {
        this.userService = userService;
        this.messageService = messageService;
        this.messageService.addObserver(this);
        this.dialogStage = stage;
        this.selectedUser1 = user1;
        this.selectedUser2 = user2;
        this.chatLabel.setText("Chat with " + selectedUser2.getFirstName());
        initModel();
    }

    private void initModel() {
        ArrayList<Message> messages = messageService.findAllForTwoUsers(selectedUser1.getId(), selectedUser2.getId());
        List<Message> messageList = StreamSupport.stream(messages.spliterator(), false).collect(Collectors.toList());
        model.setAll(messageList);
    }

    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableView.setItems(model);
    }

    public void handleDeleteMessage() {
        Message toBeDeleted = tableView.getSelectionModel().getSelectedItem();

        if (toBeDeleted != null) {
            Optional<Message> deleted = messageService.delete(toBeDeleted.getId());

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "The message has been successfully deleted!");
        } else
            MessageAlert.showErrorMessage(null, "No message was selected!");
    }

    public void handleSendMessage() {
        ArrayList<Long> to = new ArrayList<>();
        to.add(selectedUser2.getId());
        Message toBeSent = new Message(selectedUser1.getId(), to, textFieldMessage.getText());
        Random random = new Random();
        Long id = random.nextLong(100000);
        toBeSent.setId(id);

        Optional<Message> sent = messageService.save(toBeSent);

        if (sent.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Send", "The message was sent!");
            textFieldMessage.clear();
        }
    }

    public void handleReplyMessage() {
        Message toBeReplied = tableView.getSelectionModel().getSelectedItem();

        if (toBeReplied != null) {
            ArrayList<Long> to = new ArrayList<>();
            to.add(selectedUser2.getId());
            Message toBeSent = new Message(selectedUser1.getId(), to, textFieldMessage.getText());
            Random random = new Random();
            Long id = random.nextLong(100000);
            toBeSent.setId(id);

            Optional<Message> sent = messageService.save(toBeSent);

            toBeReplied.setReplyMessage(id);
            messageService.update(toBeReplied.getId(), toBeReplied);

            if (sent.isEmpty()) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Send", "The message was sent!");
                textFieldMessage.clear();
            }
        } else
            MessageAlert.showErrorMessage(null, "No message was selected!");
    }

    @Override
    public void update(UtilizatorChangeEventType messageChangeEvent) {
        initModel();
    }
}
