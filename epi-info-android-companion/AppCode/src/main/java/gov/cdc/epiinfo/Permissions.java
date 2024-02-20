package gov.cdc.epiinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions extends AppCompatActivity {

    public static final int READ_MEDIA_IMAGES = 1;
    public static final int READ_MEDIA_VIDEO = 2;
    public static final int READ_MEDIA_AUDIO = 3;
    public static final int GPS = 4;
    public static final int CAMERA = 5;
    public static final int RECORD_AUDIO = 6;

    private int CurrentPermission;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CurrentPermission = getIntent().getExtras().getInt("PermissionType");

        setContentView(R.layout.permissions);
        Button btnSet = findViewById(R.id.btnSet);
        TextView lblMessage = findViewById(R.id.lblMessage);
        TextView lblTitle = findViewById(R.id.lblTitle);

        switch (CurrentPermission)
        {
            case READ_MEDIA_IMAGES:
            case READ_MEDIA_VIDEO:
            case READ_MEDIA_AUDIO:
                lblTitle.setText(getString(R.string.request_storage));
                lblMessage.setText(getString(R.string.storage_desc));
                break;
            case GPS:
                lblTitle.setText(getString(R.string.request_location));
                lblMessage.setText(getString(R.string.location_desc));
                break;
            case RECORD_AUDIO:
                lblTitle.setText(getString(R.string.request_mic));
                lblMessage.setText(getString(R.string.mic_desc));
                break;
            case CAMERA:
                lblTitle.setText(getString(R.string.request_camera));
                lblMessage.setText(getString(R.string.camera_desc));
                break;
        }

        btnSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (CurrentPermission)
                {
                    case READ_MEDIA_IMAGES:
                    case READ_MEDIA_VIDEO:
                    case READ_MEDIA_AUDIO:
                        checkStoragePermissions();
                        break;
                    case GPS:
                        checkGPSPermissions();
                        break;
                    case RECORD_AUDIO:
                        checkAudioPermissions();
                        break;
                    case CAMERA:
                        checkCameraPermissions();
                        break;
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_MEDIA_IMAGES:
            case READ_MEDIA_VIDEO:
            case READ_MEDIA_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent p = new Intent(this, Permissions.class);
                    p.putExtra("PermissionType", Permissions.GPS);
                    startActivity(p);
                    this.finish();
                } else {
                    this.finish();
                }
                break;
            case GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent p = new Intent(this, Permissions.class);
                    p.putExtra("PermissionType", Permissions.CAMERA);
                    startActivity(p);
                    this.finish();
                } else {
                    this.finish();
                }
                break;
            case CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent p = new Intent(this, Permissions.class);
                    p.putExtra("PermissionType", Permissions.RECORD_AUDIO);
                    startActivity(p);
                    this.finish();
                } else {
                    this.finish();
                }
                break;
            case RECORD_AUDIO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.finish();
                } else {
                    this.finish();
                }
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                    READ_MEDIA_IMAGES);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_VIDEO},
                    READ_MEDIA_VIDEO);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_VIDEO},
                    READ_MEDIA_AUDIO);

        } else {
            Intent permissions = new Intent(this, Permissions.class);
            permissions.putExtra("PermissionType",Permissions.GPS);
            startActivity(permissions);
            this.finish();
        }
    }

    private void checkGPSPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS);


        } else {
            Intent permissions = new Intent(this, Permissions.class);
            permissions.putExtra("PermissionType",Permissions.CAMERA);
            startActivity(permissions);
            this.finish();
        }
    }

    private void checkAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);


        } else {
            this.finish();
        }
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA);


        } else {
            Intent permissions = new Intent(this, Permissions.class);
            permissions.putExtra("PermissionType",Permissions.RECORD_AUDIO);
            startActivity(permissions);
            this.finish();
        }
    }

}
