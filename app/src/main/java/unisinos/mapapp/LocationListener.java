package unisinos.mapapp;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;

/**
 * Created by luiz on 21/05/15.
 */
public class LocationListener implements OnMyLocationChangeListener {

    GoogleMap googleMap;

    boolean hasChanged = false;

    LatLng lastPos;

    public LocationListener(GoogleMap map) {
        googleMap = map;
    }

    @Override
    public void onMyLocationChange(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if (lastPos == null || (current.latitude != lastPos.latitude && current.longitude != lastPos.longitude)) {
            lastPos = current;
            EventsManager.getInstance().setCurrentPosition(current);

            if (!hasChanged) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(current).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                hasChanged = true;
            }
        }


    }
}
