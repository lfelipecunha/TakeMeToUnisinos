package unisinos.mapapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by luiz on 22/05/15.
 */
public class EventsManager {


    private static final double ON_STATION_DISTANCE = 50;

    private static final double MINIMUN_DISTANCE = 500;

    public static final int WAITING = 0;
    public static final int START = 1;
    private static final int FIRST_STATION = 2;
    private static final int NEAR_UNISINOS_STATION = 4;
    private static final int AT_UNISINOS_STATION = 5;
    private static final int ON_BUS = 6;
    private static final int ARRIVE = 7;

    /**
     * Singleton Pattern
     */
    private static EventsManager instance;

    public static EventsManager getInstance() {
        if (EventsManager.instance == null) {
            EventsManager.instance = new EventsManager();
        }
        return EventsManager.instance;
    }

    private ArrayList<Station> stations;
    private ArrayList<BusStation> busStations;

    private UnisinosStation unisinosStation;

    private Station lastStation;

    private Station currentStation;

    private GoogleMap map;

    private LatLng currentPosition;

    private MapsFragment mapsFragment;

    private MyTime arriveAtUniStation;
    private MyTime nextBus;

    private MainActivity mainActivity;

    private EventsManager() {
        unisinosStation = new UnisinosStation();

        stations = new ArrayList<>();
        stations.add(new Station("Mercado", -30.0263442, -51.2286169, 0, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Rodoviária", -30.0225466, -51.2196396, 2, Station.STATION_FROM_MERCADO));
        stations.add(new Station("São Pedro", -30.0061734,-51.2096603, 5, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Farrapos", -29.9973491,-51.1978905, 7, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Aeroporto", -29.9878318, -51.1833799, 10, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Anchieta", -29.9768051, -51.1794209, 12, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Niteroi", -29.9544306, -51.1765788, 15, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Fátima", -29.9388943, -51.1776241, 18, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Canoas / La salle", -29.9189036, -51.1822454, 21, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Mathias Velho", -29.9039721, -51.1789192, 24, Station.STATION_FROM_MERCADO));
        stations.add(new Station("São Luís / ULBRA", -29.8877395, -51.17959, 26, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Petrobras", -29.8766632, -51.1810646, 28, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Esteio", -29.8521815, -51.1797101, 31, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Luis Pasteur", -29.8328267, -51.1654956, 34, Station.STATION_FROM_MERCADO));
        stations.add(new Station("Sapucaia", -29.8231835, -51.1489243, 37, Station.STATION_FROM_MERCADO));
        stations.add(unisinosStation);
        stations.add(new Station("São Leopoldo", -29.769218, -51.141187, 14, Station.STATION_FROM_NOVO_HAMBURGO));
        stations.add(new Station("Rio dos Sinos", -29.749111, -51.145171, 10, Station.STATION_FROM_NOVO_HAMBURGO));
        stations.add(new Station("Santo Afonso", -29.729781, -51.140382, 7, Station.STATION_FROM_NOVO_HAMBURGO));
        stations.add(new Station("Industrial", -29.715916, -51.134979, 5, Station.STATION_FROM_NOVO_HAMBURGO));
        stations.add(new Station("Fenac", -29.700995, -51.135044, 3, Station.STATION_FROM_NOVO_HAMBURGO));
        stations.add(new Station("Novo Hamburgo", -29.687047, -51.132857, 0, Station.STATION_FROM_NOVO_HAMBURGO));

        String nearC[] = {"C", "F"};
        String nearB[] = {"A", "B", "L", "M"};
        String nearD[] = {"D", "H", "I", "G"};
        String nearE[] = {"E","J", "K"};

        busStations = new ArrayList<>();
        busStations.add(new BusStation("Sector C", -29.792798, -51.151002, 5, nearC));
        busStations.add(new BusStation("Sector D", -29.797114, -51.152831, 7, nearD));
        busStations.add(new BusStation("Sector E", -29.798144, -51.156544, 9, nearE));
        busStations.add(new BusStation("Sector B", -29.792352, -51.154956, 13, nearB));



        TrainSchedule.loadSchedules();
        BusSchedule.loadSchedules();
    }

    public void setActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public void setMap(GoogleMap m) {
        map = m;
        putMarkers();
    }

    public int getStep() {
        int step;
        SharedPreferences sharedPreferences = getSharedPreferences();
        step = sharedPreferences.getInt("step", WAITING);
        return step;
    }

    public SharedPreferences getSharedPreferences() {
        return mainActivity.getPreferences(Context.MODE_PRIVATE);
    }

    public void setStep(int s) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("step", s);
        editor.commit();
    }

    public void setCurrentPosition(LatLng pos) {
        currentPosition = pos;
        Station aux = computeNearestStation();
        BusStation busStation = getBusDestination();
        int step = getStep();

        Log.e("EVENTS MANAGER", "STEP: " + step);

        if (currentStation != null && !currentStation.equals(aux)) {
            lastStation = currentStation;
        }

        currentStation = aux;
        double distance = currentStation.getDistance(getCurrentPosition());
        if (step < FIRST_STATION) {
            mapsFragment.setStation(currentStation, true);
        } else {
            mapsFragment.setStation(currentStation, false);
        }

        if (step == START && distance < ON_STATION_DISTANCE) {
            step = FIRST_STATION;
        } else if (isUnisinosNextStation()) {
            notifyUser(
                "Hey",
                "You have to get off at the next station!"
            );
            step = EventsManager.NEAR_UNISINOS_STATION;
        } else if (step == NEAR_UNISINOS_STATION && distance < 50) {
            step = AT_UNISINOS_STATION;
        } else if (step == AT_UNISINOS_STATION && distance > 50) {
            step = ON_BUS;
        } else if (step == ON_BUS && busStation != null && busStation.getDistance(currentPosition) <= 200) {
            notifyUser(
                "Hey",
                "You have to get off at the next station!"
            );
            step = ARRIVE;
        }

        setArriveTime();
        Log.e("EventsManager", "SETTING STEP " + step);
        setStep(step);
    }

    public void setArriveTime() {
        if (getStep() >= START) {
            mainActivity.setArriveTime(getTimeUntilDestination(), arriveAtUniStation, nextBus);

        }
    }

    public boolean isUnisinosNextStation() {
        int step = getStep();
        return step >= EventsManager.FIRST_STATION &&
                step < EventsManager.NEAR_UNISINOS_STATION &&
                (isNearFromUnisinos() || currentStation.equals(unisinosStation));
    }



    public void toggle() {
        int step = getStep();
        if (step >= START) {
            setStep(WAITING);
        } else if (step == WAITING) {
            setStep(START);
        }
    }


    public boolean isNearFromUnisinos()
    {
        return unisinosStation.getDistance(getCurrentPosition()) <= MINIMUN_DISTANCE;
    }

    private Station computeNearestStation() {
        int size = stations.size();
        Station near = stations.get(0);
        double distance = near.getDistance(getCurrentPosition());

        for (int i = 1; i < size; i++) {
            double aux = stations.get(i).getDistance(getCurrentPosition());

            if (distance > aux) {
                distance = aux;
                near = stations.get(i);
            }
        }
        return near;
    }

    private void putMarkers() {
        for (int i=0; i<stations.size(); i++) {
            Station station = stations.get(i);
            MarkerOptions marker = new MarkerOptions();
            marker.position(station.getPosition()).title(station.getName());

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            // adding marker
            map.addMarker(marker);
        }

        for (BusStation bs : busStations) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(bs.getPosition()).title(bs.getName());

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            // adding marker
            map.addMarker(marker);
        }
    }

    public void setFragment(MapsFragment fragment) {
        mapsFragment = fragment;
    }


    public LatLng getCurrentPosition() {
        return currentPosition;
    }


    public void notifyUser(String title, String msg) {
        Activity a = mainActivity;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(a)
                        .setSmallIcon(R.drawable.small_icon)
                        .setContentTitle(title)
                        .setContentText(msg);

        Intent resultIntent = new Intent(a, a.getClass());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(a);
        stackBuilder.addParentStack(a.getClass());

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = mBuilder.build();
        n.defaults |= Notification.DEFAULT_SOUND;
        n.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(0, n);
    }

    private MyTime getTimeUntilDestination() {
        MyTime result = MyTime.currentTime();
        if (currentStation == null) {
            return null;
        }
        double distance = currentStation.getDistance(currentPosition);
        int step = getStep();
        if (
                currentStation != null &&
                (
                    currentStation != unisinosStation ||
                    (distance > ON_STATION_DISTANCE && lastStation != null)
                ) &&
                step < AT_UNISINOS_STATION
        ) {
            LatLng stationPos;
            if (!currentStation.equals(unisinosStation)) {
                stationPos = currentStation.getPosition();
            } else {
                stationPos = lastStation.getPosition();
            }
            double fullDistance = unisinosStation.getDistance(stationPos);
            double currentDistance = unisinosStation.getDistance(currentPosition);
            int time = unisinosStation.getTimeDistanceFrom(currentStation);
            time = (int)(time * currentDistance / fullDistance);

            result.add(time);
        }
        if (step < AT_UNISINOS_STATION) {
            arriveAtUniStation = result.clone();
        } else {
            arriveAtUniStation = null;
        }
        Schedule schedule = Schedule.get(Schedule.BUS);
        if (step < ON_BUS) {
            result = schedule.getNext(result);
            if (result != null) {
                nextBus = result.clone();
            } else {
                nextBus = null;
            }
        } else {
            nextBus = null;
        }
        if (result != null) {
            result.add(getBusTime());
        }

        return result;
    }

    private int getBusTime() {
        BusStation b = getBusDestination();
        int time = 0;
        int step = getStep();

        if (b != null) {
            time = b.getTimeFrom();
            if (step == ON_BUS) {
                double fullDistance = b.getDistance(busStations.get(0).getPosition());
                double currentDistance = b.getDistance(currentPosition);
                time = (int)(time * currentDistance / fullDistance);
            }
        }

        return time;
    }

    private BusStation getBusDestination() {
        String dest = getDestination();
        if (dest == null) {
            return null;
        }
        BusStation b = null;
        for (int i =0; i < busStations.size(); i++) {
            if (busStations.get(i).isNear(dest)) {
                b = busStations.get(i);
            }
        }
        return b;
    }

    public void setSectorPos(int pos) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("sector", pos);
        editor.commit();
    }

    public int getSectorPos() {
        return getSharedPreferences().getInt("sector",0);
    }

    private String getDestination() {
        int selectedItem = getSectorPos();
        String result;
        switch (selectedItem) {
            case 1:
                result = "B";
                break;
            case 2:
                result = "C";
                break;
            case 3:
                result = "D";
                break;
            case 4:
                result = "E";
                break;
            case 5:
                result = "F";
                break;
            case 6:
                result = "G";
                break;
            case 7:
                result = "H";
                break;
            case 8:
                result = "I";
                break;
            case 9:
                result = "J";
                break;
            case 10:
                result = "K";
                break;
            case 11:
                result = "L";
                break;
            case 12:
                result = "M";
                break;
            case 0:
            default:
                result = "A";
        }
        return result;
    }
}
