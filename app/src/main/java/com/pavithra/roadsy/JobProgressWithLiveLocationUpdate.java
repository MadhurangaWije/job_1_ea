package com.pavithra.roadsy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.location.GetAddressTask;
import com.pavithra.roadsy.request_service.Controller;
import com.pavithra.roadsy.request_service.RequestService;
import com.pavithra.roadsy.request_service.ServiceRequestCall;
import com.pavithra.roadsy.request_service.ServiceStatusCall;
import com.pavithra.roadsy.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobProgressWithLiveLocationUpdate extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_SIGNIN = 2;
    private static final int REQUEST_LOGIN = 3;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng myPosition;

    private FusedLocationProviderClient fusedLocationClient;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    User loggedInUser;

    private boolean displayLocationUpdate = true;

    private Location serviceRequestPlacementLocation;

    private PopupWindow mPopupWindow;
    private ConstraintLayout currentLocationActivityLayout;

    private static CustomServiceProviderListAdapter adapter;


    private PopupWindow mechanicSearchingProgressWindow;
    private Button visitedBtn;
    private Button completedBtn;
    private Button rejectBtn;

    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_progress_with_live_location_update2);


        visitedBtn = findViewById(R.id.job_progress_visted_btn);
        completedBtn = findViewById(R.id.job_progress_completed_btn);
        rejectBtn = findViewById(R.id.job_progress_rejected_btn);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("FCM", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
////                        String msg = getString(1, token);
//                        Log.d("FCM", token);
////                        Toast.makeText(CurrentLocationActivity.this, token, Toast.LENGTH_SHORT).show();
//
//                        FirebaseUser user=firebaseAuth.getCurrentUser();
//                        final FirebaseDatabase database=FirebaseDatabase.getInstance();
//                        DatabaseReference databaseReference=database.getReference("users").child(user.getUid());
//                        databaseReference.child("fcmToken").setValue(token);
//                        databaseReference.child("firebaseUid").setValue(user.getUid());
//
//                    }
//                });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        serviceRequestPlacementLocation = new Location("");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedInUser = dataSnapshot.getValue(User.class);
                if (loggedInUser != null) {
                    updateUser(loggedInUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        Intent intent = getIntent();
        final ServiceRequestCall serviceRequestCall = (ServiceRequestCall) intent.getSerializableExtra("service-request-call-for-mechanic");

        String clentUid = serviceRequestCall.getClient().getFirebaseUid();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(clentUid);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User serviceProvider = dataSnapshot.getValue(User.class);
                String lat = serviceProvider.getLocation().getLatitude();
                String lng = serviceProvider.getLocation().getLongitude();
                LatLng serviceProviderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                if (mMap != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(serviceProviderLocation).snippet("client").title("Client"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(serviceProviderLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(serviceProviderLocation, 18.0f));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final String token = serviceRequestCall.getClient().getFcmToken();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        visitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller = new Controller();
                ServiceStatusCall serviceStatusCall = new ServiceStatusCall("Visited...", token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(), "Status Send...", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong, couldn't able to update the status...\n" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller = new Controller();
                ServiceStatusCall serviceStatusCall = new ServiceStatusCall("Completed...", token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(), "Status Send...", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong, couldnt able to update the status...", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller = new Controller();
                ServiceStatusCall serviceStatusCall = new ServiceStatusCall("Rejected...", token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(), "Status Send...", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong, couldnt able to update the status...", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void signOut() {
        firebaseAuth.signOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNIN);
        finish();
    }

    private void updateUser(User user) {
        this.loggedInUser = user;
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
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

//        displayCurrentLocation();

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

                            // Creating a LatLng object for the current location
                            LatLng latLng = new LatLng(latitude, longitude);

                            com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                            databaseReference.child("location").setValue(myLocation);

                            myPosition = new LatLng(latitude, longitude);
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions().position(myPosition).snippet("adsbahsbdha").title("My Position"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18.0f));

                            //get current address by invoke an AsyncTask object
//                            new GetAddressTask(JobProgressWithLiveLocationUpdate.this).execute(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                });

    }


    private void displayCurrentLocation() {
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

                            com.pavithra.roadsy.location.Location myLocation = new com.pavithra.roadsy.location.Location(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                            databaseReference.child("location").setValue(myLocation);

                            myPosition = new LatLng(latitude, longitude);
                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions().position(myPosition).snippet("adsbahsbdha").title("My Position"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18.0f));
                            //get current address by invoke an AsyncTask object
//                            new GetAddressTask(CurrentLocationActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));

                        }
                    }
                });
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
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
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
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
    public void onLocationChanged(Location location) {
//        Toast.makeText(getApplicationContext(),"JobProgress mylocation update",Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//            mMap.clear();
//            mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            updateCurrentLocationInFirebase(latLng);


    }

    private void updateCurrentLocationInFirebase(LatLng latLng) {

//        Toast.makeText(getApplicationContext(), "latLang updating" + latLng, Toast.LENGTH_LONG).show();

        DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        com.pavithra.roadsy.location.Location myLocation = new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude), String.valueOf(latLng.latitude));
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
