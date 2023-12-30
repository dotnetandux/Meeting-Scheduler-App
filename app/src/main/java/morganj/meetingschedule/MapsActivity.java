package morganj.meetingschedule;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created on 18/11/2018.
 *
 * This class handles the maps activity which is loaded
 * from the ViewerFragment class
 * It displays a map with one marker on it
 *
 * @author MorganJones 904410
 * @version 1.0
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private SupportMapFragment smf;
    private LatLng current;
    private String name;

    /**
     * Loads file activity_maps.xml and assigns xml objects
     * Also sets location data
     * @param savedInstance ..
     */
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_maps);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        smf.getMapAsync(this);

        Intent i = getIntent();

        // Get extra data
        Double a = (i.getExtras().getDouble("LOC_LAT"));
        Double b = (i.getExtras().getDouble("LOC_LAN"));
        name = i.getExtras().getString("LOC_NAME");

        current = new LatLng(a, b);
    }

    /**
     * Sets the marker on a map for the location passed in
     * @param googleMap1 ..
     */
    @Override
    public void onMapReady(GoogleMap googleMap1)
    {
        this.googleMap = googleMap1;

        // Normal terrain with zoom controls
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Set new marker and zoom to it
        LatLng location = current;
        googleMap.addMarker(new MarkerOptions().position(location).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
