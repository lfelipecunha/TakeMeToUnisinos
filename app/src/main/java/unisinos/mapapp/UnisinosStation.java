package unisinos.mapapp;

/**
 * Created by luiz on 04/06/15.
 */
public class UnisinosStation extends Station {

    private static final int TIME_FROM_NOVO_HAMBURGO = 20;
    private static final int TIME_FROM_MERCADO = 45;

    public UnisinosStation() {
        super("UNISINOS", -29.7867932, -51.1406024, -1, -1);
    }

    public int getTimeDistanceFrom(Station station) {
        int time;
        if (station.getStationFrom() == STATION_FROM_MERCADO) {
            time = TIME_FROM_MERCADO;
        } else {
            time = TIME_FROM_NOVO_HAMBURGO;
        }

        return time - station.getTimeFrom();
    }
}

