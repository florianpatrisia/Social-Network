package com.example.laborator;
import com.example.laborator.domain.*;
import com.example.laborator.domain.validators.*;
import com.example.laborator.repository.*;
import com.example.laborator.service.FriendRequestService;
import com.example.laborator.service.MessageService;
import com.example.laborator.service.PrietenieService;
import com.example.laborator.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class StartApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{

    Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();
    UserDBRepository repoUtilizator = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234", utilizatorValidator);
    UserDBPagingRepository utilizatorPagingRepo=new UserDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234");
    UtilizatorService utilizatorService=new UtilizatorService(utilizatorPagingRepo);

    Validator<Prietenie> prietenieValidator = new PrietenieValidator();
    PrietenieDBRepository repoPrietenie = new PrietenieDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234", prietenieValidator);
    PrietenieDBPagingRepository prieteniePagingRepo=new PrietenieDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234", prietenieValidator);
    PrietenieService servicePrietenie=new PrietenieService(prieteniePagingRepo, utilizatorPagingRepo, utilizatorService);

    Validator<FriendRequest> requestValidator=new FriendRequestValidator();
    FriendRequestDBRepository repoRequest=new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234",repoUtilizator, repoPrietenie);
    FriendRequestService requestService=new FriendRequestService(repoRequest, repoUtilizator, repoPrietenie);


    Validator<Message> messageValidator=new MessageValidator();
    MessageDBRepository messageRepo=new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234", messageValidator);
    MessageDBPagingRepository messagePagingRepo=new MessageDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "asdf1234", messageValidator);
    MessageService messageService=new MessageService(messagePagingRepo);
    //initView(primaryStage);

    FXMLLoader messageLoader = new FXMLLoader();
//    messageLoader.setLocation(getClass().getResource("messages-view.fxml"));
    messageLoader.setLocation(getClass().getResource("login-view.fxml"));
    //messageLoader.setLocation(getClass().getResource("home-view.fxml"));
    AnchorPane messageTaskLayout = messageLoader.load();

     primaryStage.setScene(new Scene(messageTaskLayout));

//     Utilizator u=null;
//     u=utilizatorService.findID(1L);
//     u.setId(1L);

//        HomeContoller contoller=messageLoader.getController();
//        contoller.setService(utilizatorService, servicePrietenie, requestService, messageService, u, repoUtilizator);

//        MessagesController controller=messageLoader.getController();
//        controller.setService(utilizatorService, servicePrietenie, requestService, messageService, u);

        LoginController loginController=messageLoader.getController();
        loginController.setLoginService(utilizatorService,servicePrietenie,requestService,messageService,  primaryStage, utilizatorPagingRepo);



        //Utilizator u=null;
        //UtilizatorController controller=messageLoader.getController();
        //merge: controller.setUtilizatorController(utilizatorService);
        //NU MERGEEEEEE: controller.setService(utilizatorService, primaryStage, u);

//        FriendRequestController controller=messageLoader.getController();
//        controller.setService(requestService,repoUtilizator ,u);

//        Long fromUserId = 1L;
//        ArrayList<Long> toUserIds = new ArrayList<>();
//        toUserIds.add(2L);
//        toUserIds.add(3L);
//        String messageContent = "Salut! Cum merg lucrurile?";
//        String messageData = "2023-01-01";
//
//        Long replyMessageId = 101L;
//
//        Message message1 = new Message(fromUserId, toUserIds, messageContent);
//        messageService.findAll();
//        messageService.save(message1);
//        System.out.println(message1);
//
//        Message message2 = new Message(fromUserId, toUserIds, messageContent, messageData);
//        System.out.println(message2);

        primaryStage.setWidth(800);
        primaryStage.show();




}

}