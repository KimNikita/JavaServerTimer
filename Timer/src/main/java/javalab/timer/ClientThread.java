/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalab.timer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Никита
 */
public class ClientThread {

    Server server;
    int id;
    Socket cs = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    Thread t;

    public ClientThread(Socket cs, int id, Server server) {
        this.server = server;
        this.id = id;
        this.cs = cs;
        try {
            oos = new ObjectOutputStream(cs.getOutputStream());
            sendMessage(server.getState());
            t = new Thread(
                    () -> {
                        try {
                            ois = new ObjectInputStream(cs.getInputStream());
                            while (true) {
                                Message msg = (Message) ois.readObject();
                                Event ue = msg.getEvent();
                                System.out.println("Message was get");
                                if (msg.getClosed()) {
                                    break;
                                }
                                server.addEvent(ue);
                            }
                            disconnect(false);
                        } catch (IOException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            );
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect(boolean me) {
        try {
            if (me) {
                Message msg = new Message();
                msg.setClosed(true);
                sendMessage(msg);
                t.stop();
            } else {
                server.remove(this);
            }
            if (cs != null) {
                cs.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
