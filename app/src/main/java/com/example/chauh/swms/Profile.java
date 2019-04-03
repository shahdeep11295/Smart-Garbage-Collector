package com.example.chauh.swms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Profile extends AppCompatActivity {
    TextView name,contactno,city,zone,licenceno;
    Intent i;
    String uname,s_city,s_cont,s_zone,s_lic;
Connection conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name= (TextView) findViewById(R.id.displayname);
        contactno= (TextView) findViewById(R.id.displaycontact);
        city= (TextView) findViewById(R.id.displaycity);
        zone= (TextView) findViewById(R.id.displayzone);
        licenceno= (TextView) findViewById(R.id.displaylicence);

        i=getIntent();
        uname=i.getStringExtra("user");
        CheckLogin checkLogin=new CheckLogin();
        checkLogin.execute("");


    }
    public class Wrapper1{
        public String uname;
        public String contact;
        public String city;
        public String zone;
        public String licence;
    }



    public class CheckLogin extends AsyncTask<String,String,Wrapper1> {
        String z = "";
        Boolean isSuccess = false;

        protected void onPostExecute(Wrapper1 w)
        {
            name.setText(w.uname);
            contactno.setText(w.contact);
            city.setText(w.city);
            zone.setText(w.zone);
            licenceno.setText(w.licence);

            //bin_value.setText(r.toString());
            //Toast.makeText(Home.this, r, Toast.LENGTH_SHORT).show();

        }


        @Override
        protected Wrapper1 doInBackground(String... params) {


                    conn = connectionclass(DbConfig.connectionstring);
                    Wrapper1 w = null;
                    z = "db connected";
                    if (conn == null) {
                        z = "check internet connection";
                    } else {
                        String query = "select * from Collector_Info where Username='" + uname + "'";
                        Statement stmt = null;
                        try {
                            stmt = conn.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            ResultSet rs = stmt.executeQuery(query);
                            while (rs.next()) {
                                String name = rs.getString("Username");
                                String cont = rs.getString("Contact_No");
                                String city = rs.getString("City");
                                String Zone = rs.getString("Zone");
                                String licence = rs.getString("Licence");
                                w = new Wrapper1();
                                w.uname = name;
                                w.contact = cont;
                                w.city = city;
                                w.zone = Zone;
                                w.licence = licence;

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

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }
}
