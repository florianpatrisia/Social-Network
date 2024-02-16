package com.example.laborator;


import com.example.laborator.controller.MessageController;
import com.example.laborator.domain.FriendRequest;
import com.example.laborator.domain.Message;
import com.example.laborator.domain.Prietenie;
import com.example.laborator.domain.Utilizator;
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

public class MessageApplication  extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String url="jdbc:postgresql://localhost:5433/socialnetwork";
        String username = "postgres";
        String password = "1234";

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

        FXMLLoader fxmlLoader = new FXMLLoader(MessageApplication.class.getResource("message-view.fxml"));
        AnchorPane socialNetworkLayout = fxmlLoader.load(); // Încarcă conținutul fișierului FXML într-un AnchorPane
        stage.setScene(new Scene(socialNetworkLayout, 500, 400)); // Setează scena folosind conținutul încărcat
        MessageController controller = fxmlLoader.getController();
        controller.setService( utilizatorService,servicePrietenie, requestService, messageService, null);
        stage.setTitle("Messages");
        stage.show();

    }




}

