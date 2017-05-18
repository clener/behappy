package whitewire.behappy.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import whitewire.behappy.MapsActivity;
import whitewire.behappy.R;


/**
 * Created by Claudio on 19-Mar-17.
 */

public class NetworkResultsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.network_results, container, false);

        Button submitButton = (Button) rootView.findViewById(R.id.load_updates);
        Button mapsButton = (Button) rootView.findViewById(R.id.maps_button);
        ArrayList<Integer> ageArray = new ArrayList<>();
        final Spinner ageStartSpinner = (Spinner) rootView.findViewById(R.id.spinner_start_age);
        final Spinner ageEndSpinner = (Spinner) rootView.findViewById(R.id.spinner_end_age);
        final TextView happyCityView = (TextView) rootView.findViewById(R.id.city_results);
        final TextView happyMalesView = (TextView) rootView.findViewById(R.id.happy_males);
        final TextView happyFemalesView = (TextView) rootView.findViewById(R.id.happy_females);
        final TextView happyBetweenView = (TextView) rootView.findViewById(R.id.between_ages);

        for (int i = 1; i <= 120; i++) {
            ageArray.add(i);
        }

        // Creating adapter for spinner
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, ageArray);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ageStartSpinner.setAdapter(ageAdapter);
        ageEndSpinner.setAdapter(ageAdapter);
        ageStartSpinner.setSelection(0);
        ageEndSpinner.setSelection(119);

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String happyCity = happyCityView.getText().toString();
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("city", happyCity);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int upperAgeBound = Integer.parseInt(ageEndSpinner.getSelectedItem().toString());
                final int lowerAgeBound = Integer.parseInt(ageStartSpinner.getSelectedItem().toString());

                // Checking if location is turned on
                LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}


                if (upperAgeBound < lowerAgeBound) {
                    Toast.makeText(getContext(), "Please enter valid parameters",
                            Toast.LENGTH_SHORT).show();
                } else if (gps_enabled == false && network_enabled == false) {
                    Toast.makeText(getContext(), "Please enable your location",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Setting up network request
                    RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

                    // Start the queue
                    mRequestQueue.start();
                    String url = "http://behappyapp.herokuapp.com/polls/retreive_all/";

                    // Formulate the request and handle the response.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        int totalMales = obj.getInt("totalMales");
                                        int totalFemales = obj.getInt("totalFemales");
                                        int happyMales = obj.getInt("happyMales");
                                        int happyFemales = obj.getInt("happyFemales");
                                        int happyBounds = obj.getInt("happyBounds");
                                        String happyCity = obj.getString("happyCity");

                                        if (totalMales != 0) {
                                            double newMales = happyMales * 100 / totalMales;
                                            happyMalesView.setText(String.format("%d%%", (int) newMales));
                                        } else {
                                            happyMalesView.setText("There are no males");
                                        }
                                        if (totalFemales != 0) {
                                            double newFemales = happyFemales * 100 / totalFemales;
                                            happyFemalesView.setText(String.format("%d%%", (int) newFemales));
                                        } else {
                                            happyFemalesView.setText("There are no females");
                                        }

                                        happyBetweenView.setText(String.format("%d", happyBounds));
                                        happyCityView.setText(happyCity);

                                    } catch (JSONException j) {
                                        Toast.makeText(getContext(), j.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), error.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parameters = new HashMap<>();
                            parameters.put("lowerAgeBound", Integer.toString(lowerAgeBound));
                            parameters.put("upperAgeBound", Integer.toString(upperAgeBound));
                            return parameters;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/x-www-form-urlencoded");
                            return params;
                        }
                    };

                    // Add the request to the RequestQueue.
                    mRequestQueue.add(stringRequest);
                }
            }
        });
        return rootView;
    }
}
