/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalab.timer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Никита
 */
public class Server {

    ServerSocket ss = null;
    Socket cs;
    int port = 777;
    InetAddress ip = null;
    int count = 0;
    DefaultListModel<String> model1, model2;
    boolean data = false;
    boolean get_out_of_here = false;
    Connection con;
    ArrayList<ClientThread> clients = new ArrayList<>();

    Thread client = null;
    Thread timer = null;
    int hour = 20;
    int minute = 0;

    public Server(DefaultListModel<String> m1, DefaultListModel<String> m2) {
        model1 = m1;
        model2 = m2;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized String getTime() {
        String h = hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour);
        String m = minute < 10 ? "0" + Integer.toString(minute) : Integer.toString(minute);
        return h + ":" + m;
    }

    public synchronized Message getState() {
        Message msg = new Message();
        msg.setTime(getTime());
        msg.setId(getCount());
        try {
            ArrayList<Event> allevents = new ArrayList<>();
            Statement st = con.createStatement();
            ResultSet res_all = st.executeQuery("SELECT * FROM events");
            while (res_all.next()) {
                allevents.add(new Event(res_all.getInt("id"), res_all.getInt("hour"), res_all.getInt("minute"), res_all.getString("desc")));
            }
            msg.setAll(allevents);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }

    public synchronized void addEvent(Event e) {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO events VALUES (?, ?, ?, ?)");
            pst.setInt(1, e.getId());
            pst.setInt(2, e.getHour());
            pst.setInt(3, e.getMinute());
            pst.setString(4, e.getDesc());
            pst.executeUpdate();
            data = true;
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Message was process");
    }

    public synchronized void remove(ClientThread ct) {
        clients.remove(ct);
    }

    public synchronized void run(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            ss = new ServerSocket(this.port, 0, this.ip);
            System.out.println("Сервер запущен");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/timer", "root", "Qwerty123");
            System.out.println("Подключение к БД установлено");
            con.createStatement().executeUpdate("DELETE FROM events");
            timer = new Thread(
                    () -> {
                        while (true) {
                            if (minute + 1 > 59) {
                                minute = 0;
                                if (hour + 1 > 23) {
                                    hour = 0;
                                } else {
                                    hour += 1;
                                }
                            } else {
                                minute += 1;
                            }

                            boolean flag = false;
                            Message msg = new Message();
                            msg.setTime(getTime());
                            ArrayList<Event> allevents = new ArrayList<>();
                            ArrayList<Event> happenedevents = new ArrayList<>();

                            try {
                                PreparedStatement pst = con.prepareStatement("SELECT * FROM events WHERE hour = ? AND minute = ?");
                                pst.setInt(1, hour);
                                pst.setInt(2, minute);

                                PreparedStatement del = con.prepareStatement("DELETE FROM events WHERE hour = ? AND minute = ?");
                                del.setInt(1, hour);
                                del.setInt(2, minute);

                                ResultSet res = pst.executeQuery();
                                while (res.next()) {
                                    flag = true;
                                    happenedevents.add(new Event(res.getInt("id"), res.getInt("hour"), res.getInt("minute"), res.getString("desc")));
                                }

                                if (flag || data) {
                                    data = false;
                                    model1.clear();
                                    if (flag) {
                                        msg.setHappened(happenedevents);
                                        del.executeUpdate();
                                    }
                                    Statement st = con.createStatement();
                                    ResultSet res_all = st.executeQuery("SELECT * FROM events");
                                    while (res_all.next()) {
                                        allevents.add(new Event(res_all.getInt("id"), res_all.getInt("hour"), res_all.getInt("minute"), res_all.getString("desc")));
                                        model1.addElement(new Event(res_all.getInt("id"), res_all.getInt("hour"), res_all.getInt("minute"), res_all.getString("desc")).getEvent());
                                    }
                                    msg.setAll(allevents);
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            for (ClientThread ct : clients) {
                                ct.sendMessage(msg);
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
            );
            timer.start();
            client = new Thread(
                    () -> {
                        while (true) {
                            try {
                                cs = ss.accept();
                                if (get_out_of_here) {
                                    break;
                                }
                                count++;
                                System.out.println("Клиент " + count + " подключен");
                                model2.addElement("id: " + count + " name: client_" + count);
                                ClientThread ct = new ClientThread(cs, count, this);
                                clients.add(ct);
                            } catch (IOException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
            );
            client.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public synchronized void stop(boolean me) {
        if (ss != null) {
            timer.stop();
            for (ClientThread ct : clients) {
                ct.disconnect(me);
            }
            clients.clear();
            try {
                get_out_of_here = true;
                Socket gs = new Socket(ip, port);
                gs.close();
                client.stop();
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
