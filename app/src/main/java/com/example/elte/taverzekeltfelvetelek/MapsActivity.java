package com.example.elte.taverzekeltfelvetelek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zza;
import com.google.android.gms.maps.model.internal.zzl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    VanENet net_ellenoriz = new VanENet();
    Marker marker;
    private String uj_megjegyzes = "";
    private String params = "";
    private double uj_lat;
    private double uj_lon;
    private static String url_all_markers = "http://www.taverzekelt2016.nhely.hu/taverzekeles_get_all_markers.php";
    private static String url_update_marker = "http://www.taverzekelt2016.nhely.hu/taverzekeles_upload_marker.php";
    private static String url_delete_marker = "http://www.taverzekelt2016.nhely.hu/taverzekeles_delete_marker.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKERS = "markers";
    private static final String TAG_ID = "id";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_DATE = "create_date";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_DATA = "data";
    private static final int REFRESH_DELAY = 5000; // milliseconds

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    JSONArray markers = null;
    ArrayList<DataObj> markersList, addedMarkersList, removedMarkersList;
    private HashMap<Marker, DataObj> markerData;
    private int changedMarkers = 0; // 0-no actions; 1-added markers; 2-removed markers
    private Handler h = new Handler(); // Refresh handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout to main layout
        setContentView(R.layout.activity_maps);

        // List of markers from DB, empty at first
        markersList = new ArrayList<DataObj>();

        markerData = new HashMap<Marker, DataObj>();

        // If device has active internet, init loader, else toast a message
        if (net_ellenoriz.NetCheck(getApplicationContext())) {
            new LoadAllMarkers().execute();
        } else {
            Toast.makeText(this, R.string.toast_no_internet_connection,
                    Toast.LENGTH_LONG).show();
        }

        // Setup of map and other things like current position
        setUpMapIfNeeded();

        // Add map listener for long clicks
        mMap.setOnMapLongClickListener(this);

        // Add marker click listener(default is show infowindow)
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

//    Refresh every now and then
    protected void refresh()
    {
        h.postDelayed(new Runnable() {
            public void run() {
                // Check if some markers have been added
                if (changedMarkers != 0) {
                    // Refresh maps
                    switch (changedMarkers) {
                        case 1: // Added
                            addedMarkersList.clear();
                            break;
                        case 2: // Removed
                            removedMarkersList.clear();
                            break;
                        default:
                            break;
                    }
                    // Reset flag
                    changedMarkers = 0;
                }
                h.postDelayed(this, REFRESH_DELAY);
            }
        }, REFRESH_DELAY);
    }

//    Detect long click and add marker

    @Override
    public void onMapLongClick(final LatLng point) {

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
                uj_megjegyzes = description;
                Log.d("uj_megjegyzes", uj_megjegyzes);
                uj_lat = point.latitude;
                uj_lon = point.longitude;
                params = "lat=" + uj_lat + "&lon=" + uj_lon + "&comment=" + uj_megjegyzes;
                Log.d("lat: ", Double.toString(uj_lat));
                Log.d("lon: ", Double.toString(uj_lon));

                uploadMarker(newPoint, description);
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

//    Call marker upload async task

    private void uploadMarker(LatLng p, String m){
        MarkerUpload ma = new MarkerUpload();

        ma.addParam(new BasicNameValuePair("lat", p.latitude+""));
        ma.addParam(new BasicNameValuePair("lon", p.longitude+""));
        ma.addParam(new BasicNameValuePair("comment", m));

        if (net_ellenoriz.NetCheck(getApplicationContext())) {
            ma.execute();
        } else {
            Toast.makeText(MapsActivity.this,
                    R.string.toast_no_internet_connection,
                    Toast.LENGTH_SHORT).show();
        }
    }

//    Override default marker click action

    @Override
    public boolean onMarkerClick(Marker marker) {
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
     * This is where we can add markers or lines, add listeners or move the camera
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        /**
         *
         * Custom marker info window
         *
         */

            // Setting a custom info window adapter for the google map
            GoogleMap.InfoWindowAdapter iwa;
            iwa = new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker m) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker m) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                    // Getting the position from the marker
                    LatLng latLng = m.getPosition();

                    // Getting reference to the TextView to set latitude
                    TextView desc = (TextView) v.findViewById(R.id.description);

                    desc.setText(m.getTitle());

                    Button btn = (Button)v.findViewById(R.id.delete);

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            };
            mMap.setInfoWindowAdapter(iwa);

        /**
         *
         * Capture info window click event
         *
         * @Info    Notice!! - this captures WHOLE infowindow click, not only delete button. It's bad practice to use dynamic
         *          content in infowindow, but there exists a pretty detailed and complicated workaround here:
         * @link    http://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go
         *
         */

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker)
                {
                    final Integer markerId = Integer.parseInt(marker.getSnippet());


                    // Confirm dialog box
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Remove marker")
                            .setMessage("Are you sure you want to remove this marker?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MarkerDelete ma = new MarkerDelete();
                                    ma.addParam(new BasicNameValuePair("marker_id", Integer.toString(markerId)));
                                    ma.execute();

                                    // Delete marker from map
                                    marker.remove();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
//         Get current location
            try
            {
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
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }

    //    Communication with server in background
//    Show all markers asyc task

    class LoadAllMarkers extends AsyncTask<String, String, String> {
        boolean query_success = false;

        private ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();

        private String[] markerInfo;

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

                Log.d("All Markers: ", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("Load marker success: ", "yes");
                        markers = json.getJSONArray(TAG_MARKERS);

                        for (int i = 0; i < markers.length(); i++) {
                            JSONObject c = markers.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String lat = c.getString(TAG_LAT);
                            String lon = c.getString(TAG_LON);
                            String date = c.getString(TAG_DATE);
                            String comment = c.getString(TAG_COMMENT);

                            try {
                                java.util.Date newDate   = df.parse(date);

                                DataObj markerObj   = new DataObj(Integer.parseInt(id.trim()), Float.parseFloat(lat.trim()), Float.parseFloat(lon.trim()), comment.trim(), newDate);

                                markersList.add(markerObj);
                                // InnerMarkerContainer.add(Integer.parseInt(id));
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        Log.d("MarkerList content : ", markersList.toString());
                        query_success = true;
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

                for (DataObj currentObj: markersList) {
                    String idString = Integer.toString(currentObj.getId());
                    Marker m        = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(currentObj.getLat(), currentObj.getLng()))
                                        .title(currentObj.getMessage())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .snippet(idString));
                    markerData.put(m, currentObj);
                }
            } else {
                Toast toast = Toast.makeText(MapsActivity.this,
                        R.string.toast_service_not_available,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            }

        }

    }

//    Add marker async task

    class MarkerUpload extends AsyncTask<String, String, String> {
        boolean query_successMu = false;

        // Returned params
        Integer id;
        String description;
        String lat;
        String lon;

        private ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        private List<NameValuePair> marker_params = new ArrayList<NameValuePair>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage(getString(R.string.pd_load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected void addParam(NameValuePair nvp)
        {
            marker_params.add(nvp);
        }

        protected String doInBackground(String... args) {
            if (net_ellenoriz.ServerCheck(url_update_marker)) {
                JSONObject jsonMu = jParser.makeHttpRequest(url_update_marker,
                        marker_params);
                try {
                    int success         = jsonMu.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        query_successMu = true;

                        // Get info for new marker

                        id              = jsonMu.getInt(TAG_ID);
                        description     = jsonMu.getString(TAG_COMMENT);
                        lat             = jsonMu.getString(TAG_LAT);
                        lon             = jsonMu.getString(TAG_LON);
                    }
                    else
                    {
                        query_successMu = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                query_successMu = false;
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (query_successMu) {

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Float.parseFloat(lat), Float.parseFloat(lon)))
                        .title(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(Integer.toString(id)));

                Toast toast = Toast.makeText(MapsActivity.this, R.string.toast_update_marker_success,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            } else {
                Toast toast = Toast.makeText(MapsActivity.this, R.string.toast_update_marker_error,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            }

        }

    }

//    Delete marker async task

    class MarkerDelete extends AsyncTask<String, String, String> {
        boolean query_successMu = false;

        private ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        private List<NameValuePair> marker_params = new ArrayList<NameValuePair>();

        protected void addParam(NameValuePair nvp)
        {
            marker_params.add(nvp);
        }

        protected String doInBackground(String... args) {
            if (net_ellenoriz.ServerCheck(url_delete_marker)) {

                JSONObject jsonMu = jParser.makeHttpRequest(url_delete_marker, marker_params);

                try {
                    int success     = jsonMu.getInt(TAG_SUCCESS);
                    String message  = jsonMu.getString(TAG_COMMENT);

                    if (success == 1) {
                        query_successMu = true;
                    }
                    else
                    {
                        query_successMu = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                query_successMu = false;
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            if (query_successMu) {
                Toast toast = Toast.makeText(MapsActivity.this, R.string.toast_delete_marker_success,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            } else {
                Toast toast = Toast.makeText(MapsActivity.this, R.string.toast_delete_marker_error,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.show();
            }

        }

    }
}
