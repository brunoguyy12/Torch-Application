package com.example.practical7;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnToggleTorch; // Use Button instead of ToggleButton
    private ToggleButton btnTorchState;
    private static final int CAMERA_REQUEST = 1; // Request code for permission
    private boolean isTorchOn = false; // Track flashlight state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggleTorch = findViewById(R.id.toggleButton); // Reference the Button
        btnTorchState = findViewById(R.id.torchStateButton); // Reference the new ToggleButton

        // Check permission in onCreate (optional)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }

        btnToggleTorch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        toggleTorch();
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, toggle torch on click
            } else {
                Toast.makeText(this, "Camera permission is required to use the flashlight", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleTorch() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            isTorchOn = !isTorchOn; // Toggle state
            cameraManager.setTorchMode(cameraId, isTorchOn);
            String message = isTorchOn ? "Torch ON" : "Torch OFF";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Update ToggleButton state
            btnTorchState.setChecked(isTorchOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error toggling flashlight", Toast.LENGTH_SHORT).show();
        }
    }
}