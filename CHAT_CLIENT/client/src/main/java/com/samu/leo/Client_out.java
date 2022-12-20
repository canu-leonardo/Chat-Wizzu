package com.samu.leo;

import java.io.*;
import java.net.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client_out {
    private Socket socket;
    private static DataOutputStream outVersoIlServer;   

    private static Client_in ascoltatore;
    public static User meMedesimo = new User();

    private static ObjectMapper json = new ObjectMapper();

    //Costruttore
    public Client_out(Client_in ascoltatore){
        this.ascoltatore = ascoltatore;
    }    

    //metodo per la connessione al server
    public Socket connetti(){       

        try {
            this.socket = new Socket("10.22.9.5", 7777);    //inizializzo la connessione con il server creando il socket
            Client_out.meMedesimo.setSocket(socket);
            this.outVersoIlServer   = new DataOutputStream(socket.getOutputStream());   //Instanzio lo stream di scrittura
            this.ascoltatore.start();   //avvio il tread ascoltatore

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("===Disconnesso dal Server===");
        }        
        
        return socket;
    }

    //======METODI DI INVIO MESSAGGI=====/
    
    /*INVIO della richiaste del nickname (la risposta verrà gestita in "Client_in") */
    public static void richiestaNickname(String nickName) {
        Messaggio messaggio = new Messaggio(0, nickName, "", "SERVER");
        try {
            outVersoIlServer.writeBytes(json.writeValueAsString(messaggio) + "\n");
        } catch (Exception e) {
            System.out.println("errore nell'invio del messaggio di richiesta del nickname");
        }
    }

    /*INVIO di un messaggio privato */
    public static void messaggioSingolo(String corpo, String desitnatario){
        Messaggio messaggio = new Messaggio(1, corpo, Client_out.meMedesimo.nickname, desitnatario);
        try {
            outVersoIlServer.writeBytes(json.writeValueAsString(messaggio) + "\n");
        } catch (Exception e) {
            System.out.println("errore nell'invio del messaggio privato");
        }
        ascoltatore.messaggi.add(messaggio);
    }

    /*INVIO di un messaggio pubblico */
    public static void messaggioATutti(String corpo) {
        Messaggio messaggio = new Messaggio(2, corpo, Client_out.meMedesimo.nickname, "Taverna");
        try {
            outVersoIlServer.writeBytes(json.writeValueAsString(messaggio) + "\n");
        } catch (Exception e) {
            System.out.println("errore nell'invio del messaggio pubblico");
        }
        ascoltatore.multicast.add(messaggio);
    }

    /*Avviso il server della disconnessione */
    public void miStoDisconnettendo() {
        Messaggio messaggio = new Messaggio(3, "", "", "SERVER");
        try {
            outVersoIlServer.writeBytes(json.writeValueAsString(messaggio) + "\n");
        } catch (Exception e) {
            System.out.println("errore nell'invio del messaggio di disconnessione");
        }
    }
}
