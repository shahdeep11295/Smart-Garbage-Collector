package com.example.chauh.swms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {
    Connection conn;
    private GoogleMap mMap;
    int i=0;
    PowerManager pm;
    PowerManager.WakeLock wl;

    private ImageButton btnFindPath;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    public double c_lat = 0.0;
    public double c_long = 0.0;
    LatLng myplace = null;
    public double p1_lat=0.0;
    public double p1_lon=0.0;

    LatLng place1 = new LatLng(p1_lat,p1_lon),
            place2 = new LatLng(12.973147, 79.168113),
            place3 = new LatLng(12.966456, 79.162041),
            origin = null,
            destination = null,
            waypoints = null;




    @Override
    protected void onPause() {
        super.onPause();
        wl.release();//to release wake lock.
    }






    protected void onResume() {
        super.onResume();
        wl.acquire();//to acquire wake lock.
    }

    double bin_1=0;
    int bin_2=45;
    int bin_3=20;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getMyLocation();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wake lock");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);
        wl.acquire();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i1;
        fab = (FloatingActionButton) findViewById(R.id.refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getBinValue getbinvalue = new getBinValue();
//                getbinvalue.execute("");
//                getJSON("http://indoorproject.16mb.com/binValue.php");
                getJSON("http://smart11295.000webhostapp.com/binValue.php");
                progressDialog = ProgressDialog.show(MapsActivity.this, "Please wait.",
                        "Fetching Data From serevr...!", true);
                mMap.clear();



                //getbinvalue.execute("");





            }
        });

        onNetwork();


        i1 = getIntent();
        p1_lat= Double.parseDouble(i1.getStringExtra("plat"));
        p1_lon= Double.parseDouble(i1.getStringExtra("plon"));
        place1 = new LatLng(p1_lat,p1_lon);
        bin_1= Double.parseDouble(i1.getStringExtra("status"));
        Toast.makeText(this, bin_1+""+p1_lat+""+""+p1_lon, Toast.LENGTH_SHORT).show();
        //bin_1 = i1.getIntExtra("Bin1value", 0);
        //bin_2 = i1.getIntExtra("Bin2value", 0);
        //bin_3 = i1.getIntExtra("Bin3value", 0);
        btnFindPath = (ImageButton) findViewById(R.id.btnFindPath);
        //etOrigin = (EditText) findViewById(R.id.etOrigin);
        //etDestination = (EditText) findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMyLocation();
                if (myplace == null) {
                    Toast.makeText(MapsActivity.this, "Your Location is Not Found please wait...", Toast.LENGTH_SHORT).show();
                } else {
                    getMyLocation();
                    sendRequest();
                }


            }
        });
        // setPinPoint();

    }

    private void checkGPS() {
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("GPS is not enbled.");
            builder.setMessage("To Get Your Current Location You have To Enable Gps \n Are Sure Want to go to seeting to Enable GPS?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);

                    // Toast.makeText(MapsActivity.this, c_lat +","+c_long, Toast.LENGTH_SHORT).show();


                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            //Toast.makeText(this, "GPS is Not Enabled Please Enable Gps Here....", Toast.LENGTH_SHORT).show();
        }

    }
    public class Wrapper1{
        public String id1;
        public String value1;
        public String la;
        public String lo;
    }
//    public class getBinValue extends AsyncTask<String,String,Wrapper1> {
//        String z="";
//        String value="";
//        Boolean isSuccess = false;
//        protected void onPostExecute(Wrapper1 w)
//        {
//            String status=w.value1;
//            String p_lat=w.la;
//            String p_lon=w.lo;
//          bin_1= Double.parseDouble(status);
//            progressDialog.dismiss();
//            //bin_value.setText(r.toString());
//            //Toast.makeText(Home.this, r, Toast.LENGTH_SHORT).show();
//            if(isSuccess)
//            {
//                Toast.makeText(MapsActivity.this , "Login Successfull" , Toast.LENGTH_LONG).show();
//                //finish();
//
//            }
//        }
//        @Override
//        protected Wrapper1 doInBackground(String... params) {
//            conn = connectionclass(DbConfig.connectionstring);
//            z = "db connected";
//            Wrapper1 w = null;
//            if (conn == null) {
//                z = "chech internet connection";
//            } else {
//                String query = "select * from Bin_Status where Bin_id=1";
//                Statement stmt = null;
//                try {
//                    stmt = conn.createStatement();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    ResultSet rs = stmt.executeQuery(query);
//                    while (rs.next()) {
//                        String id = rs.getString("Bin_id");
//                        value = rs.getString("Status");
//                        String lat=rs.getString("Latitude");
//                        String lon=rs.getString("Longitude");
//                        w = new Wrapper1();
//                        w.id1 = id;
//                        w.value1 = value;
//                        w.la=lat;
//                        w.lo=lon;
//                    }
//                    conn.close();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            return w;
//        }
//    }
    @SuppressLint("NewApi")
    public Connection connectionclass(String connectionstring){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        try{
            Class.forName(DbConfig.driver);
            connection= DriverManager.getConnection(connectionstring);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public  void getJSON(final String link) {
        String status = null, p_lat = null, p_long = null;
        /*
        * As fetching the json string is a network operation
        * And we cannot perform a network operation in main thread
        * so we need an AsyncTask
        * The constrains defined here are
        * Void -> We are not passing anything
        * Void -> Nothing at progress update as well
        * String -> After completion it should return a string and it will be the json string
        * */
        final String[] finalP_long = {p_long};
        final String[] final_lat = {p_lat};
        final String[] final_status = {status};
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONArray jsonArray = new JSONArray(s);
//                    String[] heroes = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        final_status[0] = obj.getString("status");
                        final_lat[0] = obj.getString("lat");
                        finalP_long[0] = obj.getString("long");
                    }
                } catch (Exception e) {

                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MapsActivity.this, MapsActivity.class);
                i.putExtra("status", final_status[0]);
                i.putExtra("plat", final_lat[0]);
                i.putExtra("plon", finalP_long[0]);
                startActivity(i);
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
//
                try {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
//                HttpHandler sh = new HttpHandler();
//                String jsonStr = sh.makeServiceCall(url);
//
//
//
//                if (jsonStr != null) {
//                    try {
//                        JSONObject jsonObj = new JSONObject(jsonStr);
//
//                        // Getting JSON Array node
//                        JSONArray contacts = jsonObj.getJSONArray(jsonStr);
//
//                        // looping through All Contacts
//                        for (int i = 0; i < contacts.length(); i++) {
//                            JSONObject c = contacts.getJSONObject(i);
//
//                            final_status[0] = c.getString("status");
//                            final_lat[0] = c.getString("lat");
//                            finalP_long[0] = c.getString("long");
//
//                            Log.d("data",""+final_status+" "+final_lat+" "+finalP_long+" ");
//
//
//
//                            // tmp hash map for single contact
////                            HashMap<String, String> contact = new HashMap<>();
////
////                            // adding each child node to HashMap key => value
////                            contact.put("id", id);
////                            contact.put("name", name);
////                            contact.put("email", email);
////                            contact.put("mobile", mobile);
////
////                            // adding contact to contact list
////                            contactList.add(contact);
//                        }
//                    } catch (final JSONException e) {
////                        Log.e(TAG, "Json parsing error: " + e.getMessage());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(),
//                                        "Json parsing error: " + e.getMessage(),
//                                        Toast.LENGTH_LONG)
//                                        .show();
//                            }
//                        });
//
//                    }
//                } else {
////                    Log.e(TAG, "Couldn't get json from server.");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),
//                                    "Couldn't get json from server. Check LogCat for possible errors!",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//
//                }


            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        checkGPS();
        getMyLocation();
        //  MarkerOptions marker = new MarkerOptions();
        // marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        // Add a marker in Sydney, Australia, and move the camera.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        /*mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                checkGPS();
                return true;
            }
        });*/

        getMyLocation();
        // LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //c_lat = current.getLatitude();
        //c_long = current.getLongitude();


        // c_lat=current.getLatitude();
        //c_long=current.getLongitude();

        //Toast.makeText(MapsActivity.this,c_lat +","+c_long, Toast.LENGTH_SHORT).show(); //c_lat +","+c_long


    }




    private void getMyLocation() {
        myplace=null;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location location=mMap.getMyLocation();
        if (location != null) {
            c_lat = location.getLatitude();
            c_long = location.getLongitude();
            myplace = new LatLng(c_lat, c_long);
            Toast.makeText(this, myplace.latitude + "," + myplace.longitude, Toast.LENGTH_SHORT).show();
        }
    }


    private void onNetwork() {

        ConnectivityManager conn= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=conn.getActiveNetworkInfo();
        if(ni!=null){
            Toast.makeText(this, "Connected To Internet", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Make Sure You are Connected To Interenet", Toast.LENGTH_SHORT).show();
        }
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        MarkerOptions BIN1=new MarkerOptions();
        MarkerOptions BIN2=new MarkerOptions();
        MarkerOptions BIN3=new MarkerOptions();


        float zoom = (float) 16.0;
        CheckBox rc=(CheckBox)findViewById(R.id.red);
        CheckBox gc=(CheckBox)findViewById(R.id.green);
        CheckBox bc=(CheckBox)findViewById(R.id.blue);



        BIN1.position(place1)
                .title("VIT-ONLINE");
        if(bin_1<=30&&bin_1>0){
            BIN1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(bin_1+"% Filled");
        }
        else
        if(bin_1<=60&&bin_1>30){
            BIN1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(bin_1+"% Filled");
            destination=place1;
            i++;
        }
        else
        if(bin_1<=100&&bin_1>60){
            BIN1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .snippet(bin_1+"% Filled");
            if(bin_1>bin_2&&bin_1>bin_3){
                waypoints=place1;

            }
            else
            {
                destination=place1;
            }

            i++;
        }

        BIN2.position(place2)
                .title("Bin");
        if(bin_2<=30&&bin_2>=0){
            BIN2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(bin_2+"% Filled");
        }
        else
        if(bin_2<=60&&bin_2>30){
            BIN2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(bin_2+"% Filled");
            destination=place2;
            i++;

        }
        else
        if(bin_2<=100&&bin_2>60){
            BIN2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .snippet(bin_2+"% Filled");
            if(bin_2>bin_1&&bin_2>bin_3){
                waypoints=place2;
            }
            else
            {
                destination=place2;
            }
            i++;

        }

        BIN3.position(place3)
                .title("Bin3");
        if(bin_3<=30&&bin_3>=0){
            BIN3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(bin_3+"% Filled");
        }
        else
        if(bin_3<=60&&bin_3>30){
            BIN3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(bin_3+"% Filled");
            destination=place3;
            i++;
        }
        else
        if(bin_3<=100&&bin_3>60){
            BIN3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .snippet(bin_3+"% Filled");


            if(bin_3>bin_2&&bin_3>bin_1){
                waypoints=place3;

            }
            else
            {
                destination=place3;
            }
            i++;

        }
        // mMap.addMarker(new MarkerOptions()
        //       .position(place1)
        //     .title("Near Shashtri Nagar")
        //   .snippet("BIN_ID=1 45% Filled")
        // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.blue:if(checked){
                if(bin_1>=30&&bin_1<60){
                    mMap.addMarker(BIN1);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place1,zoom));
                    // destination=place1;
                }
                if(bin_2>=30&&bin_2<60){
                    mMap.addMarker(BIN2);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place2,zoom));
                    //destination=place2;
                }
                if(bin_3>=30&&bin_3<60){
                    mMap.addMarker(BIN3);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place3,zoom));
                    //    destination=place3;
                }

            }
            else
            {
                mMap.clear();
                rc.setChecked(false);
                bc.setChecked(false);
                gc.setChecked(false);
            }
                break;
            case R.id.green:

                if(checked) {
                    if(bin_1>=0&&bin_1<30){
                        mMap.addMarker(BIN1);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place1,zoom));
                    }
                    if(bin_2>=0&&bin_2<30){
                        mMap.addMarker(BIN2);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place2,zoom));
                    }
                    if(bin_3>=0&&bin_3<30) {
                        mMap.addMarker(BIN3);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place3, zoom));
                    }

                }
                else {
                    mMap.clear();
                    rc.setChecked(false);
                    bc.setChecked(false);
                    gc.setChecked(false);
                }
                break;

            case R.id.red:if(checked){
                if(bin_1>60){
                    mMap.addMarker(BIN1);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place1,zoom));
                    //  waypoints=place1;
                }
                if(bin_2>60){
                    mMap.addMarker(BIN2);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place2,zoom));
                    //waypoints=place2;
                }
                if(bin_3>60) {
                    mMap.addMarker(BIN3);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place3, zoom));
                    //waypoints=place3;
                }

            }
            else{
                mMap.clear();

                rc.setChecked(false);
                bc.setChecked(false);
                gc.setChecked(false);

            }
                break;
            // TODO: Veggie sandwich
        }
    }
    public void sendRequest() {
        getMyLocation();
        //origin=myplace;
        // destination=place1;
        //waypoints=place3;
        //etDestination.getText().toString();
       /* if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }*/

        try {

            new DirectionFinder(this, myplace, destination, waypoints,i).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

           /*originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            */PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure Want To Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}