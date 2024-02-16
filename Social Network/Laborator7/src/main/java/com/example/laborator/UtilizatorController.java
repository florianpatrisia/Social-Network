package com.example.laborator;
import com.example.laborator.controller.AddUpdateUserController;
import com.example.laborator.controller.MessageAlert;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.service.UtilizatorService;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorController implements Observer<UtilizatorChangeEventType> {

    UtilizatorService service;
    Utilizator utilizator_selectat;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    Stage dialogStage;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator, String> tableColumnLastName;

    public void setUtilizatorController(UtilizatorService service) {
        // merge
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }

    public void setService(UtilizatorService service, Stage stage, Utilizator utilizator) {
        // nu merge
        this.service = service;
        this.dialogStage = stage;
        this.utilizator_selectat = utilizator;
    }

    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
            }

    private void initModel() {
        Iterable<Utilizator> utilizatori = service.findAll();
        List<Utilizator> utilizatoriList = StreamSupport.stream(utilizatori.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(utilizatoriList);
    }
    @FXML
    public void handleAddUtilizator(ActionEvent actionEvent) {
        afiseazaMesajEditareUtilizator(null);
    }
    @FXML
    public void handleDeleteUtilizator(ActionEvent actionEvent) {
        Utilizator selected = tableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Optional<Utilizator> deleted = service.delete(selected.getId());

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Utilizatorul a fost sters cu succes!");
        } else
            MessageAlert.showErrorMessage(null, "Nu ati selectat nici-un utilizator!");
    }
    @FXML
    public void handleUpdateUtilizator(ActionEvent actionEvent) {
        Utilizator selected = tableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            afiseazaMesajEditareUtilizator(selected);

        } else MessageAlert.showErrorMessage(null, "Nu ati selectat nici-un utilizator!");
    }

    @Override
    public void update(UtilizatorChangeEventType utilizatorChangeEventType) {
        initModel();
    }
    private void afiseazaMesajEditareUtilizator(Utilizator utilizator) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("edit-utilizator-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editeaza Utilizator");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            AddUpdateUserController utilizatorController = loader.getController();
            utilizatorController.setService(service, dialogStage, utilizator);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

