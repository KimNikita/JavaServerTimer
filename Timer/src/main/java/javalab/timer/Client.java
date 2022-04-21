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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Никита
 */
public class Client {

    int id;
    Socket cs = null;
    int port;
    InetAddress ip = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    Thread t;
    String time;
    DefaultListModel<String> model1, model2;

    public Client(DefaultListModel<String> m1, DefaultListModel<String> m2) {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        port = 777;
        model1 = m1;
        model2 = m2;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized String getTime() {
        return time;
    }

    public void connect(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            cs = new Socket(ip, port);
            System.out.println("Подключение успешно");
            oos = new ObjectOutputStream(cs.getOutputStream());
            t = new Thread(
                    () -> {
                        try {
                            ois = new ObjectInputStream(cs.getInputStream());
                            Message msg = (Message) ois.readObject();
                            id = msg.getId();
                            time = msg.getTime();
                            ArrayList<Event> allevents = new ArrayList<>();
                            ArrayList<Event> happenedevents = new ArrayList<>();
                            allevents = msg.getAll();
                            happenedevents = msg.getHappened();
                            if (allevents != null) {
                                model1.clear();
                                for (Event e : allevents) {
                                    model1.addElement(e.getEvent());
                                }
                            }
                            if (happenedevents != null) {
                                for (Event e : happenedevents) {
                                    model2.addElement(e.getEvent());
                                }
                            }
                            while (true) {
                                msg = (Message) ois.readObject();
                                if (msg.getClosed()) {
                                    break;
                                }
                                time = msg.getTime();
                                allevents = new ArrayList<>();
                                happenedevents = new ArrayList<>();
                                allevents = msg.getAll();
                                happenedevents = msg.getHappened();
                                if (allevents != null) {
                                    model1.clear();
                                    for (Event e : allevents) {
                                        model1.addElement(e.getEvent());
                                    }
                                }
                                if (happenedevents != null) {
                                    for (Event e : happenedevents) {
                                        model2.addElement(e.getEvent());
                                    }
                                }
                            }
                            disconnect(false);
                        } catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            );
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addEvent(Event e) {
        Message msg = new Message();
        msg.setEvent(e);
        try {
            oos.writeObject(msg);
            System.out.println("Message was send");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect(boolean me) {
        try {
            if (cs != null) {
                if (me) {
                    Message msg = new Message();
                    msg.setClosed(true);
                    oos.writeObject(msg);
                    t.stop();
                }
                cs.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
