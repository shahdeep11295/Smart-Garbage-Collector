package com.example.chauh.swms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class forgot_password extends AppCompatActivity {
EditText inputmobile,newpassword;
    Button loginbutton;
    Connection conn;
    String password;
    String mobileno;
    int mob;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputmobile= (EditText) findViewById(R.id.contactno);
        newpassword= (EditText) findViewById(R.id.newpassword);
        loginbutton= (Button) findViewById(R.id.login_button);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileno= inputmobile.getText().toString();

                password=newpassword.getText().toString();
                //mob= Integer.parseInt(mobileno);
             getBinValue getbinvalue=new getBinValue();
                getbinvalue.execute("");
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class Wrapper{
        public String id1;
        public String value1;
        public String la;
        public String lo;
    }
    public class getBinValue extends AsyncTask<String,String,Wrapper> {
        String z="";
        String value="";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(forgot_password.this, "Please wait.",
                    "Processing ....", true);

        }

        protected void onPostExecute(Wrapper w)
        {
           // Toast.makeText(forgot_password.this,"Password Updated Succesfully",Toast.LENGTH_LONG).show();
            //bin_value.setText(r.toString());
            //Toast.makeText(Home.this, r, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();


                Toast.makeText(forgot_password.this , "password update succesfully" , Toast.LENGTH_LONG).show();
                Intent i=new Intent(forgot_password.this,Signin.class);
                startActivity(i);
                finish();




        }
        @Override
        protected Wrapper doInBackground(String... params) {
            conn = connectionclass(DbConfig.connectionstring);
            z = "db connected";
            Wrapper w = null;
            if (conn == null) {
                z = "check internet connection";
            } else {
                String query = "UPDATE Collector_Info SET Password='"+password+"' WHERE Contact_No='"+mobileno+"'";
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                    stmt.executeUpdate(query);

                } catch (SQLException e) {
                    e.printStackTrace();

                }
                try {
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

}
