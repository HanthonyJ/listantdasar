package com.smartneasy.projek1;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity{

    ProgressDialog pDialog;
    Button btnregister, btnlogin;
    EditText textusernamer1, textpasswordr1, textconfirmpassword1;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = server.URL + "register.php";

    private static final String TAG = register.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_josn_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if(conMgr.getActiveNetworkInfo() !=null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()){
            }else{
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnregister = (Button) findViewById(R.id.btnregister);
        textusernamer1 = (EditText) findViewById(R.id.textusernamer1);
        textpasswordr1 = (EditText) findViewById(R.id.textpasswordr1);
        textconfirmpassword1 = (EditText) findViewById(R.id.textconfirmpassword1);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Auto-generated method stub
                intent = new Intent(register.this, login.class);
                finish();
                startActivity(intent);
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Auto-generated methode stub
                String username = textusernamer1.getText().toString();
                String password = textpasswordr1.getText().toString();
                String confirm_password = textconfirmpassword1.getText().toString();

                if(conMgr.getActiveNetworkInfo() !=null && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()){
                    checkRegister(username, password, confirm_password);
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkRegister(final String username, final String password, final String confirm_password){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    //check for error node in json
                    if (success == 1) {
                        Log.e("Successfully Register!", jObj.toString());
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE),
                                Toast.LENGTH_LONG).show();
                        textusernamer1.setText("");
                        textpasswordr1.setText("");
                        textconfirmpassword1.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.e(TAG,"Login Error: " + error.getMessage());
               Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
               hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("confirm_password", confirm_password);
                return params;
            }
        };
        //adding request to request queue
        AppController.getmInstance().addToRequestQueue(strReq, tag_josn_obj);
    }
    private void showDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }
    private void    hideDialog(){
        if(pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    public void onBackPressed(){
        intent = new Intent(register.this, login.class);
        finish();
        startActivity(intent);
    }
    }
