package airsoft.stalker.pda;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.os.Vibrator;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    //private GeofencingClient mGeofencingClient;
    //private List<Geofence> mGeofenceList;
    private Hashtable<String,Anomaly> mAnomalies;
    //private PendingIntent mGeofencePendingIntent;
    private Context mContext;
    Ringtone mGeigerSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //mGeofenceList = new ArrayList<Geofence>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if(isInAnomaly(location)){
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        //v.vibrate(500);
                        startGeigerCounterSound();
                    }
                    else{
                        stopGeigerCounterSound();
                    }
                }
            };
        };
        //mGeofencingClient = LocationServices.getGeofencingClient(this);

        setContentView(R.layout.activity_maps);
        startLocationUpdates();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
        Hashtable<String,Anomaly> anomalies = addAnomalies();
        setAnomaliesToGeofence(anomalies);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           /* mGeofencingClient.addGeofences(getGeofencingRequest(),getGeofencePendingIntent()).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String a = "A";
                }
            });*/
            mMap.setMyLocationEnabled(true);

        }
        else {
            // Show rationale and request permission.
        }
        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng bootcamp = new LatLng(50.3865, 30.747);
        mMap.addMarker(new MarkerOptions().position(bootcamp).title("Bootcamp"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mAnomalies.get("Home").getPosition(),17));

    }

    public Hashtable<String,Anomaly> addAnomalies(){
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
           // setGeofence(anomaly);
        }
    }

    private boolean isInAnomaly(Location location){
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        double accuracy = location.getAccuracy();
        Enumeration e = mAnomalies.keys();
        while(e.hasMoreElements()){
            String i = (String) e.nextElement();
            Anomaly anomaly = mAnomalies.get(i);
            double distance = Math.sqrt(Math.pow((latitude - anomaly.lat),2) + Math.pow((longtitude - anomaly.lng),2));
            distance *= 100000;
            if(distance < anomaly.radius + accuracy)
                return true;
        }
        return false;
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

   /* private void setGeofence(Anomaly entry){
        if(mGeofenceList == null)
            mGeofenceList = new ArrayList<Geofence>();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(entry.name)

                .setCircularRegion(
                        entry.lat,
                        entry.lng,
                        entry.radius
                )
                .setExpirationDuration(1000000000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(300)
                .build());
    }*/

    /*private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }*/

   /* private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        mGeofencePendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }*/

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

   /* public class GeofenceTransitionsIntentService extends IntentService {

        public GeofenceTransitionsIntentService(String name) {
            super(name);
        }

        protected void onHandleIntent(Intent intent) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            }else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }
    }*/
}
