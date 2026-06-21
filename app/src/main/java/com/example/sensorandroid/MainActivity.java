package com.example.sensorandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private ImageView ivPhoto;
    private MaterialButton btnCapturePhoto;
    private MaterialButton btnGetLocation;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvProvider;
    private TextView tvGpsStatus;
    private TextView tvTemperature;

    // Location Services
    private FusedLocationProviderClient fusedLocationClient;

    // Sensor Manager for Temperature
    private SensorManager sensorManager;
    private Sensor tempSensor;

    // Activity Result Launchers
    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets dynamically for layout padding adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI component bindings
        ivPhoto = findViewById(R.id.ivPhoto);
        btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvProvider = findViewById(R.id.tvProvider);
        tvGpsStatus = findViewById(R.id.tvGpsStatus);
        tvTemperature = findViewById(R.id.tvTemperature);

        // Initialize Google Fused Location API
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and register temperature sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        if (tempSensor == null) {
            tvTemperature.setText("Sensor suhu tidak tersedia pada perangkat ini");
        }

        // Camera Result contract callback configuration
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        ivPhoto.setImageBitmap(bitmap);
                        Toast.makeText(MainActivity.this, "Foto berhasil diambil", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Batal mengambil foto", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Multiple Permission Launcher callback configuration
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean camVal = result.get(Manifest.permission.CAMERA);
                    boolean cameraGranted = camVal != null && camVal;

                    Boolean fineVal = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    boolean fineLocationGranted = fineVal != null && fineVal;

                    Boolean coarseVal = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    boolean coarseLocationGranted = coarseVal != null && coarseVal;

                    if (cameraGranted) {
                        takePhoto();
                    } else if (result.containsKey(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Izin Kamera ditolak. Tidak dapat mengambil foto.", Toast.LENGTH_SHORT).show();
                    }

                    if (fineLocationGranted || coarseLocationGranted) {
                        fetchLocation();
                    } else if (result.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) || result.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(this, "Izin Lokasi ditolak. Tidak dapat memperbarui lokasi.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Hook click listeners
        btnCapturePhoto.setOnClickListener(v -> checkCameraPermissionAndTakePhoto());
        btnGetLocation.setOnClickListener(v -> checkLocationPermissionAndGetLocation());

        // Update GPS Status text on create
        updateGpsStatusText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listener when page is visible
        if (tempSensor != null && sensorManager != null) {
            sensorManager.registerListener(tempListener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        updateGpsStatusText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister to conserve battery power
        if (sensorManager != null && tempSensor != null) {
            sensorManager.unregisterListener(tempListener);
        }
    }

    // Camera Handlers
    private void checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            requestPermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void takePhoto() {
        takePictureLauncher.launch(null);
    }

    // GPS / Location Handlers
    private void checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tvLatitude.setText("Latitude: Mengambil...");
        tvLongitude.setText("Longitude: Mengambil...");
        tvProvider.setText("Provider: Mengambil...");
        updateGpsStatusText();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        updateGpsStatusText();
                        if (location != null) {
                            tvLatitude.setText("Latitude: " + location.getLatitude());
                            tvLongitude.setText("Longitude: " + location.getLongitude());
                            tvProvider.setText("Provider: " + location.getProvider());
                            Toast.makeText(MainActivity.this, "Lokasi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        } else {
                            // If last location is null (common in emulators), request fresh update
                            requestNewLocation();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateGpsStatusText();
                        tvLatitude.setText("Latitude: Gagal");
                        tvLongitude.setText("Longitude: Gagal");
                        tvProvider.setText("Provider: Gagal");
                        Toast.makeText(MainActivity.this, "Gagal mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                updateGpsStatusText();
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    tvLatitude.setText("Latitude: " + location.getLatitude());
                    tvLongitude.setText("Longitude: " + location.getLongitude());
                    tvProvider.setText("Provider: " + location.getProvider());
                    Toast.makeText(MainActivity.this, "Lokasi baru berhasil didapatkan", Toast.LENGTH_SHORT).show();
                } else {
                    tvLatitude.setText("Latitude: Tidak tersedia");
                    tvLongitude.setText("Longitude: Tidak tersedia");
                    tvProvider.setText("Provider: Tidak tersedia");
                    Toast.makeText(MainActivity.this, "Lokasi tidak dapat ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void updateGpsStatusText() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = false;
        if (locationManager != null) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        String status = isGpsEnabled ? "Aktif (GPS Enabled)" : "Tidak Aktif (GPS Disabled)";
        tvGpsStatus.setText("Status GPS: " + status);
    }

    // Realtime Ambient Temperature Sensor Listener
    private final SensorEventListener tempListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                float tempVal = event.values[0];
                tvTemperature.setText(String.format("Suhu: %.2f °C", tempVal));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used
        }
    };
}