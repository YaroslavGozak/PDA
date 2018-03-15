package airsoft.stalker.pda;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.os.Vibrator;

import com.google.android.gms.location.*;//FusedLocationProviderClient;
import com.google.android.gms.maps.model.*;//.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Enumeration;
import java.util.Hashtable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Hashtable<String,Anomaly> mAnomalies;
    private Context mContext;
    private Ringtone mGeigerSound;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setupLocation();
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Hashtable<String,Anomaly> anomalies = addAnomalies();
        setAnomaliesToGeofence(anomalies);
        if (checkPermission()) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // Show rationale and request permission.
        }
        // Add a marker in Sydney and move the camera

        configureMap();
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {
                }
                return;
            }
        }
    }
    private void configureMap(){
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng bootcamp = new LatLng(50.3865, 30.747);
        mMap.addMarker(new MarkerOptions().position(bootcamp).title("Bootcamp"));
        if(checkPermission())
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng startLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation,17));
                        }
                    }
                });
    }

    private void setupLocation(){
        createLocationClient();
        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
    }

    private void createLocationClient(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void createLocationCallback(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if(isInAnomaly(location)){
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                        startGeigerCounterSound();
                    }
                    else{
                        stopGeigerCounterSound();
                    }
                }
            };
        };
    }

    private Hashtable<String,Anomaly> addAnomalies(){
        mAnomalies = new Hashtable<String,Anomaly>();
        mAnomalies.put("Anomaly_1", new Anomaly(50.3860,30.747,20, "Anomaly_1"));
        mAnomalies.put("Anomaly_2", new Anomaly(50.3885, 30.743,30, "Anomaly_2"));
        mAnomalies.put("Anomaly_3", new Anomaly(50.3845, 30.750,40, "Anomaly_3"));
        mAnomalies.put("Anomaly_4", new Anomaly(50.3835, 30.743,40, "Anomaly_4"));
        mAnomalies.put("Anomaly_5", new Anomaly(50.3895, 30.749,30, "Anomaly_5"));
        mAnomalies.put("Home", new Anomaly(50.44547, 30.53848,4, "Home"));
        mAnomalies.put("Rita", new Anomaly(50.3817, 30.5315,40, "Rita"));
        mAnomalies.put("Mohyla Academy", new Anomaly(50.4644,30.5190,30,"Mohyla Academy"));
        mAnomalies.put("Mohyla Academy corp 6", new Anomaly(50.4676,30.5239,30,"Mohyla Academy corp 6"));
        mAnomalies.put("Station Kontraktova square", new Anomaly(50.4652,30.5161,30,"Station Kontraktova square"));
        mAnomalies.put("Station Arsenalna", new Anomaly(50.4443, 30.5453,40, "Station Arsenalna"));
        mAnomalies.put("Station VDNH", new Anomaly(50.3822, 30.4769,40, "Station VDNH"));
        mAnomalies.put("Station Zhitomirska", new Anomaly(50.4565, 30.3645,40, "Station Zhitomirska"));
        mAnomalies.put("Faculty of Physics", new Anomaly(50.3810, 30.4729,60, "Faculty of Physics"));
        mAnomalies.put("Physics Hostel", new Anomaly(50.3874, 30.4739,20, "Physics Hostel"));
        mAnomalies.put("House of Kesar", new Anomaly(50.4609, 30.3626,40, "House of Kesar"));
        mAnomalies.put("Velika Kyshenia", new Anomaly(50.4443, 30.5421,10, "Velika Kyshenia"));

        mAnomalies.put("Mariinsky Park 1", new Anomaly(50.4460, 30.5389,10, "Mariinsky Park 1"));
        mAnomalies.put("Mariinsky Park 2", new Anomaly(50.4453, 30.5406,15, "Mariinsky Park 2"));
        mAnomalies.put("Mariinsky Park 3", new Anomaly(50.4447, 30.5420,20, "Mariinsky Park 3"));
        mAnomalies.put("Mariinsky Park 4", new Anomaly(50.4456, 30.5423,25, "Mariinsky Park 4"));
        mAnomalies.put("Mariinsky Park 5", new Anomaly(50.4466, 30.5423,30, "Mariinsky Park 5"));
        mAnomalies.put("Mariinsky Park 6", new Anomaly(50.4478, 30.5389,20, "Mariinsky Park 6"));
        mAnomalies.put("Mariinsky Park 7", new Anomaly(50.4467, 30.5399,20, "Mariinsky Park 7"));
        mAnomalies.put("Mariinsky Park 8", new Anomaly(50.4473, 30.5415,5, "Mariinsky Park 8"));
        mAnomalies.put("Mariinsky Park 9", new Anomaly(50.4481, 30.5398,15, "Mariinsky Park 9"));

        return  mAnomalies;
    }

    private void setAnomaliesToGeofence(Hashtable<String,Anomaly> anomaliesMap){
        Enumeration e = anomaliesMap.keys();
        while(e.hasMoreElements()){
            String i = (String) e.nextElement();
            Anomaly anomaly = anomaliesMap.get(i);
            CircleOptions options = new CircleOptions()
                    .center(new LatLng(anomaly.lat,anomaly.lng))
                    .radius(anomaly.radius)
                    .fillColor(Color.argb(30,200,0,200))
                    .strokeColor(Color.argb(70,200,0,200));
            mMap.addCircle(options);
        }
    }

    private boolean isInAnomaly(Location location){
        Enumeration e = mAnomalies.keys();
        while(e.hasMoreElements()){
            String i = (String) e.nextElement();
            Anomaly anomaly = mAnomalies.get(i);
            if(isDistanceLessThanAnomalyRadius(anomaly, location))
                return true;
        }
        return false;
    }

    private boolean isDistanceLessThanAnomalyRadius(Anomaly anomaly, Location location){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double accuracy = location.getAccuracy();
        double distance = Math.sqrt(Math.pow((latitude - anomaly.lat),2) + Math.pow((longitude - anomaly.lng),2));
        distance *= 100000;
        if(distance < anomaly.radius + accuracy)
            return true;
        else return false;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    private void startGeigerCounterSound(){
        if(mGeigerSound == null){
            createGeigerSoundRingtone();
        }
        if(!mGeigerSound.isPlaying())
            mGeigerSound.play();
    }

    private void stopGeigerCounterSound(){
        if(mGeigerSound == null){
            createGeigerSoundRingtone();
        }
        if(mGeigerSound.isPlaying())
            mGeigerSound.stop();
    }

    private void createGeigerSoundRingtone(){
        Uri notification = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.geiger_counter_sound);
        mGeigerSound = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }

    private class Anomaly{
        Anomaly(double _lat, double _lng, int _radius, String _name){
            lat = _lat;
            lng = _lng;
            radius = _radius;
            name = _name;
        }

        private double lat;
        private String name;
        private double lng;
        private int radius;

        public LatLng getPosition(){
            return new LatLng(lat, lng);
        }
    }

}
