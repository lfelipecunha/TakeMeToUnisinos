package unisinos.mapapp;

import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by luiz on 22/05/15.
 */
public class Station {

    public static final int STATION_FROM_MERCADO = 0;
    public static final int STATION_FROM_NOVO_HAMBURGO = 1;

    private String name;
    private LatLng position;
    private int timeFrom;
    private int stationFrom;

    public Station(String n, double latitude, double longitude, int time, int origin){
        name = n;
        timeFrom = time;
        stationFrom = origin;
        position = new LatLng(latitude, longitude);
    }

    public double getDistance(LatLng pos){
        return SphericalUtil.computeDistanceBetween(position, pos);
    }

    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return position;
    }

    public int getTimeFrom() {
        return timeFrom;
    }

    public int getStationFrom() {
        return stationFrom;
    }

    public MyTime getNextTrain() {
        int id = stationFrom == STATION_FROM_MERCADO ? Schedule.MERCADO : Schedule.NOVO_HAMBURGO;
        return Schedule.get(id).getNext(timeFrom);
    }

    public ArrayList<MyTime> getTimeSchedule() {
        int id;
        switch (stationFrom) {
            case STATION_FROM_MERCADO:
                id = Schedule.MERCADO;
                break;
            case STATION_FROM_NOVO_HAMBURGO:
                id = Schedule.NOVO_HAMBURGO;
                break;
            default:
                id = Schedule.BUS;
        }
        Log.e("STATION", "ID: "+ id);
        Schedule aux = Schedule.get(id);
        ArrayList<MyTime> list = aux.getCurrentTimes();
        ArrayList<MyTime> result = new ArrayList<>();
        for (MyTime time : list) {
            MyTime buf = time.clone();
            buf.add(timeFrom);
            result.add(buf);
        }
        return result;
    }
}
