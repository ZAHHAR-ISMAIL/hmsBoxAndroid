package com.test.hmsboxandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapViewDemoActivity";
    // Huawei map.
    private HuaweiMap hMap;

    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain a MapView instance.
        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        // Please replace Your API key with the API key in
        // agconnect-services.json.
        MapsInitializer.setApiKey("DAEDAH0L/YSRo801L4lf9Bx6Gh0AzEcBpQT8BLxcEgA0ifsJaFOIWFpinfCSjlRzRrzh/dlnSDsqesSmQkFhVEif4m0OEkGAToVGog==");
        mMapView.onCreate(mapViewBundle);
        // Obtain a map instance.
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        // Obtain a map instance from callback.
        Log.d(TAG, "onMapReady: ");
        hMap = huaweiMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}