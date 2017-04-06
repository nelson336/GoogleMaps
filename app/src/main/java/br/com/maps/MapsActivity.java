package br.com.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ACCESS_MY_LOCATION_REQUEST_CODE = 5489;

    private SupportMapFragment mapFragment;
    private EditText etSearch;
    private Button btSearch;

    private GoogleMap mMap;
    private Button btChangeType;
    private Button btZoomIn;
    private Button btZoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initView();
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            //            LatLng sydney = new LatLng(-34, 151);
            //            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            accessMyLocation();
        }
    }

    private void accessMyLocation() {
        String[] permissionNotGranted = PermissionUtils.checkPermissionContext(MapsActivity.this, false, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionNotGranted.length > 0) {
            ActivityCompat.requestPermissions(MapsActivity.this, permissionNotGranted, ACCESS_MY_LOCATION_REQUEST_CODE);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); // add ponto em sua localização -- precisa das permissoes
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_MY_LOCATION_REQUEST_CODE:
                if (PermissionUtils.isPermissionsGrantedContext(MapsActivity.this, permissions)) {
                    accessMyLocation();
                } else {
                    Log.e("Permissão", "Negada");
                    Toast.makeText(MapsActivity.this, "Não aceitou a permissão", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        btSearch = (Button) findViewById(R.id.btSearch);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btChangeType = (Button) findViewById(R.id.btChangeType);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        btZoomIn = (Button) findViewById(R.id.btZoomIn);
        btZoomOut = (Button) findViewById(R.id.btZoomOut);
        btChangeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeType();
            }
        });

        btZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    zoomIn();
            }
        });

        btZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 zoomOut();
            }
        });
    }

    private void zoomIn() {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    private void zoomOut() {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private void changeType() {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void search() {
        String location = etSearch.getText().toString();
        if (!TextUtils.isEmpty(location)) {
            try {
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
//                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> addressList = geocoder.getFromLocationName(location, 1); // APENAS 1 -- PASSADO POR PARAMETRO
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); // animation camera --- location

                } else {
                    Log.e("AddressList", "Empty");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", e.toString());
            }

        }
    }
}
