package com.example.organizacijaskladi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private TextView articles;
    private String url="https://warehouse-organizer-is.azurewebsites.net/api/v1/Article";
    private String url1="https://warehouse-organizer-is.azurewebsites.net/api/v1/Order";
    private String url2="https://warehouse-organizer-is.azurewebsites.net/api/v1/Warehouse";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue=Volley.newRequestQueue(getApplicationContext());
        articles = findViewById(R.id.Articles);
    }
    public void showArticles(View view){
        if(view != null){
            JsonArrayRequest request = new JsonArrayRequest(url,jsonArrayListenerArticles,errorListener);
            requestQueue.add(request);
        }
    }

    public void showWhouses(View view){
        if(view != null){
            JsonArrayRequest request = new JsonArrayRequest(url2,jsonArrayListenerWarehouses,errorListener);
            requestQueue.add(request);
        }
    }
    private Response.Listener<JSONArray>jsonArrayListenerArticles = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();
            for(int i = 0; i<response.length(); i++){
                try {
                    JSONObject object = response.getJSONObject(i);
                    int code = object.getInt("code");
                    String desc = object.getString("description");
                    int quantity = object.getInt("quantity");
                    data.add(code+" "+desc+" "+quantity);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            for(String row:data){
                String currTxt = articles.getText().toString();
                articles.setText(currTxt+"\n\n"+row);
            }
        }
    };
    private Response.Listener<JSONArray>jsonArrayListenerWarehouses = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();
            for(int i = 0; i<response.length(); i++){
                try {
                    JSONObject object = response.getJSONObject(i);
                    String code = object.getString("warehouseCode");
                    String desc = object.getString("zone");

                    data.add(code+" "+desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
        }
            for(String row:data){
                String currTxt = articles.getText().toString();
                articles.setText(currTxt+"\n\n"+row);
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