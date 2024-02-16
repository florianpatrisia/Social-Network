package com.example.laborator;

import com.example.laborator.controller.MessageController;
import com.example.laborator.domain.FriendRequest;
import com.example.laborator.domain.Tuple;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.repository.UserDBPagingRepository;
import com.example.laborator.repository.UserDBRepository;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class HomeContoller implements Observer<UtilizatorChangeEventType>{
    protected UtilizatorService utilizatorService;
    protected PrietenieService prietenieService;
    protected FriendRequestService requestService;
    protected MessageService messageService;
    protected UserDBRepository utilizatorRepo;
    protected Utilizator utilizator_selectat;
    // add, delete, update
    @FXML
    public Button buttonMessages;
    @FXML
    public Button buttonSendFriendRequest;
    @FXML
    public Button buttonFriendRequest;
    @FXML
    Button previousButton;
    @FXML
    Button previousButton1;
    @FXML
    Button nextButton;
    @FXML
    Button nextButton1;

    @FXML
    public TableColumn<Utilizator, String> prietenColumnFirstName;
    @FXML
    public TableColumn <Utilizator, String> prietenColumnLastName;
    @FXML
    public TableColumn <Utilizator, Long> prietenColumnId;
    @FXML
    public TableView<Utilizator> tableViewPrieteni;
    @FXML
    public TableColumn <Utilizator, String> ne_prietenColumnFirstName;
    @FXML
    public TableColumn <Utilizator, String> ne_prietenColumnLastName;
    @FXML
    public TableColumn <Utilizator, Long> ne_prietenColumnId;
    @FXML
    public TableView<Utilizator> tableViewNe_Prieteni;
    @FXML
    public Label messageToUser;
    @FXML
    public TextField nrElem;
    ObservableList<Utilizator> modelPrieteni = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelNEPrieteni = FXCollections.observableArrayList();
    Stage nuj;
    public int currentPageUtilizatori = 0;
    public int elementsPerPageUtilizatori = 5;
    public int totalNumberOfElementsUtilizatori = 0;
    public int currentPagePrieteni = 0;
    public int elementsPerPagePrieteni = 5;
    public int totalNumberOfElementsPrieteni = 0;


    public void setService(UtilizatorService utilizatorService, PrietenieService prietenieService, FriendRequestService requestS, MessageService messageS, Utilizator utilizator, UserDBPagingRepository repoU) {
        this.utilizatorService = utilizatorService;
        this.prietenieService=prietenieService;
        this.requestService=requestS;
        this.messageService=messageS;
        this.utilizator_selectat=utilizator;
        this.utilizatorRepo=repoU;

        this.utilizatorService.addObserver(this);
        this.prietenieService.addObserver(this);

        initPrieteni();
        initNe_Prieteni();
    }

    @FXML
    public void initialize(){
        prietenColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        prietenColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        prietenColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));

        ne_prietenColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        ne_prietenColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        ne_prietenColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));

        tableViewPrieteni.setItems(modelPrieteni);
        tableViewNe_Prieteni.setItems(modelNEPrieteni);

        this.nrElem.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    String text = nrElem.getText();
                    elementsPerPageUtilizatori=Integer.valueOf(text);
                    elementsPerPagePrieteni=Integer.valueOf(text);
                    initPrieteni();
                    initNe_Prieteni();
                    nrElem.clear();
                }
            }
        });
    }

//    private void initPrieteni() {
//        List<Long> toti_utilizatori =prietenieService.getFriends(utilizator_selectat.getId());
//        List<Utilizator> utilizatori=new ArrayList<>();
//        toti_utilizatori.forEach(id->{
//            Utilizator u=utilizatorService.findID(id);
//            u.setId(id);
//            utilizatori.add(u);
//        });
//
//        modelPrieteni.setAll(utilizatori);
//    }
    private void initNe_Prieteni() {
        Page<Utilizator> utilizatoriPage=utilizatorService.utilizatoriOnPage(new Pageable(currentPageUtilizatori, elementsPerPageUtilizatori));
        totalNumberOfElementsUtilizatori = utilizatoriPage.getTotalNumberOfElements();
        handlePagingNavigationChecksUtilizatori();

        List<Utilizator> utilizatoriList=StreamSupport.stream(utilizatoriPage.getElementsOnPage().spliterator(),false).toList();
        modelNEPrieteni.setAll(utilizatoriList);

//        Iterable<Utilizator> toti_utilizatorii_ne_prieteni = utilizatorService.findAll();
//        List<Utilizator> allUsersList = StreamSupport.stream(toti_utilizatorii_ne_prieteni.spliterator(), false).toList();
//        modelNEPrieteni.setAll(allUsersList);
//        System.out.println(allUsersList);
//        modelNEPrieteni.setAll(allUsersList);

    }

    private void initPrieteni() {
        Page<Utilizator> prieteniPage = prietenieService.getPrieteniPage(utilizator_selectat.getId(), new Pageable(currentPagePrieteni, elementsPerPagePrieteni));
        totalNumberOfElementsPrieteni = prieteniPage.getTotalNumberOfElements();
        handlePagingNavigationChecksPrieteni();

        List<Utilizator> prieteniList = StreamSupport.stream(prieteniPage.getElementsOnPage().spliterator(), false).toList();
        modelPrieteni.setAll(prieteniList);

//        Page<Prietenie> prieteniPage=prietenieService.prieteniiOnPage(new Pageable(currentPageUtilizatori, elementsPerPageUtilizatori));
//        totalNumberOfElementsUtilizatori = prieteniPage.getTotalNumberOfElements();
//        handlePagingNavigationChecks();
        //modelPrieteni.setAll(utilizatori);
    }
    public void goToPreviousPageUtilizatori(){
        currentPageUtilizatori--;
        initNe_Prieteni();
    }
    public void goToNextPageUtilizatori(){
        currentPageUtilizatori++;
        initNe_Prieteni();
    }
    public void goToPreviousPagePrieteni(){
        currentPagePrieteni--;
        initPrieteni();
    }
    public void goToNextPagePrieteni(){
        currentPagePrieteni++;
        initPrieteni();
    }
    private void handlePagingNavigationChecksUtilizatori(){
        previousButton.setDisable(currentPageUtilizatori == 0);
        nextButton.setDisable((currentPageUtilizatori + 1) * elementsPerPageUtilizatori >= totalNumberOfElementsUtilizatori);
    }
    private void handlePagingNavigationChecksPrieteni(){
        previousButton.setDisable(currentPagePrieteni == 0);
        nextButton.setDisable((currentPagePrieteni + 1) * elementsPerPagePrieteni >= totalNumberOfElementsPrieteni);
    }

    @FXML
    public void handle_Messages(ActionEvent actionEvent)
    {
        try
        {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(MessageController.class.getResource("messages-view.fxml"));
//            AnchorPane layout = loader.load();
//            Stage stageMessage = new Stage();
//            stageMessage.setTitle("Mesajeeee");
//            stageMessage.initModality(Modality.WINDOW_MODAL);
//            stageMessage.setScene(new Scene(layout));
//            MessageController controller=loader.getController();
//            controller.setService(utilizatorService,prietenieService, requestService, messageService, utilizator_selectat);
//            stageMessage.setWidth(800);
//            stageMessage.show();



            FXMLLoader loader = new FXMLLoader(MessageApplication.class.getResource("messages-view.fxml"));
            AnchorPane root = loader.load();
            MessageController controller=loader.getController();
            controller.setService(utilizatorService,prietenieService, requestService, messageService, utilizator_selectat);
            Scene scene = new Scene(root, 800, 800);
            Stage stage = new Stage();
            stage.setTitle("Mesajeeeeeeee");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @FXML
    public void handle_sendFriendRequests(ActionEvent actionEvent)
    {
        Utilizator selected =tableViewNe_Prieteni.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            Tuple<Long,Long> id_request = new Tuple<>(utilizator_selectat.getId(),selected.getId());
            FriendRequest friendRequest = new FriendRequest(id_request);
            friendRequest.setId(id_request);
            try
            {
                requestService.save(friendRequest);
                initNe_Prieteni();
                this.messageToUser.setText("Cerere de prietenie trimisa cu succes");
                this.messageToUser.setTextFill(Color.DARKGREEN);
            }catch (Exception e) {
                this.messageToUser.setText(e.getMessage());
                //this.messageToUser.setText("Eroare la trimiterea cererii");
                this.messageToUser.setTextFill(Color.RED);
            }
        }
        else
        {
            this.messageToUser.setText("Nu ati selectat nici-un utilizator");
            this.messageToUser.setTextFill(Color.DARKRED);
        }
    }

    public void handle_friendRequests(ActionEvent actionEvent)
    {
        try {
            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(FriendRequestController.class.getResource("friend-request-view.fxml"));
            AnchorPane layout = loader2.load();
            Stage utilizatorStage = new Stage();
            utilizatorStage.setTitle("Toate cererile de prietenie primite si trimise!!");
            utilizatorStage.initModality(Modality.WINDOW_MODAL);
            utilizatorStage.setScene(new Scene(layout));
            FriendRequestController controller=loader2.getController();
            controller.setService(requestService,utilizatorRepo, utilizator_selectat);
            utilizatorStage.setWidth(800);
            utilizatorStage.show();
        }
       catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(UtilizatorChangeEventType utilizatorChangeEventType) {
        initPrieteni();
        initNe_Prieteni();
        //tableViewPrieteni.refresh();
    }
}
