package whitewire.behappy.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import whitewire.behappy.DatabaseHandler;
import whitewire.behappy.R;

import static whitewire.behappy.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static whitewire.behappy.R.id.day;

/**
 * Created by Claudio on 18-Mar-17.
 */

public class QuestionFragment extends Fragment implements
        ConnectionCallbacks, OnConnectionFailedListener {

    Calendar calendar = Calendar.getInstance();
    int mood = 0;
    int age = 0;
    int gender = 0;
    Double latitude = 0.0;
    Double longitude = 0.0;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.question, container, false);

        final RadioButton yesButton = (RadioButton) rootView.findViewById(R.id.yesButton);
        final RadioButton noButton = (RadioButton) rootView.findViewById(R.id.noButton);
        Button saveButton = (Button) rootView.findViewById(R.id.saveButton);

        // Keyboard feature to not always be showing
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        // To get location
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(getActivity(), "Not connected...", Toast.LENGTH_SHORT).show();
        }

        // To make sure app has permission
        checkLocationPermission();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = 1;
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood = 2;
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputTxt = (EditText) getView().findViewById(R.id.inputText);

                // Getting data from shared preferences
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                age = settings.getInt("age", 0);
                gender = settings.getInt("gender", 0);

                // Not continuing with process until user has chosen feeling
                if (mood == 0) {
                    Toast.makeText(getContext(), "Choose how you are feeling today.",
                            Toast.LENGTH_SHORT).show();
                } else if (latitude == 0 || longitude == 0) {
                    Toast.makeText(getContext(), "Please restart the app as location was not found",
                            Toast.LENGTH_LONG).show();
                } else {
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int year = calendar.get(Calendar.YEAR);
                    Log.v("day", Integer.toString(day));
                    Log.v("month", Integer.toString(month));
                    Log.v("year", Integer.toString(year));

                    // Store EditText in Variable
                    String message = inputTxt.getText().toString();

                    // If user has entered a message continue
                    if (message.trim().length() > 0) {
                        DatabaseHandler db = new DatabaseHandler(getContext());
                        db.insertLabel(message, day, month, year, mood);
                        String city = getLocationName(latitude, longitude);
                        sendRequest(city, Double.toString(latitude), Double.toString(longitude), Integer.toString(mood), Integer.toString(gender),
                                Integer.toString(age), Integer.toString(day), Integer.toString(month),
                                Integer.toString(year));

                        // Resetting UI
                        yesButton.setChecked(false);
                        noButton.setChecked(false);
                        inputTxt.getText().clear();
                        mood = 0;

                    } else {
                        Toast.makeText(getContext(), "Please enter label name",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    public void sendRequest(final String city, final String latitude, final String longitude, final String mood,
                            final String gender, final String age, final String day, final String month, final String year) {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://behappyapp.herokuapp.com/polls/add/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "Your entry has been successful",
                                Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error is: " + error.toString(),
                        Toast.LENGTH_LONG).show();
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("age", age);
                parameters.put("city", city);
                parameters.put("longitude", longitude);
                parameters.put("latitude", latitude);
                parameters.put("mood", mood);
                parameters.put("gender", gender);
                parameters.put("day", day);
                parameters.put("month", month);
                parameters.put("year", year);
                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public boolean checkLocationPermission() {
        //Toast.makeText(getActivity(), "wtf", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // stop GoogleApiClient
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            //Toast.makeText(getActivity(),"Latitude: "+ String.valueOf(mLastLocation.getLatitude())+" Longitude: "+
                    //String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            Toast.makeText(getActivity(), "lat is " + latitude + " long is " + longitude, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public String getLocationName(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        try {
            // Getting list of addresses from geocoder
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adrs : addresses) {
                if (adrs != null) {
                    String city = adrs.getLocality();

                    if (city != null && !city.equals("")) {
                        //Toast.makeText(getActivity(), "city is: " + city, Toast.LENGTH_SHORT).show();
                        return city;
                    } else {
                        //Toast.makeText(getActivity(), "city is null ", Toast.LENGTH_SHORT).show();
                        return "city error";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } return "IOException";
    }
}