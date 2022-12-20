package com.samu.leo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * My JavaFX App -- wizzu
*/
public class App extends Application {

    private static Scene scene;
    private static Client_in ascoltatore = new Client_in();
    private static Client_out client;

    private static ControllerMain controllerMain;
    private static ControllerLogIn controllerLogIn ;

    //SETUP della finestra
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("LOGIN"));
        //stage.getIcons().add(new Image(App.class.getResourceAsStream("/Logo.png")));
        stage.setScene(scene);
        stage.setTitle("Wizzu");
        stage.setResizable(false);
        stage.show();
    }
    @Override 
    public void stop() {
        client.miStoDisconnettendo();
    }

    @Override
    public void init(){ 
        controllerMain = new ControllerMain(ascoltatore);
        controllerLogIn = new ControllerLogIn(ascoltatore);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        if (fxml.equals("LOGIN")){
            fxmlLoader.setControllerFactory(funzione -> {return controllerLogIn; });
        }else if (fxml.equals("CHAT")){
            fxmlLoader.setControllerFactory(funzione -> { return controllerMain; });
        }        
        return fxmlLoader.load();
    }
    //fine SETUP della finestra

    public static void main(String[] args) {
        App.client = new Client_out(ascoltatore);   //creazione del client
        client.connetti();                          //si avvia il client
        Application.launch();                       //la schermata viene lanciata
    }
}