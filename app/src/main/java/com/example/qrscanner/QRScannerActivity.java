package com.example.qrscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    @SuppressLint({"MissingPermission", "MissingSuperCall"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with scanning
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleResult(Result rawResult) {
        // Handle the result (e.g., display, vibrate, open links)
        Toast.makeText(this, "Scanned: " + rawResult.getText(), Toast.LENGTH_LONG).show();

        // Vibrate to indicate a successful scan
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500);
        }

        // Here you can perform actions based on the scanned content, like opening a link
        String scannedContent = rawResult.getText();
        if (scannedContent.startsWith("http://") || scannedContent.startsWith("https://")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedContent));
            startActivity(browserIntent);
        }

        // Resume scanning after a short delay
        new Handler().postDelayed(() -> scannerView.resumeCameraPreview(QRScannerActivity.this), 2000);
        scannerView.resumeCameraPreview(this);
    }

    // Implement other necessary methods like onResume, onPause, onRequestPermissionsResult, etc.
    // Make sure to properly handle the Android lifecycle and permissions.
}
