package com.example.laborator.controller;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.service.FriendRequestService;
import com.example.laborator.service.MessageService;
import com.example.laborator.service.PrietenieService;
import com.example.laborator.service.UtilizatorService;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.ArrayList;
import java.util.Map;

public class MessageController implements Observer<UtilizatorChangeEventType> {
    @FXML
    public TextField from;
    @FXML
    public TextField to;
    @FXML
    public TextField message;
    @FXML
    public TextField data;
    @FXML
    public TextField reply;

    @FXML
    public Label errorMessage;
    Utilizator prieten;
    Utilizator activeUser;
    @FXML
    public TableView<Utilizator> tableViewPrieteni;
    public TableColumn<Utilizator, String> friendColumnFirstName;
    public TableColumn<Utilizator, String> friendColumnLastName;
    UtilizatorService utilizatorService;
    Utilizator utilizator_selectat;
    PrietenieService prietenieService;
    FriendRequestService requestService;
    MessageService messageService;
    Message messageSelected;
    ObservableList<Message> messages = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriends = FXCollections.observableArrayList();
    public TextField messageField;
    @FXML
    public ListView<Message> messageListView;

    public void setService(UtilizatorService service1, PrietenieService service2, FriendRequestService service3, MessageService service4, Utilizator u) {
        this.utilizatorService = service1;
        this.prietenieService = service2;
        this.requestService = service3;
        this.messageService = service4;
        this.activeUser = u;

        this.messageService.addObserver(this);
        this.prietenieService.addObserver(this);
        this.requestService.addObserver(this);
        this.utilizatorService.addObserver(this);
        initFriends();
    }

    @FXML
    public void initialize() {
        friendColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewPrieteni.setItems(modelFriends);

        //messageListView.setItems(messages);

        this.tableViewPrieteni.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (tableViewPrieteni != null){
            this.tableViewPrieteni.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    prieten = (Utilizator) tableViewPrieteni.getSelectionModel().getSelectedItem();
                    if (prieten != null) {
                        initMessages(prieten);
                        messageListView.setItems(messages);
                    }
                }
            });}
        else System.out.println("TABLE VIEW PRIETENI NU EstE NULLLLL "+tableViewPrieteni);

        this.messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String text = messageField.getText();
                    ArrayList<Long> to = new ArrayList<>();
                    to.add(prieten.getId());
                    Message message = new Message(activeUser.getId(), to, text);
                    try {
                        messageService.save(message);
                        errorMessage.setText("");
                    } catch (Exception e) {
                        errorMessage.setText(e.getMessage());
                        errorMessage.setTextFill(Color.DARKRED);
                    }
                    messageField.clear();
                }
            }
        });

        messageListView.setCellFactory(list -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Resetați stilul celulei dacă este goală sau nu conține un item
                } else {
                    setText(item.getMesaage());

                    if(item.getFrom()==activeUser.getId())
                                                setText("Me:" + item.getMesaage());
                    else
                            setText("You:" + item.getMesaage());
//                    if (item.getReplyMessage() != 0)
//                        setText("Reply:" + " " + item.getMesaage());

                    if (item.getReplyMessage() != 0)
                        setText("Reply:" + " " + item.getMesaage());
//                    {
//                        setOnMouseClicked(event -> {
//                            // Verificare dacă celula a fost apăsată
//                            if (event.getClickCount() == 1) {
//                                // Afisare etichetă sau informație suplimentară când se face clic pe celulă
////                                String replyInfo = "Reply to: " + item.getReplyMessage() + " - " + item.getMesaage();
////                                System.out.println(replyInfo); // Înlocuiți acest lucru cu logica pentru afișarea informației dorite
//                                // Puteți afișa o fereastră de dialog, să setați un label pe un alt component etc.
//                            }
//                        });
//                        setText("Reply:" + " " + item.getMesaage());
//                        if ("blue".equals(item.getColor())) {
//                            setStyle("-fx-text-fill: blue; -fx-alignment: center-right;"); // Culorile pentru mesajele trimise
//                        } else if ("green".equals(item.getColor())) {
//                            setStyle("-fx-text-fill: green; -fx-alignment: center-left;"); // Culorile pentru mesajele primite
//                        }
//                        Tooltip tooltip = new Tooltip(messageService.findMessageById(item.getReplyMessage()).getMesaage());
//                        setTooltip(tooltip);
//                    }
                }
            }
        });


        this.messageListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                messageSelected = messageListView.getSelectionModel().getSelectedItem();
                if (messageSelected != null) {
                    System.out.println(messageSelected);
                }
            }
        });
    }

    public void setActiveUser(Utilizator activeUser) {
        this.activeUser = activeUser;
    }

    public void initFriends() {
//        Map<Utilizator, LocalDateTime> allFriends = (Map<Utilizator, LocalDateTime>) prietenieService.getFriends2(activeUser.getId());
//        List<Utilizator> friendsList = new ArrayList<>();
//        for (Map.Entry<Utilizator, LocalDateTime> entry : allFriends.entrySet())
//            friendsList.add(entry.getKey());
//        modelFriends.setAll(friendsList);
        List<Utilizator> friendsList = prietenieService.getFriends2(activeUser.getId());
        modelFriends.setAll(friendsList);
    }

    public void initMessages(Utilizator friend) {
        List<Message> conversation = messageService.getConversation(activeUser.getId(), friend.getId());
        List<Message> list = new ArrayList<>();
        for (Message x : conversation) {
            if (x.getFrom().equals(activeUser.getId())) {
                x.setColor("blue");
                // x.setAlignment("left");
            } else {
                x.setColor("green");
                //x.setAlignment("right");
            }
            list.add(x);
        }
        this.messages.setAll(list);
    }

    public void handleSendToAll(ActionEvent actionEvent) {
        List<Utilizator> all = tableViewPrieteni.getSelectionModel().getSelectedItems();

        String text = messageField.getText();
        ArrayList<Long> to = new ArrayList<>();

        for (Utilizator it : all)
            to.add(it.getId());

        Message message = new Message(activeUser.getId(), to, text);
        try {
            messageService.save(message);
            this.errorMessage.setText("");
        } catch (Exception e) {
            this.errorMessage.setText(e.getMessage());
            this.errorMessage.setTextFill(Color.DARKRED);
        }
        this.messageField.clear();
        prieten = (Utilizator) tableViewPrieteni.getSelectionModel().getSelectedItem();
        if (prieten != null) {
            initMessages(prieten);
            messageListView.setItems(messages);
        }
    }

    public void handleReply(ActionEvent actionEvent) {
//        User u = friendsTable.getSelectionModel().getSelectedItem();

        String text = messageField.getText();
        ArrayList<Long> to = new ArrayList<>();
        to.add(prieten.getId());

//        System.out.println("aaaaaaaaa"+ messageSelected.getId());

        Message message = new Message(activeUser.getId(), to, text,LocalDateTime.now(),  messageSelected.getId());
//        System.out.println("bbbbbbbbbb"+message.getReplyMessage());

        try {
            messageService.replyToOne(message, prieten.getId());
            this.errorMessage.setText("");
        } catch (Exception e) {
            this.errorMessage.setText(e.getMessage());
            this.errorMessage.setTextFill(Color.DARKRED);
        }
        this.messageField.clear();
        prieten = (Utilizator) tableViewPrieteni.getSelectionModel().getSelectedItem();
        if (prieten != null) {
            initMessages(prieten);
            messageListView.setItems(messages);
        }
    }

    @Override
    public void update(UtilizatorChangeEventType utilizatorChangeEventType) {
        initFriends();
        initMessages(tableViewPrieteni.getSelectionModel().getSelectedItem());
    }
}