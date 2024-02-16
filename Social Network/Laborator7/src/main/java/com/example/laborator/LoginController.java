package com.example.laborator;

import com.example.laborator.controller.AddUtilizatorController;
import com.example.laborator.controller.MessageAlert;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.ValidationException;
import com.example.laborator.repository.UserDBPagingRepository;
import com.example.laborator.repository.UserDBRepository;
import com.example.laborator.service.FriendRequestService;
import com.example.laborator.service.MessageService;
import com.example.laborator.service.PrietenieService;
import com.example.laborator.service.UtilizatorService;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController implements Observer<UtilizatorChangeEventType> {
    @FXML
    protected TextField id_user;
    @FXML
    protected TextField password_user;
    @FXML
    protected Button login;
    @FXML
    protected Button singup;
    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    FriendRequestService requestService;
    MessageService messageService;
    UserDBRepository utilizatorRepo;
    UserDBPagingRepository utilizatorPagingRepo;

    private Utilizator utilizator_selectat;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    Stage dialogStageLogin;


    public void setLoginService(UtilizatorService serviceUtilizator,PrietenieService servicePrietenie, FriendRequestService requestS,MessageService messageS, Stage dialogStage, UserDBPagingRepository repoU) {
        this.utilizatorService = serviceUtilizator;
        this.prietenieService=servicePrietenie;
        this.requestService=requestS;
        this.messageService=messageS;
        this.dialogStageLogin=dialogStage;
        this.utilizatorPagingRepo=repoU;
        //this.utilizatorRepo=repoU;
        //this.utilizator_selectat=utilizator;
        initModel();
    }
    private void initModel() {
        Iterable<Utilizator> utilizatori = utilizatorService.findAll();
        List<Utilizator> utilizatoriList = StreamSupport.stream(utilizatori.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(utilizatoriList);
    }

    public void handle_login() {
        Long id = Long.valueOf(id_user.getText());
        String password = password_user.getText();
        //System.out.println(utilizatorService.findOne(id+10));

        try {
            Utilizator find_user=null;
            find_user=utilizatorService.findID(id);
            if (find_user!=null)
                find_user.setId(id);

            if (find_user==null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "ID utilizator invalid!", "Incearca din nou!");
            if(!Objects.equals(password, "admin"))
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Parola invalida", "Incearca din nou!");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("home-view.fxml"));
            AnchorPane layout = loader.load();

            Stage LoginStage = new Stage();
            LoginStage.setTitle("Urmatorea pagina dupa ce te logezi");
            LoginStage.initModality(Modality.WINDOW_MODAL);
            LoginStage.setScene(new Scene(layout));

            HomeContoller controller=loader.getController();
            controller.setService(utilizatorService, prietenieService,requestService, messageService, find_user, utilizatorPagingRepo);//, utilizatorStage, utilizator_selectat);

            LoginStage.show();

        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //dialogStage.close();
    }

    public void handle_signup() {
        try {
            //System.out.println(utilizatorService.findAll());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("add-utilizator-view.fxml"));
            AnchorPane layout = loader.load();

            Stage utilizatorStage = new Stage();
            utilizatorStage.setTitle("Adauga Utilizator Nou");
            utilizatorStage.initModality(Modality.WINDOW_MODAL);
            utilizatorStage.setScene(new Scene(layout));

           //FXMLLoader utilizatorLoader = new FXMLLoader(getClass().getResource("add-utilizator-view.fxml"));
            //AnchorPane utilizatorLayout = utilizatorLoader.load();

            AddUtilizatorController controller=loader.getController();
            // daca nu mai merge atunci gtrebuie dialog stage
            controller.setService(utilizatorService, utilizatorStage);//?? nu cred ca e bine
            //controller.setService(utilizatorService, utilizatorStage);

            utilizatorStage.show();
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afiseazaListaUtilizatori(Utilizator utilizator) {
        try {
            //System.out.println(utilizatorService.findAll());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("utilizator-view.fxml"));
            AnchorPane layout = loader.load();
            //System.out.println(getClass().getResource("utilizator-view.fxml"));

            Stage utilizatorStage = new Stage();
            utilizatorStage.setTitle("Utilizatorii");
            utilizatorStage.initModality(Modality.WINDOW_MODAL);
            utilizatorStage.setScene(new Scene(layout));

            FXMLLoader utilizatorLoader = new FXMLLoader(getClass().getResource("utilizator-view.fxml"));
            AnchorPane utilizatorLayout = utilizatorLoader.load();

            AddUtilizatorController controller=utilizatorLoader.getController();
            controller.setService(utilizatorService, dialogStageLogin);

            utilizatorStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleAddUtilizator() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("add-utilizator-view.fxml"));
            AnchorPane layout = loader.load();

            Stage utilizatorStage = new Stage();
            utilizatorStage.setTitle("Adauga Utilizator Nou");
            utilizatorStage.initModality(Modality.WINDOW_MODAL);
            utilizatorStage.setScene(new Scene(layout));

            FXMLLoader utilizatorLoader = new FXMLLoader(getClass().getResource("add-utilizator-view.fxml"));
            AnchorPane utilizatorLayout = utilizatorLoader.load();

            AddUtilizatorController controller = utilizatorLoader.getController();
            controller.setService(utilizatorService, dialogStageLogin);


            utilizatorStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UtilizatorChangeEventType utilizatorChangeEventType) {

    }
}