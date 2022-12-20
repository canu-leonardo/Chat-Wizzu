package com.samu.leo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;

public class Client_in extends Thread{

    // si tratta di una funzione che costituirà un evento e ci permetterà di segnalare l'accettazione del nickname alla grafica
    public Consumer<Boolean> nicknameAccepted = isAccepted -> {};
    // si tratta di una funzione che costituirà un evento e ci permetterà di segnalare l'arrivo di un messaggio alla grafica così da aggiornare la chat
    public Consumer<Messaggio> messageIsArriverd = message -> {};
    // si tratta di una funzione che costituirà un evento e ci permetterà di segnalare alla grafica che bisogna cambiare il nickname
    public Runnable nicknameDaCambiare = () -> {};
    // si tratta di una funzione che costituirà un evento e ci permetterà di segnalare l'accettazione del nickname alla grafica
    public Runnable listUpdated = () -> {};
    // si tratta di una funzione che costituirà un evento e ci permetterà di far partire l'alert dell'arrivo del messaggio
    

    private BufferedReader inDalServer;                 //buffer per la lettutre di messaggi dal server
    private ObjectMapper json = new ObjectMapper();     //mapper per la serializzazsione dei messaggi
    public static boolean accettato;                    

    // inizializzazione della cronologia e archiviazione dei messaggi della della chat
    public static ArrayList<Messaggio> messaggi  =  new ArrayList<>(); 
    public static ArrayList<Messaggio> multicast =  new ArrayList<>(); // non è un vero multicast, adoperimo il protocollo TCP 
    public static ArrayList<String> utenti = new ArrayList<>();

    //Costruttore
    public Client_in(){  }

    //metodo run per il thread
    public void run() {

        try { //inizializzazione dello stream di input
            this.inDalServer = new BufferedReader(new InputStreamReader(Client_out.meMedesimo.socket.getInputStream()));
        } catch (Exception e) {   System.out.println("errore nella creazione dello stream");    }

        while(true){

            //riceviamo la stringa serializzata
            String stringaRicevuta = "";
            try {   stringaRicevuta = inDalServer.readLine();   } 
            catch (Exception e) {   System.out.println("errore nella ricezione di un messaggio");    }

            //la deserializziamo in un oggetto "Messaggio"
            Messaggio messaggio = new Messaggio();
            try {
                messaggio = json.readValue(stringaRicevuta, Messaggio.class);
            } catch (Exception e) {   System.out.println("errore nella deserializzazione di un messaggio");    }

            //effettuiamo il controllo, così da attivare la funzionalità richiesta
            String tipo = messaggio.getHeader();
            switch (tipo) {

                case "@NICKNAME_NOT_ACCEPTED":

                    this.nickName_non_accettato();
                    break;

                case "@NICKNAME_ACCEPTED":

                    this. nickName_accettato(messaggio);
                    break;

                case "@PRIVATE_MESSAGE":

                    this.messaggioUnicast(messaggio);
                    break;

                case "@PUBLIC_MESSAGE":

                    this.messaggioMulticast(messaggio);

                    break;

                case "@CLIENT_LIST":

                    this.clientList(messaggio);

                break;

                case "@!SERVER_CLOSED":

                this.chiusoIlServer();

                    break;
            }
        }
    }

    //================ METODI DELLO SWITCH CASE ================//

    // metodo per il rifiuto del nickname 
    public void nickName_non_accettato(){
        this.accettato = false;
        //ControllerLogIn.EsitoNickName();
        this.nicknameAccepted.accept(false); // avviamo l'evento dove il nickname non è stato accettato
    }

    // metodo per l'accettazione del nickname 
    public void nickName_accettato(Messaggio messaggio){
        Client_out.meMedesimo.nickname = messaggio.getDestinatario();
        
        this.accettato = true;
        //ControllerLogIn.EsitoNickName();
        //ControllerMain.entrato();
        this.nicknameAccepted.accept(true);   // avviamo l'evento dove il nickname è stato accettato
        this.nicknameDaCambiare.run();          //avvisiamo la seconda pagina che il nickname è da cambiare
                                                //(ugnuno inizialmente lo ha settato a 'VOID')
    }

    
    //metodo che gestisce l'arrivo di un messaggio privato
    public void messaggioUnicast(Messaggio messaggio) {
        messaggi.add(messaggio);
        messageIsArriverd.accept(messaggio);
    }

    //metodo che gestisce l'arrivo di un messaggio pubblico
    public void messaggioMulticast(Messaggio messaggio) {
        multicast.add(messaggio);
        messageIsArriverd.accept(messaggio);
    }

    //metodo che aggiorna la lista dei client connessi
    public void clientList(Messaggio messaggio) {

        String[] s = messaggio.getCorpo().split("-!-");

        utenti.clear();

        for (int i = 0; i < s.length; i++) {
            utenti.add(s[i]);
        }
       
        /*if(s.length > utenti.size()){
            if( ! utenti.contains(s[s.length - 1])){
                utenti.add(s[s.length - 1]);
                //metodo che fa partire l'allert (GLI PASSIAMO s[s.length - 1])  
            }                        
        } else {
            for (int i = 0; i < s.length; i++) {
                if ( ! s[i].equals(utenti.get(i))) {
                    //metodo che fa partire l'allert (GLI PASSIAMO utenti.get(i))
                    utenti.remove(i);
                    break;
                }
            }
        }*/                      
        this.listUpdated.run();
    }
    
    //metodo che gestisce la situazione in caso di chiusura del server
    public void chiusoIlServer() {
        try {
            Client_out.meMedesimo.socket.close();
            System.exit(1);             //chiudo l'esecuzione
        } catch (IOException e) {
            System.out.println("Problema con la chiusura del socket");
        }
        // cambiamo la fiestra ( app.setRoot() ) e facciamo comparire quella di disconnessione
    }
   
}