package com.example.chauh.swms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import static android.view.Window.FEATURE_NO_TITLE;

public class Signin extends AppCompatActivity {
    public static final String PREFS_NAME="LoginPrefs";
    Button login,forgotpass;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public    EditText User;
    EditText Pass;
    ProgressDialog progressDialog;
    Connection conn;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);

        setContentView(R.layout.activity_signin);
        SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
        if(settings.getString("logged","").toString().equals("logged"))
        {
            Intent intent=new Intent(Signin.this,Main2Activity.class);
            startActivity(intent);
            finish();
        }

        login=(Button)findViewById(R.id.login_button);
        User=(EditText)findViewById(R.id.uname);
        Pass=(EditText)findViewById(R.id.pass);
        forgotpass= (Button) findViewById(R.id.forgotpassword_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid=User.getText().toString();
                String password=Pass.getText().toString();

                if(userid.equals("")||password.equals(""))
                {
                    Snackbar.make(view, "Make Sure You Entered All Details", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
                else
                { Snackbar.make(view, "Login In Progress...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                    CheckLogin checkLogin = new CheckLogin();
//                    checkLogin.execute("");

                     userid=User.getText().toString();
                     password=Pass.getText().toString();
                    AsyncLogin login =new AsyncLogin();
                    login.execute(userid,password);
                     }


               // Intent i=new Intent(getApplicationContext(),Login.class);
                //startActivity(i);
                //finish();
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),forgot_password.class);
                startActivity(i);
                finish();
            }
        });
        onNetwork();
    }
    private void onNetwork() {

        ConnectivityManager conn= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=conn.getActiveNetworkInfo();
        if(ni!=null){
            //Toast.makeText(this, "Connected To Internet", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LayoutInflater l=getLayoutInflater();
            view=l.inflate(R.layout.no_internet_toast,(ViewGroup)findViewById(R.id.no_interenet_layout));
            Toast t1=Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
            t1.setGravity(Gravity.CENTER,0,0);
            t1.setView(view);
            t1.show();
        }
    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Signin.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
//            pdLoading.setMessage("\tLoading...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();

            progressDialog = ProgressDialog.show(Signin.this, "Please wait.",
                    "Login in Progress...", true);

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://smart11295.000webhostapp.com/login.inc.php");///change url here
//                url = new URL("http://indoorproject.16mb.com/login.inc.php");///change url here

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
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
                    return(result.toString());

                }else{

                    return("unsuccessful");
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

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Intent intent = new Intent(Signin.this,Main2Activity.class);
                startActivity(intent);
                Signin.this.finish();
//                onNetwork();


            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Signin.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Signin.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
//
//    public class CheckLogin extends AsyncTask<String,String,String> {
//        String z = "";
//        Boolean isSuccess = false;
//        @Override
//        protected void onPreExecute()
//        {
//            progressDialog = ProgressDialog.show(Signin.this, "Please wait.",
//                    "Login in Progress...", true);
//        }
//        @Override
//        protected void onPostExecute(String r)
//        {
//onNetwork();
//
//            if(isSuccess)
//            {
//                Toast.makeText(getApplicationContext() , "Login Successfull" , Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//
//
//                //finish();
//            }
//            else{
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        @Override
//        protected String doInBackground(String... params) {
//            String userid=null;//User.getText().toString();
//            String password=null;//Pass.getText().toString();
//            if(userid.trim().equals("")||password.trim().equals("")){
//
//
//               // z="enter userid and password";
//            }
//            else{
//                try{
//                    conn=connectionclass(DbConfig.connectionstring);
//                    //z="db connected";
//                    if(conn==null){
//                        //z="check internet connection";
//
//                    }else
//                    {
//                        String query="select * from Collector_Info where Username= '" + userid.toString() + "' and Password = '"+ password.toString() +"'  ";
//                        Statement stmt = conn.createStatement();
//                        ResultSet rs = stmt.executeQuery(query);
//                        if(rs.next())
//                        {
//                            z = "Login successful";
//                            isSuccess=true;
//                            conn.close();
//                            Intent i=new Intent(getApplicationContext(),Main2Activity.class);
//                            i.putExtra("user",userid.toString());
//                            SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
//                            SharedPreferences.Editor editor=settings.edit();
//                            editor.putString("logged","logged");
//                            editor.commit();
//                            startActivity(i);
//                            finish();
//                        }
//                        else
//                        {
//                            z = "Invalid Credentials!";
//                            isSuccess = false;
//                        }
//
//
//                    }
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            return z;
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

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }
}
