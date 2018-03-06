package airsoft.stalker.pda;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        MapFragment mapFragment2 = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng bootcamp = new LatLng(50.3865, 30.747);
        mMap.addMarker(new MarkerOptions().position(bootcamp).title("Bootcamp"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bootcamp,17));
        CircleOptions anomaly1 = new CircleOptions().center(new LatLng(50.3860, 30.747)).radius(20).fillColor(Color.argb(30,200,0,200)).strokeColor(Color.argb(70,200,0,200));
        CircleOptions anomaly2 = new CircleOptions().center(new LatLng(50.3885, 30.743)).radius(30).fillColor(Color.argb(30,200,0,200)).strokeColor(Color.argb(70,200,0,200));
        CircleOptions anomaly3 = new CircleOptions().center(new LatLng(50.3845, 30.750)).radius(40).fillColor(Color.argb(30,200,0,200)).strokeColor(Color.argb(70,200,0,200));
        CircleOptions anomaly4 = new CircleOptions().center(new LatLng(50.3835, 30.743)).radius(30).fillColor(Color.argb(30,200,0,200)).strokeColor(Color.argb(70,200,0,200));
        CircleOptions anomaly5 = new CircleOptions().center(new LatLng(50.3895, 30.749)).radius(40).fillColor(Color.argb(30,200,0,200)).strokeColor(Color.argb(70,200,0,200));

        mMap.addCircle(anomaly1);
        mMap.addCircle(anomaly2);
        mMap.addCircle(anomaly3);
        mMap.addCircle(anomaly4);
        mMap.addCircle(anomaly5);

    }
}
