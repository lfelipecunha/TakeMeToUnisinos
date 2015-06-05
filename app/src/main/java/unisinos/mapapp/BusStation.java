package unisinos.mapapp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiz on 04/06/15.
 */
public class BusStation extends Station{

    private String near[];

    public static List<LatLng> route;

    public double getDistance(LatLng pos) {
        if (route == null) {
            loadRoute();
        }
        LatLng pos1 = route.get(0);
        // se não esta no PATH calcula a distancia total do caminho até a estação escolhida
        boolean start = !PolyUtil.isLocationOnPath(pos, route, false, 10);

        boolean hasToBreak = false;
        double distance = 0;
        for (int i= 1; i< route.size(); i++) {
            ArrayList<LatLng> aux = new ArrayList<LatLng>();
            aux.add(pos1);
            aux.add(route.get(i));

            LatLng currentPos = route.get(i);

            if (PolyUtil.isLocationOnPath(getPosition(), aux, false, 10)) {
                Log.e("BUSSTATION", "NextPoint is BUS Station");
                currentPos = getPosition();
                hasToBreak = true;
            }

            if (start) {
                Log.e("BUSSTATION", "Adding current distance");
                distance += SphericalUtil.computeDistanceBetween(pos1, currentPos);
            }

            if (!start && PolyUtil.isLocationOnPath(pos, aux, false, 10)) {
                Log.e("BUSSTATION", "Passed POINT is the ROUTE");
                start = true;
                distance += SphericalUtil.computeDistanceBetween(pos, route.get(i));
            }

            if (hasToBreak) {
                Log.e("BUSSTATION", "BREAKING");
                break;
            }
            pos1 = route.get(i);
        }
        Log.e("BUSSTATION", "DISTANCE: " + distance);
        return distance;
    }

    public static void loadRoute(){
        route = new ArrayList<>();
        route.add(new LatLng(-29.787109, -51.140476)); // Estação TREM
        route.add(new LatLng(-29.791356, -51.151152)); // Curva para a Unisinos
        route.add(new LatLng(-29.792385, -51.151016));
        route.add(new LatLng(-29.793141, -51.150680));
        route.add(new LatLng(-29.793886, -51.150098));
        route.add(new LatLng(-29.794397, -51.149958));
        route.add(new LatLng(-29.795918, -51.150182));
        route.add(new LatLng(-29.796397, -51.150378));
        route.add(new LatLng(-29.796798, -51.150869));
        route.add(new LatLng(-29.797005, -51.152119));
        route.add(new LatLng(-29.797241, -51.153071));
        route.add(new LatLng(-29.797639, -51.153793));
        route.add(new LatLng(-29.798285, -51.154628));
        route.add(new LatLng(-29.798506, -51.155184));
        route.add(new LatLng(-29.798540, -51.155724));
        route.add(new LatLng(-29.798338, -51.156364));
        route.add(new LatLng(-29.797329, -51.157835));
        route.add(new LatLng(-29.796683, -51.159117));
        route.add(new LatLng(-29.796313, -51.159408));
        route.add(new LatLng(-29.795904, -51.159440));
        route.add(new LatLng(-29.795520, -51.159259));
        route.add(new LatLng(-29.795293, -51.158976));
        route.add(new LatLng(-29.794908, -51.158335));
        route.add(new LatLng(-29.794693, -51.158137));
        route.add(new LatLng(-29.794355, -51.157989));
        route.add(new LatLng(-29.793780, -51.158078));
        route.add(new LatLng(-29.792954, -51.158285));
        route.add(new LatLng(-29.792254, -51.154922));
    }

    public BusStation(String n, double latitude, double longitude, int time, String nearSectors[]) {
        super(n, latitude, longitude, time, -1);
        near = nearSectors;
    }

    public boolean isNear(String sector) {
        for (int i=0; i<near.length; i++) {
            if (sector.equalsIgnoreCase(near[i])) {
                return true;
            }
        }
        return false;

    }
}
