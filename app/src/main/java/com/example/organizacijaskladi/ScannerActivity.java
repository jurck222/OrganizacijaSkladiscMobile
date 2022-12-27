package com.example.organizacijaskladi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerActivity extends AppCompatActivity {
    private Button scan;
    private TextView res;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        res = findViewById(R.id.scanRes);
        scan= findViewById(R.id.scaned);
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
    ActivityResultLauncher<ScanOptions>barlauncher = registerForActivityResult(new ScanContract(),result -> {
       if(result.getContents()!=null){
           AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
           builder.setTitle("result");
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
}