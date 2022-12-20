package com.samu.leo;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ControllerLogIn {

    @FXML
    Button INVIO = new Button();                //bottone di invio del nickname

    @FXML
    TextField IN_NICKNAME = new TextField();    //campo in cui inserire il nickname

    @FXML
    Pane PAGE = new Pane();                     //pane della pagina

    @FXML
    Label NICKNAME_ESITO = new Label();         //label nel quale comparirà l'esito della richiesta del nickname

    @FXML
    Button InfoButton = new Button();           //bottone per visualizzare la pagina di info 

    //COSTRUTTORE
    public ControllerLogIn(Client_in client){
        client.nicknameAccepted = isAccepted -> {   //instanzio il metodo che si verificherà all'avvio della lambda
                                                    // ( lo chiamerò nella classe Client_in riga   )
            if (isAccepted){           
                try {    App.setRoot("CHAT");    } catch (IOException e) {    System.out.println(e);    } //metodo per cambiare pagina
            }
            else{
            //eseguimo questa opzione con il metodo "Platform.runLater" dato che il cambiamento della componente fxml su un altro thread genererebbe un errore 
                Platform.runLater( () -> {     
                    this.IN_NICKNAME.setText("");
                    this.NICKNAME_ESITO.setText("Quel nome in codice era già in uso!");
                    this.NICKNAME_ESITO.setTextFill(Color.RED);
                });
            }        
        };
    }
    
    //metodo che viene ciamato all'inizializzazione della pagina
    @FXML
    public void initialize() {
        //setto l'evento che succede una volta pigiato il bottone
        this.INVIO.setOnAction(evento -> {   Client_out.richiestaNickname(IN_NICKNAME.getText());   }); //invio della richiesta del nickname
        this.InfoButton.setOnAction(evento -> {   try {  App.setRoot("INFO");  } catch (IOException e) {  e.printStackTrace();  }   });
    } 
}
