package com.smartneasy.projek1;

import android.app.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.icu.util.IslamicCalendar;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnlogout, GetImageFromGalleryButton, UploadImageOnServerButton, btdatepicker;
    ImageView ShowSelectedImage;
    TextView text1, textusername;
    EditText imagename, nim, nohp,tglresult;
    String id,username;
    SharedPreferences sharedPreferences;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    Bitmap FixBirmap;

    String ImageTag = "image_tag";
    String ImageName = "image_data";
    String Nim = "nim";
    String NoHp = "nohp";
    String Tanggal = "tanggal";
    ProgressDialog progressDialog;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String GetImageNameFromEditText;
    String GetNimFromEditText;
    String GetNoHpFromEditText;
    String GetTanggalEditText;
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY =1, CAMERA = 2;

    public static final String TAG_ID ="id";
    public static final String TAG_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetImageFromGalleryButton = (Button)findViewById(R.id.btnselectgambar);
        UploadImageOnServerButton = (Button)findViewById(R.id.btupload);
        ShowSelectedImage = (ImageView)findViewById(R.id.imageview);
        imagename = (EditText)findViewById(R.id.imagename);
        byteArrayOutputStream = new ByteArrayOutputStream();
        nim = (EditText)findViewById(R.id.nim);
        nohp = (EditText)findViewById(R.id.nohp);
        dateFormatter = new SimpleDateFormat("dd-MM-YYYY", Locale.US);
        tglresult = (EditText)findViewById(R.id.tglresult);
        btdatepicker = (Button)findViewById(R.id.btdatepicker);
        text1 = (TextView) findViewById(R.id.text1);
        textusername = (TextView)findViewById(R.id.textusername);
        btnlogout = (Button)findViewById(R.id.btnlogout);
        sharedPreferences = getSharedPreferences(login.my_shared_preference, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);

        text1.setText("ID : " + id);
        textusername.setText("USERNAME : " + username);

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showPictureDialaog();
            }
        });

        btdatepicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDateDialog();
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Auto-generate methode stube
                //update login session ke false dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, login.class);
                finish();
                startActivity(intent);
            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                GetImageNameFromEditText = imagename.getText().toString();
                GetNimFromEditText = nim.getText().toString();
                GetNoHpFromEditText = nohp.getText().toString();
                GetTanggalEditText = tglresult.getText().toString();
                UploadImageToServer();
            }

        });
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.CAMERA},5);
            }
        }
    }
    private void showDateDialog(){
        /**calender untuk mendapatkan tanggal sekarang
         */
        Calendar newCalender = Calendar.getInstance();
        /**initiate datepicker*/
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            /**methode ini dipanggil saat kita selesai memilih tanggal di datepicker*/
            /**set calender untuk menampung tanggal yg dipilih */
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            /**update textview dengan tanggal yg kita pilih */
            tglresult.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));
        /**TAMPILKAN DATEPICKER DIALOG */
        datePickerDialog.show();
    }
    private void showPictureDialaog(){
        AlertDialog.Builder pictureDialaog = new AlertDialog.Builder(this);
        pictureDialaog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Camera"
        };
        pictureDialaog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0:
                    takePhotoFromCamera();
                    break;
                }
            }
        });
        pictureDialaog.show();
    }
    private void takePhotoFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED){
            return;
        }
        if (requestCode == CAMERA){
            FixBirmap = (Bitmap)data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBirmap);

            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //saveimage(thumbnail);
            //Toast.makeText(ShadiregistrationParts5.this, "image save", Toast.LENGTH_SHORT).show();
        }
    }
    public void UploadImageToServer(){
        FixBirmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this, "Data Dikirim",
                        "Please Wait", false, false);
            }
            @Override
            protected void  onPostExecute(String string1){
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,string1,Toast.LENGTH_LONG).show();
                nim.setText("");
                nohp.setText("");
                imagename.setText("");
                tglresult.setText("");
                ShowSelectedImage.setImageBitmap(null);
            }
            @Override
            protected String doInBackground(Void...params){
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageTag, GetImageNameFromEditText);
                HashMapParams.put(Nim, GetNimFromEditText);
                HashMapParams.put(NoHp, GetNoHpFromEditText);
                HashMapParams.put(Tanggal, GetTanggalEditText);
                HashMapParams.put(ImageName, GetImageNameFromEditText);

                String FinalData = imageProcessClass.ImageHttpRequest("http://192.168.8.101/projekandroid1/uploadimagetoserver.php", HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }
    public class ImageProcessClass{
        public String ImageHttpRequest(String requestURL, HashMap<String, String>PData){
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                RC = httpURLConnection.getResponseCode();
                if (RC == HttpURLConnection.HTTP_OK) {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException{
            stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()){
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilder.toString();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[]grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //now user should be able to camera
            }
            else {
                Toast.makeText(MainActivity.this, "Unable to use camera..please Allow us to use camera",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
