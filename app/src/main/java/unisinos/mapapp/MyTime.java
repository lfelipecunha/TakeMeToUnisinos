package unisinos.mapapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luiz on 28/05/15.
 */
public class MyTime implements Cloneable {

    private int time, hours, minutes, seconds;

    public static MyTime currentTime() {
        SimpleDateFormat s = new SimpleDateFormat("HH");
        int hours = Integer.parseInt(s.format(new Date()));
        s = new SimpleDateFormat("mm");
        int minutes = Integer.parseInt(s.format(new Date()));
        s = new SimpleDateFormat("ss");
        int seconds = Integer.parseInt(s.format(new Date()));
        return new MyTime(hours, minutes, seconds);
    }

    public MyTime(int hours, int minutes, int seconds){
        setTime(hours, minutes, seconds);
    }

    public MyTime(int hours, int minutes){
        setTime(hours, minutes, 0);
    }

    private void setTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        time = hours*3600 + minutes*60 + seconds;
    }

    public int getTime() {
        return time;
    }

    public int diff(MyTime t) {
        return time - t.getTime();
    }

    public MyTime clone() {
        return new MyTime(hours, minutes, seconds);
    }

    public void add(int minutes) {
        time += minutes * 60;
        adjust();
    }

    public String toString() {
        String h = "" + hours;
        String m = "" + minutes;
        if (h.length() == 1) {
            h = "0"+h;
        }
        if (m.length() == 1) {
            m = "0"+m;
        }

        return h+":"+m;
    }

    public void sum(MyTime t) {
        time += t.getTime();
        adjust();
    }

    private void adjust() {
        seconds = time % 60;
        hours = time/3600;
        minutes = (time - hours * 3600) / 60;
    }


}
