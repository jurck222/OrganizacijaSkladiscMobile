package com.example.organizacijaskladi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RequestQueue requestQueue;
    private Button send;
    private Button cancle;
    private Spinner sklad;
    private TextView orderCode;
    private TextView codeA;
    private TextView name;
    private EditText num;
    private EditText z;
    private String zone;
    private String url="https://warehouse-organizer-is.azurewebsites.net/api/v1/Article";
    private String url1="https://warehouse-organizer-is.azurewebsites.net/api/v1/Order";
    private String url2="https://warehouse-organizer-is.azurewebsites.net/api/v1/Warehouse";
    public ArrayList<String> filelist = new ArrayList<>();
    public ArrayList<String> whouses = new ArrayList<>() ;
    public String orderCodes="";
    public String desc="";
    public String skladisce="";
    public int quantity=0;
    public int code=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue=Volley.newRequestQueue(getApplicationContext());
        orderCode = findViewById(R.id.orderCode);
        codeA = findViewById(R.id.code);
        z=findViewById(R.id.zone);
        name = findViewById(R.id.name);
        num = findViewById(R.id.num);
        filelist =  (ArrayList<String>)getIntent().getSerializableExtra("FILES_TO_SEND");
        whouses =  (ArrayList<String>)getIntent().getSerializableExtra("FILES_TO_SEND2");
        showArticles();
        System.out.println(whouses.size());
        sklad = (Spinner) findViewById(R.id.skladisca);
        sklad.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, whouses);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sklad.setAdapter(dataAdapter);
        cancle = findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStuff();

            }
        });

    }
    public void showArticles(){

            JsonArrayRequest request = new JsonArrayRequest(url,jsonArrayListenerArticles,errorListener);
            requestQueue.add(request);

    }


    private Response.Listener<JSONArray>jsonArrayListenerArticles = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {

            orderCodes = filelist.get(0);
            orderCode.setText(orderCodes);

            for(int i = 0; i<response.length(); i++){
                try {
                    JSONObject object = response.getJSONObject(i);
                    code = object.getInt("code");
                    String text = filelist.get(1);
                    text.replace("\n", "");
                    if(code == Integer.parseInt(text)){
                        desc = object.getString("description");
                        quantity = object.getInt("quantity") * (filelist.size()-2);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            codeA.setText(String.valueOf(code));
            name.setText(desc);

            //articles.setText(orderCode+" "+code+" "+desc+" "+quantity);
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error",error.getMessage());
        }
    };
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, ScannerActivity.class);

        startActivity(switchActivityIntent);
    }
    private void getStuff(){
        quantity+=Integer.parseInt(num.getText().toString());
        zone=z.getText().toString();
        addArticle();
        switchActivities();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item = adapterView.getItemAtPosition(i).toString();
        System.out.println(item);
        skladisce=item;
        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void addArticle() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = url1;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderCode",orderCodes);
            jsonBody.put("articleCode", code);
            jsonBody.put("quantity", quantity);
            jsonBody.put("warehouseName", skladisce);
            jsonBody.put("whouseZone",zone);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}