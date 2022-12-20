package com.samu.leo;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ControllerInfo {

    @FXML
    Button BACK_TO_ENTRATA = new Button();

    @FXML
    public void initialize() {
        //setto l'evento che succede una volta pigiato il bottone
        this.BACK_TO_ENTRATA.setOnAction(evento -> {   try {  App.setRoot("LOGIN");  } catch (IOException e) {  e.printStackTrace();  }   });
    }
}
