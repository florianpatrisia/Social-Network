package com.example.laborator.controller;

import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.ValidationException;
import com.example.laborator.service.UtilizatorService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class AddUpdateUserController {
    @FXML
    public TextField textFieldId;
    @FXML
    public TextField textFieldFirstName;
    @FXML
    public TextField textFieldLastName;
    Stage dialogStage;
    Utilizator utilizator_selectat;
    UtilizatorService serviceDB;
    public void setService(UtilizatorService service, Stage stage, Utilizator utilizator) {
        this.serviceDB = service;
        this.dialogStage = stage;
        this.utilizator_selectat = utilizator;

        if (utilizator_selectat!=null) {
            dialogStage.setTitle("Update Utilizator");
            setFields(utilizator_selectat);
            textFieldId.setEditable(false);
        }
        else {
            dialogStage.setTitle("Adauga Utilizator");

            //O PROSTIE
            //Utilizator u=new Utilizator("a", "b");
            //long id=u.getId()-1;
            //textFieldId.setText(Long.toString(id));

            textFieldId.setEditable(false);
        }
    }


    public void handleSaveAndUpdate() {
        String first_name = textFieldFirstName.getText();
        String last_name = textFieldLastName.getText();
        Utilizator newUtilizator = new Utilizator(first_name, last_name);

        if(null == utilizator_selectat)
            saveUtilizator(newUtilizator);
        else
            updateUtilizator(newUtilizator);
    }


    private void updateUtilizator(Utilizator utilizator)
    {
        try {
            Optional<Utilizator> updated = this.serviceDB.update(utilizator.getId(), utilizator);
            if (updated.isEmpty())
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Modificare Utilizator", "Utilizatorul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }


    private void saveUtilizator(Utilizator utilizator) {
        try {
            Optional<Utilizator> saved = this.serviceDB.save(utilizator);
            if (saved.isEmpty())
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Slavare Utilizator", "Utilizatorul a fost salvat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    public void setFields(Utilizator utilizator) {
        textFieldId.setText(utilizator.getId().toString());
        textFieldFirstName.setText(utilizator.getFirstName());
        textFieldLastName.setText(utilizator.getLastName());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
