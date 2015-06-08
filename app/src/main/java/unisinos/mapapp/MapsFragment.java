package unisinos.mapapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.w3c.dom.Text;


public class MapsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        googleMap.setMyLocationEnabled(true);

        EventsManager e = EventsManager.getInstance();
        e.setMap(googleMap);
        e.setFragment(this);

        googleMap.setOnMyLocationChangeListener(new LocationListener(googleMap));

        return v;
    }


    public void setStation(Station station, boolean nextTrain) {
        TextView text = (TextView)getActivity().findViewById(R.id.station);
        if (text != null) {
            text.setText(station.getName());
        }

        TextView mapText = (TextView)getActivity().findViewById(R.id.map_text);
        if (mapText != null) {
            if (nextTrain) {
                mapText.setText("Next Train: " + station.getNextTrain());
            } else {
                mapText.setText("");
            }
        }

        ((MainActivity)getActivity()).setStation(station);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
