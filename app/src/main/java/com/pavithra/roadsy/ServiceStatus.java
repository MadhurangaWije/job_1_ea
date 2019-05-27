package com.pavithra.roadsy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.TextView;
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
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.location.GetAddressTask;
import com.pavithra.roadsy.request_service.ServiceRequestCall;
import com.pavithra.roadsy.util.PermissionUtils;

import java.util.HashMap;

public class ServiceStatus extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap googleMap;

    private TextView serviceStatusTextView;
    private Button contactServiceProviderBtn;
    private FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;


    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";



    private BroadcastReceiver serviceStatusUpdateBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.ServiceStatusUpdate")) {
                //Extract your data - better to use constants...
                String serviceStatus = intent.getStringExtra("service_status");
                if (serviceStatus.equals("Completed...") || serviceStatus.equals("Rejected...")) {
                    serviceCompleted();
                } else {
                    Toast.makeText(getApplicationContext(), serviceStatus, Toast.LENGTH_LONG).show();
                    serviceStatusTextView.setText(serviceStatus);
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_status);

        contactServiceProviderBtn=findViewById(R.id.contactServiceProviderBtn);
        serviceStatusTextView = findViewById(R.id.serviceStatusTextView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();



        final Intent intent = getIntent();
        final ServiceRequestCall serviceRequestCall = (ServiceRequestCall) intent.getSerializableExtra("service-request-call-for-mechanic");

        String serviceProviderUid = serviceRequestCall.getServiceProvider().getFirebaseUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(serviceProviderUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User serviceProvider = dataSnapshot.getValue(User.class);
                String lat = serviceProvider.getLocation().getLatitude();
                String lng = serviceProvider.getLocation().getLongitude();
                LatLng serviceProviderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(serviceProviderLocation).snippet("service provider").title("Service Provider"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(serviceProviderLocation));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(serviceProviderLocation, 18.0f));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.arrivalMap);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        contactServiceProviderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),ServiceProviderContact.class);
                intent1.putExtra("service-request-call-for-mechanic",serviceRequestCall);
                startActivity(intent1);
            }
        });


    }


    private void serviceCompleted() {
        finish();
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ServiceStatusUpdate");
        registerReceiver(serviceStatusUpdateBroadcastReciever, filter);

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

    }

    private void updateCurrentLocationInFirebase(LatLng latLng){

        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude),String.valueOf(latLng.latitude));
        HashMap map = new HashMap();
        map.put("location", myLocation);

        databaseReference.updateChildren(map);

    }

}
