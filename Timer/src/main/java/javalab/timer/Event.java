/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalab.timer;

import java.io.Serializable;

/**
 *
 * @author Никита
 */
public class Event implements Serializable {

    int id;
    int hour;
    int minute;
    String desc;

    public Event(int id, int hour, int minute, String desc) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getDesc() {
        return desc;
    }

    public String getEvent() {
        String h = hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour);
        String m = minute < 10 ? "0" + Integer.toString(minute) : Integer.toString(minute);
        return "client id: " + Integer.toString(id) + " time: " + h + ":" + m + " description: " + desc;
    }
}
