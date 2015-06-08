package unisinos.mapapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import java.util.ArrayList;

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
        Schedule aux = Schedule.get(id);
        ArrayList<MyTime> list = aux.getCurrentTimes();
        if (list == null) {
            return null;
        }
        ArrayList<MyTime> result = new ArrayList<>();
        for (MyTime time : list) {
            MyTime buf = time.clone();
            buf.add(timeFrom);
            result.add(buf);
        }
        return result;
    }
}
