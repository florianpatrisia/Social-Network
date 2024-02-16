package com.example.laborator.controller;

import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.ValidationException;
import com.example.laborator.service.UtilizatorService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;

public class AddUtilizatorController {
    @FXML
    public TextField textFieldId;
    @FXML
    public TextField textFieldFirstName;
    @FXML
    public TextField textFieldLastName;
    Stage dialogStageAdd;
    UtilizatorService service;
    public void setService(UtilizatorService service, Stage stage) {
        this.service = service;
        this.dialogStageAdd = stage;
        //System.out.println(this.service.findAll());
    }

    @FXML
    private void saveUtilizator() {
        String first_name = textFieldFirstName.getText();
        String last_name = textFieldLastName.getText();
        Utilizator newUtilizator = new Utilizator(first_name, last_name);

        try {
            Optional<Utilizator> saved = this.service.save(newUtilizator);
            //if (saved.isEmpty())
              //  dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Slavare Utilizator", "Utilizatorul a fost salvat.\n Id-ul utilizatorului este "+newUtilizator.getId());
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStageAdd.close();
    }
    @FXML
    public void handleCancel(){
        dialogStageAdd.close();
    }
}
