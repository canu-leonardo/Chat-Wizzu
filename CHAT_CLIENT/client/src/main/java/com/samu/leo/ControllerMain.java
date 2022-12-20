package com.samu.leo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.util.ArrayList;

public class ControllerMain {
    Client_in Client;
    @FXML
    private Pane APPLICAZIONE = new Pane();                         //tutta la finestra
    @FXML
    private Pane PARTE_SINISTRA = new Pane();                       //pane con la parte sinistra del'applicazione
    @FXML 
    private ListView<Button> TUTTI_GLI_UTENTI = new ListView<>();   //Lista di tutti gli utenti online, compresa la chat pubblica
    @FXML
    private Label NOME_DELLA_CHAT = new Label();                    //Nome della nostra applicazione ("WIZZU")
    @FXML
    private ImageView LOGO = new ImageView();                       //Logo della nostra applicazione
    @FXML   
    private Label NICKNAME = new Label();                           //nickname dell'utilizzatore
    @FXML
    private Pane CHAT = new Pane();                                 //pane con la parte destra della chat
    @FXML
    private ListView<Label> CHAT_MESSAGGI = new ListView<>();       //messaggi della chat selezionata
    @FXML 
    private TextField TEXT_IN = new TextField();                    //input del testo 
    @FXML
    private Button BOTTONE_INVIO = new Button();                    //bottonbe di invio
    @FXML
    private Label NOME_CHAT = new Label();                          //nickname dell'utente al quale stiamo scrivendo


    //COSTRUTTORE
    public ControllerMain(Client_in client){
        this.Client = client;

        client.nicknameDaCambiare = () -> {     //instanzio il metodo che si verificherà all'avvio della lambda
            //eseguimo questa opzione con il metodo "Platform.runLater" dato che il cambiamento della componente fxml su un altro thread genererebbe un errore 
            Platform.runLater(() -> {   this.NICKNAME.setText(Client_out.meMedesimo.nickname); 
                                        this.NICKNAME.setPadding(new Insets(0, 15, 0, 0));    }); 
        };

        Client.listUpdated = () -> { this.updateClientList();  };               //instanzio il metodo che si verificherà all'avvio della lambda
        Client.messageIsArriverd = message -> { updateCurrentChat(message); };  //instanzio il metodo che si verificherà all'avvio della lambda
    }

    @FXML
    public void initialize() {
        //cose che succedono all'accenzione di questa pagina
        this.BOTTONE_INVIO.setOnAction(evento -> {    Invia();   });   //aggiungo l'evento di quando verrà pigiato il bottone
        
        this.NOME_DELLA_CHAT.setText("Wizzu");                                  //Setto Il nome della CHAT
        this.NOME_DELLA_CHAT.setFont(new Font("Helvetica", 35));                // gli metto il font e la dimensione
        this.NOME_DELLA_CHAT.setAlignment(Pos.TOP_RIGHT);                       // lo alineo a destra
        this.NOME_DELLA_CHAT.setPadding(new Insets(0, 15, 0, 0));               // setto il Padding

        this.NOME_CHAT.setText("Taverna");                              //setto la chat "Taverna" di default

        this.updateClientList();            //aggiorno la lista dei client anche all'inizio, così da sincronizzarci subito con tutti quelli
    }  

    //metodo all'accenzione del pulzante di invio
    public void Invia() {
        Label messaggio = new Label();         
        messaggio.setText(TEXT_IN.getText());
        messaggio.getStyleClass().add("myMessage");
        messaggio.setAlignment(Pos.CENTER_RIGHT);
        this.TEXT_IN.setText("");
        CHAT_MESSAGGI.getItems().add(messaggio);
        if ( this.NOME_CHAT.getText().equals("Taverna")){
            Client_out.messaggioATutti(messaggio.getText());
        }else{
            Client_out.messaggioSingolo(messaggio.getText(), this.NOME_CHAT.getText());
        }
        
    }

    //metodo che aggiorna la lista dei client
    public void updateClientList(){

        Platform.runLater(() -> {    this.TUTTI_GLI_UTENTI.getItems().clear();  });
        
        ArrayList<Button> bottoniUtente = new ArrayList<Button>();

        Button Taverna = new Button();
        Taverna.setText("Taverna");
        Taverna.setPrefWidth(250.0);
        Taverna.getStyleClass().add("User");
        Taverna.setOnAction(evento -> { this.updateTaverna(); });
        bottoniUtente.add(Taverna);

        for (String utente : Client_in.utenti) {
            if ( ! utente.equals(Client_out.meMedesimo.nickname) && ! utente.equals("VOID")){
                Button persona = new Button();
                persona.setText(utente);
                persona.setPrefWidth(250.0);
                persona.getStyleClass().add("User");
                persona.setOnAction(evento -> {  updateChat(utente);  });
                bottoniUtente.add(persona);       
            }
        }
        Platform.runLater(() -> {    this.TUTTI_GLI_UTENTI.getItems().addAll(bottoniUtente);  });
    }

    //metodo che aggiorna la chat corrente
    public void updateCurrentChat(Messaggio messaggio){
        if (messaggio.getDestinatario().equals("Taverna") && this.NOME_CHAT.getText().equals("Taverna")){
            this.updateTaverna();
        }else if (messaggio.getMittente().equals(this.NOME_CHAT.getText())){
            this.updateChat(messaggio.getMittente());
        }else if ( ! messaggio.getDestinatario().equals("Taverna")){
            Platform.runLater(() -> {  
                Alert alert = new Alert(AlertType.INFORMATION);
                Rectangle2D size = Screen.getPrimary().getBounds();             
                alert.setTitle("Wizzu");
                alert.setHeaderText("Ti è arrivata una lettera");
                alert.setContentText("Te l'ha mandata proprio " + messaggio.getMittente());
                alert.setX(size.getWidth());
                alert.setY(size.getHeight());
                alert.showAndWait();
            });
            
        }
    }

    //metodo che aggiorna una chat normale 
    public void updateChat(String user) {

        this.NOME_CHAT.setText(user);                   //setto il nome della chat con il nme dello user al quale sto scrivendo

        ArrayList<Label> messaggi = new ArrayList<>();

        Platform.runLater(() -> {   this.CHAT_MESSAGGI.getItems().clear();   });

        for (Messaggio messaggio : Client_in.messaggi) {
            if(messaggio.getDestinatario().equals(user) && messaggio.getMittente().equals(Client_out.meMedesimo.nickname)){
                Label m = new Label(messaggio.getCorpo());
                m.getStyleClass().add("myMessage");
                m.setAlignment(Pos.CENTER_RIGHT);
                m.setLayoutX(this.CHAT_MESSAGGI.getWidth() - m.getWidth() );
                messaggi.add(m);
            }else if (messaggio.getMittente().equals(user)){
                Label m = new Label(messaggio.getCorpo());
                m.getStyleClass().add("OthersMessage");
                m.setAlignment((Pos.CENTER_LEFT));
                messaggi.add(m);
            }
        }

        Platform.runLater(() -> {  this.CHAT_MESSAGGI.getItems().addAll(messaggi);   });
        
    }

    //metodo che aggiorna la taverna
    public void updateTaverna() {
        this.NOME_CHAT.setText("Taverna");                   //setto il nome della chat con il nme dello user al quale sto scrivendo

        ArrayList<Label> messaggi = new ArrayList<>();

        for (Messaggio messaggio : Client_in.multicast) {
            if(messaggio.getMittente().equals(Client_out.meMedesimo.nickname)){
                Label m = new Label(messaggio.getCorpo());
                m.getStyleClass().add("myMessage");

                messaggi.add(m);
            }else{
                Label m = new Label(messaggio.getMittente() + ": \n" + messaggio.getCorpo());
                m.getStyleClass().add("OthersMessage");
                m.setAlignment((Pos.CENTER_LEFT));
                messaggi.add(m);
            }
        }

        Platform.runLater(() -> {   this.CHAT_MESSAGGI.getItems().setAll(messaggi);   });
       
    }
}