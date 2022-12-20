package com.samu.leo;

import java.net.Socket;

public class User {
    
    public String   nickname;
    public Socket   socket;
    public int      position;
    
    //===CONSTRUCTORS====
    public User(){  } // void constructor
    public User(String nickname, Socket socket) {
        this.nickname   = nickname;
        this.socket     = socket;
    }

    //===GETTERS===
    public Socket   getSocket ()      {   return socket;     }
    public String   getNickname()     {   return nickname;   }
    public int      getPosition()     {   return position;   }
    
    //===SETTERS===
    public void     setPosition(int position)       {   this.position = position;   }   
    public void     setSocket(Socket socket)        {   this.socket = socket;       }     
    public void     setNickname(String nickname)    {   this.nickname = nickname;   }
}
