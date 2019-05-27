package com.pavithra.roadsy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pavithra.roadsy.location.Location;
import com.pavithra.roadsy.request_service.RequiredService;
import com.pavithra.roadsy.request_service.ServiceRequestCall;

import java.util.ArrayList;
import java.util.List;

public class ClientRequestDisplay extends AppCompatActivity implements OnMapReadyCallback {

    ListView requiredServicesListView;
    TextView descriptionOfRequestedService,isToolsAvailableTextView,distanceToClientTextView;
    Button acceptBtn,rejectBtn;

    private MapView mapView;
    private GoogleMap googleMap;
    private FirebaseDatabase firebaseDatabase;
    private static final String MAP_VIEW_BUNDLE_KEY = "ClientLocationMapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_request_display);



        Intent intent=getIntent();
        final ServiceRequestCall serviceRequestCall=(ServiceRequestCall)intent.getSerializableExtra("service-request-call-for-mechanic");


        String clentUid=serviceRequestCall.getClient().getFirebaseUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(clentUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User serviceProvider=dataSnapshot.getValue(User.class);
                if(serviceProvider!=null) {
                    String lat = serviceProvider.getLocation().getLatitude();
                    String lng = serviceProvider.getLocation().getLongitude();
                    LatLng serviceProviderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    if(googleMap!=null) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(serviceProviderLocation).snippet("client").title("Client"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(serviceProviderLocation));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(serviceProviderLocation, 18.0f));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        requiredServicesListView=findViewById(R.id.requiredServiceListForMeachanicaListView);
        descriptionOfRequestedService=findViewById(R.id.requiredServiceDescriptionForMechanicTextView);
        isToolsAvailableTextView=findViewById(R.id.toolsAvailabilityForMechanicTextView);
        distanceToClientTextView=findViewById(R.id.distanceToClientTextView);
        acceptBtn=findViewById(R.id.acceptRequestBtn);
        rejectBtn=findViewById(R.id.rejectRequestBtn);

        List<String> requiredServiceList=new ArrayList<>();
        for (RequiredService requiredService:serviceRequestCall.getRequiredServiceList()){
            requiredServiceList.add(requiredService.getName());
        }

        String telephone=serviceRequestCall.getClient().getTelephone();
        String description=serviceRequestCall.getAdditionalServiceRequestDetail().getDescription();
        boolean isToolsAvailable=serviceRequestCall.getAdditionalServiceRequestDetail().isToolsAvailable();
        Location clientLocation=serviceRequestCall.getClient().getLocation();
        Location serviceProviderLocation=serviceRequestCall.getServiceProvider().getLocation();


        ArrayAdapter<String> arrayAdapterForRequiredServiceList=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,requiredServiceList);
        requiredServicesListView.setAdapter(arrayAdapterForRequiredServiceList);


        descriptionOfRequestedService.setText(description);
        if(isToolsAvailable){
            isToolsAvailableTextView.setText("Yes");
        }else{
            isToolsAvailableTextView.setText("No");
        }

        android.location.Location clientLocationObj=new android.location.Location("");
        clientLocationObj.setLongitude(Double.parseDouble(clientLocation.getLongitude()));
        clientLocationObj.setLatitude(Double.parseDouble(clientLocation.getLatitude()));

        android.location.Location serviceProviderLocationObj=new android.location.Location("");
        serviceProviderLocationObj.setLongitude(Double.parseDouble(serviceProviderLocation.getLongitude()));
        serviceProviderLocationObj.setLatitude(Double.parseDouble(serviceProviderLocation.getLatitude()));

        float distanceToClient=clientLocationObj.distanceTo(serviceProviderLocationObj);
        distanceToClientTextView.setText(distanceToClientTextView.getText()+"  "+String.valueOf(distanceToClient)+"m");

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JobProgressWithLiveLocationUpdate.class);
                intent.putExtra("service-request-call-for-mechanic",serviceRequestCall);
                startActivity(intent);
                finish();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle mapViewBundle=null;
        if(savedInstanceState!=null){
            mapViewBundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView=findViewById(R.id.mapViewForClientLocation);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

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
}
