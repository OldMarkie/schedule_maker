package com.mobdeve.s21.mco.schedule_maker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * The `MapLocationPickerActivity` class provides an interactive map interface
 * for users to select a location. It utilizes Google Maps to display the map
 * and allows users to tap on a location to set a marker. The selected location
 * and its corresponding address are then returned to the calling activity.
 */

public class MapLocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_picker);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button selectLocationButton = findViewById(R.id.select_location_button);
        selectLocationButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                String address = getAddressFromLatLng(selectedLocation);
                Intent intent = new Intent();
                intent.putExtra("selected_location", selectedLocation);
                intent.putExtra("selected_address", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Request the user's current location
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                updateMarkerWithAddress(selectedLocation);
            } else {
                Toast.makeText(MapLocationPickerActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Set default location
        LatLng defaultLocation = new LatLng(-34, 151);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Add marker when clicking on the map
        googleMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            updateMarkerWithAddress(latLng);
        });
    }

    // Method to update marker with address
    private void updateMarkerWithAddress(LatLng latLng) {
        // Clear the previous marker
        if (locationMarker != null) {
            locationMarker.remove();
        }

        // Get address from latitude and longitude
        String address = getAddressFromLatLng(latLng);
        locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }

    // Method to get address from latitude and longitude
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Returns the full address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }
}
