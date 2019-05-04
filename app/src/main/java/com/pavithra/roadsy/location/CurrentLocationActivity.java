package com.pavithra.roadsy.location;

//import android.support.v4.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.pavithra.roadsy.CustomServiceProviderListAdapter;
import com.pavithra.roadsy.MainActivity;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.login.LoginActivity;
import com.pavithra.roadsy.request_service.RequestService;
import com.pavithra.roadsy.util.PermissionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;




public class CurrentLocationActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,com.google.android.gms.location.LocationListener {

    private static final int REQUEST_SIGNIN = 2;
    private static final int REQUEST_LOGIN = 3;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng myPosition;
    private DrawerLayout drawerLayout;
    private EditText currentLocationEditText;

    private FusedLocationProviderClient fusedLocationClient;

    private Button iamhereBtn;
    private Button requestServiceBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    User loggedInUser;

    private boolean isCurrentLocationServiceRequest=false;

    private Location serviceRequestPlacementLocation;

    private PopupWindow mPopupWindow;
    private ConstraintLayout mRelativeLayout;

//    private ListView listView;
    private ArrayList<User> userArrayList;
    private static CustomServiceProviderListAdapter adapter;

    android.support.design.widget.FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        mRelativeLayout = findViewById(R.id.rl);

        userArrayList=new ArrayList<>();

        floatingActionButton=findViewById(R.id.floatingActionButton);
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

                    }
                });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        CurrentLocationActivity.this);

                alertDialogBuilder.setTitle("Logout...");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are You Sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                signOut();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDzE63rS8i_uzG3g1ak0fXqw9Z2W07EhJw");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setCountry("LK");


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACES", "Place: " + place.getName() + ", " + place.getId());
                Toast.makeText(getApplicationContext(),place.getLatLng()+"",Toast.LENGTH_LONG).show();
                isCurrentLocationServiceRequest=false;
//                searchLocation(place.getLatLng());
                LatLng latLng=place.getLatLng();
                if (latLng != null ) {
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            for(Address a:addressList){
//                System.out.println(a.toString());
//            }
//            Address address = addressList.get(0);
//            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("J"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    serviceRequestPlacementLocation.setLongitude(latLng.longitude);
                    serviceRequestPlacementLocation.setLongitude(latLng.latitude);

                    requestServiceBtn.setVisibility(View.VISIBLE);
//            Toast.makeText(getApplicationContext(),latLng.latitude+" "+latLng.longitude,Toast.LENGTH_LONG).show();
//
//            Location l=new Location("");
//            l.setLongitude(latLng.longitude);
//            l.setLongitude(latLng.latitude);

//                    updateCurrentLocationInFirebase(latLng);
//                    DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
//                    com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude),String.valueOf(latLng.latitude));
//                    HashMap map = new HashMap();
//                    map.put("location", myLocation);
//
//                    databaseReference.updateChildren(map);
                }

            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("PLACES", "An error occurred: " + status);

            }


        });


        serviceRequestPlacementLocation=new Location("");


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        FirebaseUser user=firebaseAuth.getCurrentUser();
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("users").child(user.getUid());



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedInUser=dataSnapshot.getValue(User.class);
                if(loggedInUser!=null) {
                    updateUser(loggedInUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        iamhereBtn=findViewById(R.id.iamhereBtn);
        requestServiceBtn=findViewById(R.id.requestServiceBtn);



        iamhereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentLocation();
                requestServiceBtn.setVisibility(View.VISIBLE);
//                isCurrentLocationServiceRequest=true;
//                signOut();
            }
        });

        requestServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllMechanicsNearMe(serviceRequestPlacementLocation);
                requestServiceBtn.setVisibility(View.GONE);
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        drawerLayout = findViewById(R.id.drawer_layout);




//        currentLocationEditText=findViewById(R.id.currentLocationEditText);

//        NavigationView navigationView = findViewById(R.id);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                        // set item as selected to persist highlight
//                        menuItem.setChecked(true);
//                        // close drawer when item is tapped
//                        drawerLayout.closeDrawers();
//
//                        // Add code here to update the UI based on the item selected
//                        // For example, swap UI fragments here
//
//                        return true;
//                    }
//                });


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        // Getting reference to the SupportMapFragment of activity_main.xml
//        SupportMapFragment fm = (SupportMapFragment)
//                getSupportFragmentManager().findFragmentById(R.id.map);


    }

    private void signOut(){
        firebaseAuth.signOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNIN);
        finish();
    }

    private void getAllMechanicsNearMe(Location location){
        final Location location1=location;
        final List<User> userList = new ArrayList<>();
        firebaseDatabase.getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            User user  = dsp.getValue(User.class);

                            if(user!=null && user.getLocation()!=null && user.getType().equals("mechanic")){
                                Location location2=new Location("");
                                location2.setLongitude(Double.parseDouble(user.getLocation().getLongitude()));
                                location2.setLatitude(Double.parseDouble(user.getLocation().getLatitude()));

                                double radius = 5000000000000.0;
                                double distance = location1.distanceTo(location2);

                                if (distance<radius){
                                    userList.add(user); //add result into array list
                                }
                            }
                        }

                        Toast.makeText(getApplicationContext(),userList.size()+" Mechanics found",Toast.LENGTH_LONG).show();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View customView = inflater.inflate(R.layout.service_provider_selection,null);

                        mPopupWindow= new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );


                        adapter= new CustomServiceProviderListAdapter(userList,getApplicationContext());
                        ListView listView = customView.findViewById(R.id.serviceProviderListView);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                User user= userList.get(position);

                                Toast.makeText(getApplicationContext(),user.getName(),Toast.LENGTH_LONG).show();

//                                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder());
                                mPopupWindow.dismiss();

                                System.out.println("7777777777777(((((((((((((( "+user.getFcmToken());
                                Intent intent = new Intent(getApplicationContext(), RequestService.class);
                                intent.putExtra("service-provider",user);
                                intent.putExtra("client",loggedInUser);
                                startActivityForResult(intent, REQUEST_LOGIN);
                                finish();

                                // This registration token comes from the client FCM SDKs.
//                                String registrationToken = "fuaE9Ixk220:APA91bGk-yTP-RXauPeV4xGQaJ6BH01E0Aow40IQZ3J-48Wj98GpDDVTTnGAsxelHtvpPY7ryMdeZjdlRzxMwVeKLToOIyzZJ3khEMceLs8lil4hErqehkUhZdMf9PidEvsoo9a-8FEB";

// See documentation on defining a message payload.
//                                Message message = Message.builder()
//                                        .putData("score", "850")
//                                        .putData("time", "2:45")
//                                        .setToken(registrationToken)
//                                        .build();

// Send a message to the device corresponding to the provided
// registration token.
//                                String response = FirebaseMessaging.getInstance().send(message);
// Response is a message ID string.
//                                System.out.println("Successfully sent message: " + response);



                            }
                        });

                        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




    }



    private void updateUser(User user){
        this.loggedInUser=user;
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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

//                            com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));



                            myPosition = new LatLng(latitude, longitude);
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(myPosition).snippet("adsbahsbdha").title("My Position"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18.0f));
                            //get current address by invoke an AsyncTask object
                            new GetAddressTask(CurrentLocationActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                });

    }


    private void displayCurrentLocation(){
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
                            new GetAddressTask(CurrentLocationActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));

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

    public void callBackDataFromAsyncTask(HashMap<String, String> addressData){
//           currentLocationEditText.setText(addressData.get("address"));
    }

    public void searchLocation(LatLng latLng) {
//        String location = currentLocationEditText.getText().toString();
//        List<Address> addressList = null;

        if (latLng != null ) {
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            for(Address a:addressList){
//                System.out.println(a.toString());
//            }
//            Address address = addressList.get(0);
//            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("J"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            Toast.makeText(getApplicationContext(),latLng.latitude+" "+latLng.longitude,Toast.LENGTH_LONG).show();
//
//            Location l=new Location("");
//            l.setLongitude(latLng.longitude);
//            l.setLongitude(latLng.latitude);

//            updateCurrentLocationInFirebase(latLng);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(isCurrentLocationServiceRequest){
//            mMap.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            updateCurrentLocationInFirebase(location);
        }

//        Toast.makeText(getApplicationContext(),location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_LONG).show();
    }

    private void updateCurrentLocationInFirebase(LatLng latLng){

        DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        com.pavithra.roadsy.location.Location myLocation=new com.pavithra.roadsy.location.Location(String.valueOf(latLng.longitude),String.valueOf(latLng.latitude));
        HashMap map = new HashMap();
        map.put("location", myLocation);

        databaseReference.updateChildren(map);

    }

}
