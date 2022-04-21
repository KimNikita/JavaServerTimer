/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalab.timer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Никита
 */
public class Message implements Serializable {

    int id;
    String time;
    Event userevent;
    ArrayList<Event> allevents;
    ArrayList<Event> happenedevents;
    boolean closed = false;

    public Message() {
    }

    public void setId(int i) {
        id = i;
    }

    public void setTime(String t) {
        time = t;
    }

    public void setEvent(Event e) {
        userevent = e;
    }

    public void setAll(ArrayList<Event> all) {
        allevents = all;
    }

    public void setHappened(ArrayList<Event> hap) {
        happenedevents = hap;
    }

    public void setClosed(boolean f) {
        closed = f;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public Event getEvent() {
        return userevent;
    }

    public ArrayList<Event> getAll() {
        return allevents;
    }

    public ArrayList<Event> getHappened() {
        return happenedevents;
    }

    public boolean getClosed() {
        return closed;
    }
}
