package com.example.chauh.swms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PREFS_NAME = "LoginPrefs";
    Intent i;
    Connection conn;
    FloatingActionButton call, fab;
    String uname;
    ProgressDialog progressDialog;
    int binValue;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        i = getIntent();
        uname = i.getStringExtra("user");


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getBinValue getbinvalue=new getBinValue();
//                getbinvalue.execute("");
//                getBINVALUE();
//                getJSON("http://indoorproject.16mb.com/binValue.php");
                getJSON("http://smart11295.000webhostapp.com/binValue.php");


            }
        });
        call = (FloatingActionButton) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:99911295"));
                startActivity(i);

                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public class Wrapper {
        public String id1;
        public String value1;
        public String la;
        public String lo;
    }

    private class getBinVluenew extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Main2Activity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
//            pdLoading.setMessage("\tLoading...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();

            progressDialog = ProgressDialog.show(Main2Activity.this, "Please wait.",
                    "Login in Progress...", true);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
//                url = new URL("http://indoorproject.16mb.com/login.inc.php");///change url here
                url = new URL("http://smart11295.000webhostapp.com/login.inc.php");///change url here

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("true")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                String status = null;
                String p_lat = null;
                String p_lon = null;

                Intent intent = new Intent(Main2Activity.this, MapsActivity.class);
                i.putExtra("status", status);
                i.putExtra("plat", p_lat);
                i.putExtra("plon", p_lon);
                startActivity(intent);
                Main2Activity.this.finish();
//                onNetwork();


            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
//                Toast.makeText(Main2Activity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Main2Activity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

    void getBINVALUE() {
        String result = null;
        InputStream is = null;

        String status = null;
        String p_lat = null;
        String p_long = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://indoorproject.16mb.com/binValue.php");//binValue URL
            HttpPost httppost = new HttpPost("http://smart11295.000webhostapp.com/binValue.php");//binValue URL
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            Log.e("log_tag", "connection success");
            //   Toast.makeText(getApplicationContext(), “pass”, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
            Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();

        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
                //  Toast.makeText(getApplicationContext(), “Input Reading pass”, Toast.LENGTH_SHORT).show();
            }
            is.close();

            result = sb.toString();
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Input reading fail", Toast.LENGTH_SHORT).show();

        }

        //parse json data
        try {
            JSONArray jArray = new JSONArray(result);
//            TableLayout tv = (TableLayout) findViewById(R.id.table);
//            tv.removeAllViewsInLayout();
            int flag = 1;
            for (int i = -1; i < jArray.length() - 1; i++) {
//                TableRow tr = new TableRow(MainActivity.this);
//                tr.setLayoutParams(new LayoutParams(
//                        LayoutParams.FILL_PARENT,
//                        LayoutParams.WRAP_CONTENT));
                if (flag == 1) {
//                    TextView b6 = new TextView(MainActivity.this);
//                    b6.setText(“Id”);
//                    b6.setTextColor(Color.BLUE);
//                    b6.setTextSize(15);
//                    tr.addView(b6);
//                    TextView b19 = new TextView(MainActivity.this);
//                    b19.setPadding(10, 0, 0, 0);
//                    b19.setTextSize(15);
//                    b19.setText(“Name”);
//                    b19.setTextColor(Color.BLUE);
//                    tr.addView(b19);
//                    TextView b29 = new TextView(MainActivity.this);
//                    b29.setPadding(10, 0, 0, 0);
//                    b29.setText(“Status”);
//                    b29.setTextColor(Color.BLUE);
//                    b29.setTextSize(15);
//                    tr.addView(b29);
//                    tv.addView(tr);
//                    final View vline = new View(MainActivity.this);
//                    vline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 2));
//                    vline.setBackgroundColor(Color.BLUE);
//                    tv.addView(vline);
                    flag = 0;
                } else {
                    JSONObject json_data = jArray.getJSONObject(i);
//                    Log.i(“log_tag”, ”id: “ + json_data.getInt(“Id”) + “, Username: “ + json_data.getString(“username”) + “, No: “ + json_data.getString(“comment”));
//                    TextView b = new TextView(MainActivity.this);
                    status = json_data.getString("Status");
                    p_lat = json_data.getString("Latitude");
                    p_long = json_data.getString("Longitude");

//                    b.setText(stime);
//                    b.setTextColor(Color.RED);
//                    b.setTextSize(15);
//                    tr.addView(b);
//                    TextView b1 = new TextView(MainActivity.this);
//                    b1.setPadding(10, 0, 0, 0);
//                    b1.setTextSize(15);
//                    String stime1 = json_data.getString(“username”);
//                    b1.setText(stime1);
//                    b1.setTextColor(Color.BLACK);
//                    tr.addView(b1);
//                    TextView b2 = new TextView(MainActivity.this);
//                    b2.setPadding(10, 0, 0, 0);
//                    String stime2 = json_data.getString(“comment”);
//                    b2.setText(stime2);
//                    b2.setTextColor(Color.BLACK);
//                    b2.setTextSize(15);
//                    tr.addView(b2);
//                    tv.addView(tr);
//                    final View vline1 = new View(MainActivity.this);
//                    vline1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
//                    vline1.setBackgroundColor(Color.WHITE);
//                    tv.addView(vline1);
                }
            }
        } catch (JSONException e) {
//            Log.e(“log_tag”, “Error parsing data“ + e.toString());
            Toast.makeText(getApplicationContext(), "onArray fail", Toast.LENGTH_SHORT).show();
        }


        Intent i = new Intent(Main2Activity.this, MapsActivity.class);
        i.putExtra("status", status);
        i.putExtra("plat", p_lat);
        i.putExtra("plon", p_long);
        startActivity(i);


    }

    //this method is actually fetching the json string
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
                        final_status[0] = obj.getString("Status");
                        final_lat[0] = obj.getString("Latitude");
                        finalP_long[0] = obj.getString("Longitude");
                    }
                } catch (Exception e) {

                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Main2Activity.this, MapsActivity.class);
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


    public class getBinValue extends AsyncTask<String, String, Wrapper> {
        String z = "";
        String value = "";
        Boolean isSuccess = false;

        protected void onPostExecute(Wrapper w) {
            String status = w.value1;
            String p_lat = w.la;
            String p_lon = w.lo;
            Intent i = new Intent(Main2Activity.this, MapsActivity.class);
            i.putExtra("status", status);
            i.putExtra("plat", p_lat);
            i.putExtra("plon", p_lon);
            startActivity(i);
            //bin_value.setText(r.toString());
            //Toast.makeText(Home.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                Toast.makeText(Main2Activity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                //finish();

            }
        }

        @Override
        protected Wrapper doInBackground(String... params) {
            conn = connectionclass(DbConfig.connectionstring);
            z = "db connected";
            Wrapper w = null;
            if (conn == null) {
                z = "chech internet connection";
            } else {
                String query = "select * from Bin_Status where Bin_id=1";
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String id = rs.getString("Bin_id");
                        value = rs.getString("Status");
                        String lat = rs.getString("Latitude");
                        String lon = rs.getString("Longitude");
                        w = new Wrapper();
                        w.id1 = id;
                        w.value1 = value;
                        w.la = lat;
                        w.lo = lon;
                    }
                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return w;
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(String connectionstring) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        try {
            Class.forName(DbConfig.driver);
            connection = DriverManager.getConnection(connectionstring);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure Want To Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            //Intent i=new Intent(Main2Activity.this,MapsActivity.class);
            //startActivity(i);
            // Handle the camera action
//            getBinValue getbinvalue = new getBinValue();
//            getbinvalue.execute("");
            getJSON("http://smart11295.000webhostapp.com/binValue.php");
        } else if (id == R.id.nav_dev) {
            i = new Intent(this, Developer.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            Intent i = new Intent(Main2Activity.this, Signin.class);
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.commit();
            startActivity(i);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(Main2Activity.this, Profile.class);
            i.putExtra("user", uname);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}




