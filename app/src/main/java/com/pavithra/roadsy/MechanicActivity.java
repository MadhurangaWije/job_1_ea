package com.pavithra.roadsy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.location.GetAddressTask;
import com.pavithra.roadsy.login.LoginActivity;
import com.pavithra.roadsy.util.PermissionUtils;

import java.util.HashMap;

public class MechanicActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_SIGNIN = 1;
    Button signOutBtn;

    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private LatLng myPosition;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private Location serviceRequestPlacementLocation;

    LocationRequest  locationRequest;
    GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mechanicMap);
        mapFragment.getMapAsync(this);

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mechanicMap);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(1, token);
                        Log.d("FCM", token);
//                        Toast.makeText(CurrentLocationActivity.this, token, Toast.LENGTH_SHORT).show();

                        FirebaseUser user=firebaseAuth.getCurrentUser();
                        final FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=database.getReference("users").child(user.getUid());
                        databaseReference.child("fcmToken").setValue(token);
                        databaseReference.child("firebaseUid").setValue(user.getUid());

                    }
                });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        signOutBtn=findViewById(R.id.signOutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        serviceRequestPlacementLocation=new Location("");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(1, token);
                        Log.d("FCM", token);

                        FirebaseUser user=firebaseAuth.getCurrentUser();
                        final FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=database.getReference("users").child(user.getUid());
                        databaseReference.child("fcmToken").setValue(token);
                        databaseReference.child("firebaseUid").setValue(user.getUid());

                    }
                });
    }

    private void signOut(){
        firebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNIN);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(getApplicationContext(),"Mechanic activity locationchanged"+location,Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18.0f));
        updateCurrentLocationInFirebase(latLng);
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();

                            // Getting longitude of the current location
                            double longitude = location.getLongitude();

                            serviceRequestPlacementLocation.setLatitude(latitude);
                            serviceRequestPlacementLocation.setLongitude(longitude);

                            // Creating a LatLng object for the current location
                            LatLng latLng = new LatLng(latitude, longitude);

                            com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));

                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=database.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                            databaseReference.child("location").setValue(myLocation);


                            myPosition = new LatLng(latitude, longitude);
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(myPosition).snippet("adsbahsbdha").title("My Position"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18.0f));
                            //get current address by invoke an AsyncTask object
                            //new GetAddressTask(MechanicActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                });


    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void updateCurrentLocationInFirebase(LatLng latLng){

        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude),String.valueOf(latLng.latitude));
        HashMap map = new HashMap();
        map.put("location", myLocation);

        databaseReference.updateChildren(map);

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
