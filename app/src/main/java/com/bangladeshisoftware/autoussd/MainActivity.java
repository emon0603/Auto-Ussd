package com.bangladeshisoftware.autoussd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1;

    private EditText ussdEditText;
    private Button callUssdBtn,acceb_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ussdEditText = findViewById(R.id.ussdEditText);
        callUssdBtn = findViewById(R.id.callUssdBtn);
        acceb_bt = findViewById(R.id.acceb_bt);

        acceb_bt.setOnClickListener(view->{
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);

        });

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE_CALL);
            }
        } else {
            // Permission has already been granted
            callUssdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runUssd();
                    Intent intent = new Intent(MainActivity.this, UssdService.class);
                    startService(intent);
                    Log.d("click", "Okay Clicked");
                }
            });
        }
    }

    private void runUssd(){
        String ussdCode = "*121#";
        //String ussdCode = ussdEditText.getText().toString();



        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(ussdToCallableUri(ussdCode));
        try{
            startActivity(intent);
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private Uri ussdToCallableUri(String ussd) {
        String uriString = "";
        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {
            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    callUssdBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            runUssd();
                        }
                    });
                } else {
                }
                return;
            }

        }
    }

}


