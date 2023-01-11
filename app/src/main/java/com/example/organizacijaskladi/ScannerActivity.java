package com.example.organizacijaskladi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity {
    private Button scan;
    private Button finish;
    private TextView res;
    private RequestQueue requestQueue;
    private String url="https://warehouse-organizer-is.azurewebsites.net/api/v1/Article";
    private String url1="https://warehouse-organizer-is.azurewebsites.net/api/v1/Order";
    private String url2="https://warehouse-organizer-is.azurewebsites.net/api/v1/Warehouse";
    private ArrayList<String> barcodes ;
    private ArrayList<String>whouses;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodes=new ArrayList<>();
        whouses=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        setContentView(R.layout.activity_scanner);
        res = findViewById(R.id.scanRes);
        scan= findViewById(R.id.scaned);
        finish = findViewById(R.id.finish);
        showWhouses();
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }
    public void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up for flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);
    }
    public void showWhouses(){

        JsonArrayRequest request = new JsonArrayRequest(url2,jsonArrayListenerWarehouses,errorListener);
        requestQueue.add(request);

    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("FILES_TO_SEND", barcodes);
        switchActivityIntent.putExtra("FILES_TO_SEND2", whouses);
        startActivity(switchActivityIntent);
    }
    ActivityResultLauncher<ScanOptions>barlauncher = registerForActivityResult(new ScanContract(),result -> {
       if(result.getContents()!=null){
           AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
           builder.setTitle("result");

           char[] ch = result.getContents().toCharArray();
           System.out.println(ch.length);
           String test = "";
           for (int i = 0; i<ch.length-1;i++){
               test+=ch[i]+"";
           }
           int i = Integer.parseInt(test);
           barcodes.add(result.getContents());
           res.setText(result.getContents());
           builder.setMessage(result.getContents());
           builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
               }
           }).show();
       }
    });
    private Response.Listener<JSONArray>jsonArrayListenerWarehouses = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();

            for(int i = 0; i<response.length(); i++){
                try {
                    JSONObject object = response.getJSONObject(i);
                    String code = object.getString("warehouseCode");
                    whouses.add(code);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error",error.getMessage());
        }
    };
}