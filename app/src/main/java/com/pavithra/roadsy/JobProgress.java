package com.pavithra.roadsy;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pavithra.roadsy.request_service.Controller;
import com.pavithra.roadsy.request_service.ServiceRequestCall;
import com.pavithra.roadsy.request_service.ServiceStatusCall;
import com.pavithra.roadsy.util.PermissionUtils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobProgress extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener,GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    Button visitedBtn,completedBtn,rejectBtn;
    private MapView mapView;
    private GoogleMap googleMap;
    private FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    private static final String MAP_VIEW_BUNDLE_KEY = "JobProgressMapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_progress);
        Bundle mapViewBundle=null;
        if(savedInstanceState!=null){
            mapViewBundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.clientMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        Intent intent=getIntent();
        final ServiceRequestCall serviceRequestCall=(ServiceRequestCall) intent.getSerializableExtra("service-request-call-for-mechanic");

        final String token=serviceRequestCall.getClient().getFcmToken();


        visitedBtn=findViewById(R.id.visitedBtn);
        completedBtn=findViewById(R.id.completedBtn);
        rejectBtn=findViewById(R.id.rejectBtn);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        String clentUid=serviceRequestCall.getClient().getFirebaseUid();

        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(clentUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User serviceProvider=dataSnapshot.getValue(User.class);
                String lat=serviceProvider.getLocation().getLatitude();
                String lng=serviceProvider.getLocation().getLongitude();
                LatLng serviceProviderLocation=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                if(googleMap!=null){
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(serviceProviderLocation).snippet("client").title("Client"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(serviceProviderLocation));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(serviceProviderLocation, 18.0f));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        attendingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Controller controller=new Controller();
//                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Attending...",token);
//                System.out.println(serviceStatusCall.getDataString());
//                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });

        visitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Visited...",token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldn't able to update the status...\n"+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Completed...",token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Rejected...",token);
                controller.sendStatus(serviceStatusCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


//        Bundle mapViewBundle=null;
//        if(savedInstanceState!=null){
//            mapViewBundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
//        }


//            mapView = findViewById(R.id.clientMapView);
//            mapView.onCreate(mapViewBundle);
//            mapView.getMapAsync(this);


    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMinZoomPreference(18.0f);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
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
        enableMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        updateCurrentLocationInFirebase(latLng);
        Toast.makeText(getApplicationContext(), "updating", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentLocationInFirebase(LatLng latLng){

        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude),String.valueOf(latLng.latitude));
        HashMap map = new HashMap();
        map.put("location", myLocation);

        databaseReference.updateChildren(map);

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }



    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);
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
//            mPermissionDenied = true;
        }
    }


}
