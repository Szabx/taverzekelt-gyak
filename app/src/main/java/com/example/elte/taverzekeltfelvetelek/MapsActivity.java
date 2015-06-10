package com.example.elte.taverzekeltfelvetelek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    VanENet net_ellenoriz = new VanENet();
    Marker marker;
    private static String url_all_markers = "http://djzolee.net76.net/taverzekeles_get_all_markers.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKERS = "markers";
    private static final String TAG_ID = "id";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_DATE = "date";
    private static final String TAG_MEGJEGYZES = "megjegyzes";

    JSONArray markers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (net_ellenoriz.NetCheck(getApplicationContext())) {
            //new LoadAllMarkers().execute();
        } else {
            Toast.makeText(this, R.string.toast_no_internet_connection,
                    Toast.LENGTH_LONG).show();
        }
        setUpMapIfNeeded();

        // Add map listener for long clicks
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onMapLongClick(LatLng point) {

        final LatLng newPoint = point;

        // Init the dialog object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter description");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description;
                description = input.getText().toString();

                mMap.addMarker(new MarkerOptions()
                        .position(newPoint)
                        .title(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        marker.setTitle("This is where the reason comes");
//        marker.showInfoWindow();
        return false;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);


        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();


        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    /*class LoadAllMarkers extends AsyncTask<String, String, String> {
        boolean query_success = false;

        private ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage(getString(R.string.pd_load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (net_ellenoriz.ServerCheck(url_all_markers)) {
                JSONObject json = jParser.makeHttpRequest(url_all_markers,
                        params);

                Log.d("All Pesticides: ", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        markers = json.getJSONArray(TAG_MARKERS);

                        for (int i = 0; i < markers.length(); i++) {
                            JSONObject c = markers.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String lat = c.getString(TAG_LAT);
                            String lon = c.getString(TAG_LON);
                            String date = c.getString(TAG_DATE);
                            String megjegyzes = c.getString(TAG_MEGJEGYZES);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_NID, id);
                            map.put(TAG_NEV, name);

                            productsList.add(map);
                            query_success = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                query_success = false;
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (query_success) {
                ListAdapter adapter = new SimpleAdapter(Fo.this, productsList,
                        R.layout.elem, new String[]{TAG_NID, TAG_NEV},
                        new int[]{R.id.nid, R.id.nev});
                setListAdapter(adapter);
            } else {
                Toast toast = Toast.makeText(Fo.this,
                        R.string.toast_service_not_available,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            }

        }

    }*/

}
