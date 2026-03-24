package com.example.sfweatherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    Button btnGet;
    TextView tvData,tvTemperature,tvPlace;
    String lat, lon;
    String api = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m&hourly=temperature_2m";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();
        initLocation();
        checkAndRequestLocationPermission();
        btnGet.setOnClickListener(v -> getData());
    }

    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                getLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied. Using default location.",
                        Toast.LENGTH_LONG).show();
                tvData.setText("Please grant location permission to get weather for your location.\n\nShowing default location data.");
            }
        }
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("API", response);
                        try {
                            // Parse the root JSON object
                            JSONObject jsonObject = new JSONObject(response);

                            // Get current temperature
                            JSONObject current = jsonObject.getJSONObject("current");
                            double currentTemp = current.getDouble("temperature_2m");

                            // Get the "hourly" object
                            JSONObject hourly = jsonObject.getJSONObject("hourly");

                            // Get the temperature array
                            org.json.JSONArray temperatureArray = hourly.getJSONArray("temperature_2m");

                            // Build a display string
                            StringBuilder sb = new StringBuilder();
                            sb.append("═══════════════════════\n");
                            sb.append("CURRENT TEMPERATURE\n");
                            sb.append("═══════════════════════\n");
                            sb.append(String.format("%.1f°C", currentTemp));
                            sb.append("\n\n");
                            sb.append("24-Hour Forecast:\n");
                            sb.append("───────────────────────\n");

                            int count = Math.min(temperatureArray.length(), 24);
                            for (int i = 0; i < count; i++) {
                                double temp = temperatureArray.getDouble(i);
                                sb.append(String.format("Hour %02d: %.1f°C\n", i, temp));
                            }

                            tvData.setText(sb.toString());
                            tvTemperature.setText(String.format("Temperature: %.1f°C", currentTemp));
                            tvPlace.setText("Location: " + lat + ", " + lon);
                            if (currentTemp > 30){
                                Drawable drawableRight = ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_wb_sunny_24);
                                tvTemperature.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
                            }else {
                                Drawable drawableRight = ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_wb_cloudy_24);
                                tvTemperature.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
                            }

                        } catch (JSONException e) {
                            Log.e("API", "JSON parsing error: " + e.getMessage());
                            tvData.setText(String.format("Error parsing data: %s", e.getMessage()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API", error.toString());
                tvData.setText(String.format("Error: %s", error.toString()));
            }
        });

        queue.add(stringRequest);
    }

    void initUI() {
        btnGet = findViewById(R.id.btnGet);
        tvData = findViewById(R.id.tvData);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvPlace = findViewById(R.id.tvPlace);
    }

    void initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Toast.makeText(MainActivity.this,
                                    "Location: " + location.getLatitude() + ", " + location.getLongitude(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("Location", "GOT:" + location.getLatitude() + "," + location.getLongitude());

                            lat = String.valueOf(location.getLatitude());
                            lon = String.valueOf(location.getLongitude());
                            api = "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
                                    "&longitude=" + lon + "&current=temperature_2m&hourly=temperature_2m";
                        } else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                            Toast.makeText(MainActivity.this,
                                    "Unable to get location. Using default.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Location", "NO LOCATION");
                        }
                    }
                });
    }
}