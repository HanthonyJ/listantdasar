package com.smartneasy.projek1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class login extends AppCompatActivity{
    ProgressDialog pDialog;
    Button btnregister, btnlogin;
    EditText textusername, textpassword;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = server.URL + "login.php";

    private static final String TAG = login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    Boolean session = false;
    String id,username;

    public static final String my_shared_preference = "my shared preference";
    public static final String session_status = "session status";

   @Override
    protected void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        setContentView(R.layout.login);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if(conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isAvailable()){
            }else{
                Toast.makeText(getApplicationContext(),"No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        btnlogin = (Button)findViewById(R.id.btnlogin);
        btnregister = (Button)findViewById(R.id.btnregister);
        textusername = (EditText)findViewById(R.id.textusername);
        textpassword = (EditText)findViewById(R.id.textpassword);
        //cek session login jika true maka akan langsung buka mainactivity
       sharedPreferences = getSharedPreferences(my_shared_preference, Context.MODE_PRIVATE);
       session = sharedPreferences.getBoolean(session_status, false);
       id = sharedPreferences.getString(TAG_ID, null);
       username = sharedPreferences.getString(TAG_USERNAME,null);

       if(session){
           Intent intent = new Intent(login.this, MainActivity.class);
           intent.putExtra(TAG_ID, id);
           intent.putExtra(TAG_USERNAME, username);
           finish();
           startActivity(intent);
       }
       btnlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //todo auto-generated method stub
               String username = textusername.getText().toString();
               String password = textpassword.getText().toString();
               //mengecek kolom yang kosong
               if (username.trim().length() > 0 && password.trim().length() > 0) {
                   if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                           && conMgr.getActiveNetworkInfo().isConnected()) {
                       checklogin(username, password);
                   } else {
                       Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_LONG).show();
                   }
               } else {
                   //promt user to enter credentials
                   Toast.makeText(getApplicationContext(), "kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
               }
           }
       });
        btnregister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO Auto-generated methodstub
                intent = new Intent(login.this, register.class);
                finish();
                startActivity(intent);
            }
        });
    }
    private  void checklogin(final String username, final String password){
       pDialog = new ProgressDialog(this);
       pDialog.setCancelable(false);
       pDialog.setMessage("Logging in....");
       showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    //check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        Log.e("Success fully Login", jObj.toString());
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        //menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, username);
                        editor.commit();
                        //memanggil main activity
                        Intent intent = new Intent(login.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
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

                return params;
            }
        };
        //adding request to request queue
        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }
        private void showDialog(){
       if(!pDialog.isShowing())
           pDialog.show();
        }
        private void    hideDialog(){
       if(pDialog.isShowing())
           pDialog.dismiss();
        }
}
