package com.example.laborator;

import com.example.laborator.domain.FriendRequest;
import com.example.laborator.domain.Tuple;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.repository.UserDBRepository;
import com.example.laborator.service.FriendRequestService;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

//import java.awt.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestController implements Observer<UtilizatorChangeEventType> {
    protected FriendRequest friendRequest;
    protected UserDBRepository utilizatorRepo;
    Utilizator utilizator_selectat;
    protected FriendRequestService requestService;
    @FXML
    public Button buttonAccept;
    @FXML
    public Button buttonDelete;
    @FXML
    public TableView<FriendRequest> tableViewRequestSent;
    @FXML
    public TableView<FriendRequest> tableViewRequestReceived;
    @FXML
    public TableColumn<FriendRequest, String> ToColumnSent;
    @FXML
    public TableColumn<FriendRequest, String> StatusColumnSent;
    @FXML
    public TableColumn<FriendRequest, String> DateColumnSent;
    @FXML
    public TableColumn<FriendRequest, String> FromColumnReceived;
    @FXML
    public TableColumn<FriendRequest, String> StatusColumnReceived;
    @FXML
    public TableColumn<FriendRequest, String> DateColumnReceived;
    ObservableList<FriendRequest> modelSent = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelReceived = FXCollections.observableArrayList();
    public Label messageToUserForRequest;
    //@FXML
    //private Label messageToUserForRequest;
    //Stage dialogStageRequest;

    public void setService(FriendRequestService requestService,UserDBRepository repo, Utilizator utilizator)
    {
        this.requestService=requestService;
        this.utilizatorRepo=repo;
        this.utilizator_selectat=utilizator;
        //this.requestService.setUtilizatorRepo(repo);
        //this.dialogStageRequest=dialogStage;

        this.requestService.addObserver(this);
        initSent();
        initReceived();
    }

    public void initialize()
    {
        ToColumnSent.setCellValueFactory(new PropertyValueFactory<>("to"));
        StatusColumnSent.setCellValueFactory(new PropertyValueFactory<>("status"));
        DateColumnSent.setCellValueFactory(new PropertyValueFactory<>("date"));

        FromColumnReceived.setCellValueFactory(new PropertyValueFactory<>("from"));
        StatusColumnReceived.setCellValueFactory(new PropertyValueFactory<>("status"));
        DateColumnReceived.setCellValueFactory(new PropertyValueFactory<>("date"));

        this.messageToUserForRequest = new Label();

        tableViewRequestSent.setItems(modelSent);
        tableViewRequestReceived.setItems(modelReceived);
    }
    private void initSent() {
        Iterable<FriendRequest> friendRequests = requestService.findAll();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator() , false)
                .filter(x -> (x.getId().getLeft().equals(utilizator_selectat.getId())))
                .collect(Collectors.toList());
        modelSent.setAll(friendRequestList);
    }

    private void initReceived() {
        Iterable<FriendRequest> friendRequests = this.requestService.findAll();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator() , false)
                .filter(x -> (x.getId().getRight().equals(utilizator_selectat.getId())))
                .collect(Collectors.toList());
        modelReceived.setAll(friendRequestList);
    }
    @FXML
    public void handleAcceptFriendRequest(ActionEvent actionEvent)
    {
        FriendRequest selected = this.tableViewRequestReceived.getSelectionModel().getSelectedItem();
        //System.out.println("controller request "+ selected);
        if (selected != null)
        {
            try
            {
                requestService.accept(selected.getFrom(), utilizator_selectat.getId());
                this.messageToUserForRequest.setText("Cerere de prietenie acceptata!");
                this.messageToUserForRequest.setText(String.valueOf(Color.GREEN));
                initSent();
                initReceived();
            } catch (Exception e)
            {
                this.messageToUserForRequest.setText(e.getMessage());
                this.messageToUserForRequest.setText(String.valueOf(Color.GREEN));
                System.out.println(e);
            }
        }
        else
        {
            this.messageToUserForRequest.setText("No request selected!");
            //this.messageToUserForRequest.setTextFill(Color.DARKRED);
        }
    }
    @FXML
    public void handleDeleteFriendRequest(ActionEvent actionEvent)
    {
        FriendRequest selected = this.tableViewRequestReceived.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            try
            {
                Tuple<Long, Long> id_request=new Tuple<>(selected.getFrom(), utilizator_selectat.getId());
                requestService.delete(id_request);
                this.messageToUserForRequest.setText("Cerere de prietenie stearsa!");
                this.messageToUserForRequest.setText(String.valueOf(Color.GREEN));
                initSent();
                initReceived();
            } catch (Exception e)
            {
                this.messageToUserForRequest.setText(e.getMessage());
                this.messageToUserForRequest.setText(String.valueOf(Color.GREEN));
                System.out.println(e);
            }
        }
        else
        {
            this.messageToUserForRequest.setText("No request selected!");
            //this.messageToUserForRequest.setTextFill(Color.DARKRED);
        }
    }


    @Override
    public void update(UtilizatorChangeEventType utilizatorChangeEventType) {

    }
}
