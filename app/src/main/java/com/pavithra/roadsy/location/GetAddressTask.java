package com.pavithra.roadsy.location;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetAddressTask extends AsyncTask<String,Void,HashMap<String,String>> {
    private CurrentLocationActivity activity;

    public GetAddressTask(CurrentLocationActivity activity){
        this.activity=activity;
    }

    @Override
    protected HashMap<String,String> doInBackground(String... params) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1);

            //get current Street name
            String address = addresses.get(0).getAddressLine(0);

            //get current province/City
            String province = addresses.get(0).getAdminArea();

            //get country
            String country = addresses.get(0).getCountryName();

            //get postal code
            String postalCode = addresses.get(0).getPostalCode();

            //get place Name
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            HashMap<String,String> locationData= new HashMap<>();
            locationData.put("address",address);
            locationData.put("province",province);
            locationData.put("country",country);
            locationData.put("postalCode",postalCode);
            locationData.put("knownPlaces",knownName);

            return locationData;


//            return "Street: " + address + "\n" + "City/Province: " + province + "\nCountry: " + country
//                    + "\nPostal CODE: " + postalCode + "\n" + "Place Name: " + knownName;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * When the task finishes, onPostExecute() call back data to Activity UI and displays the address.
     * @param stringStringHashMap
     */
    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        super.onPostExecute(stringStringHashMap);
        activity.callBackDataFromAsyncTask(stringStringHashMap);
    }
}
