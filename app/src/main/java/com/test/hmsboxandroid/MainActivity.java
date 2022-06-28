package com.test.hmsboxandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.adapter.internal.AvailableCode;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.SettingsClient;
//import com.huawei.locationsample6.LogInfoUtil;
//import com.huawei.locationsample6.R;
//import com.huawei.logger.LocationLog;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.huawei.hms.support.api.entity.core.CommonCode;


import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int score = 0;

    // One Button
    Button BSelectImage;

    // One Preview Image
    ImageView IVPreviewImage;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE_TEXT = 200;
    int SELECT_PICTURE_FACE = 300;

    // TODO: Define a variable for the Analytics Kit instance.
    HiAnalyticsInstance instance;

    private static final String TAG = "BVBMapViewDemoActivity";
    // Huawei map.
    private HuaweiMap hMap;

    private MapView mMapView;

    CallbackManager callbackManager;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //Location Kit
    LocationCallback mLocationCallback;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SettingsClient mSettingsClient;


    // Face Detection

//    private static final String TAG_FACE = "LocalFaceTransactor";
//
//    private MLFaceAnalyzer detector;
//    private boolean isOpenFeatures;
//    private Context mContext;
//
//    public MainActivity(MLFaceAnalyzer detector) {
//        this.detector = detector;
//    }
//
//
//    public void LocalFaceTransactor(Activity activity, MLFaceAnalyzerSetting options, final Context context, boolean isOpenFeatures,
//                                    TextView age_tv, TextView countdown_tv) {
//        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
//    }


    MLFaceAnalyzer analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();

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

        // TODO: Initialize Analytics Kit
// Enable Analytics Kit logging.
        HiAnalyticsTools.enableLog();
// Generate an Analytics Kit instance.
        instance = HiAnalytics.getInstance(this);


        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });





        // Location
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            // Dynamically apply for the android.permission.ACCESS_BACKGROUND_LOCATION permission in addition to the preceding permissions if the API level is higher than 28.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }

        findViewById(R.id.location_requestLocationUpdatesWithCallback).setOnClickListener(this::onClick);
        findViewById(R.id.location_removeLocationUpdatesWithCallback).setOnClickListener(this::onClick);
        //addLogFragment();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(5000);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                Log.i(TAG,
                                        "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                                + "," + location.getLatitude() + "," + location.getAccuracy());
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                    }
                }
            };
        }
    }



    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_FACE);
        //startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_TEXT);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE_TEXT) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
                        MLFrame frame = MLFrame.fromBitmap(bitmap);
                        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
                        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
                            @Override
                            public void onSuccess(MLText text) {
                                // Processing for successful recognition.
                                Log.d(TAG, "Text ML is : "+ text.toString());
                                Log.d(TAG, "StringValue ML is : "+ text.getStringValue());
                                Log.d(TAG, "Block ML is : "+ text.getBlocks());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                // Processing logic for recognition failure.
                                Log.d(TAG, "Errror ML is : "+ e.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode == SELECT_PICTURE_FACE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        MLFaceAnalyzer analyzerFace = MLAnalyzerFactory.getInstance().getFaceAnalyzer();
                        MLFrame frame = MLFrame.fromBitmap(bitmap);
                        Task<List<MLFace>> taskFace = analyzerFace.asyncAnalyseFrame(frame);
                        taskFace.addOnSuccessListener(new OnSuccessListener<List<MLFace>>() {
                            @Override
                            public void onSuccess(List<MLFace> faces) {
                                // Detection success.
                                Log.d(TAG, "Face ML is : "+ faces.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                // Detection failure.
                                Log.d(TAG, "Face ML error is : "+ e.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
        // Removed when the location update is no longer required.
        removeLocationUpdatesWithCallback();
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


    private void requestLocationUpdatesWithCallback() {
        Log.i(TAG, "requestLocationUpdatesWithCallback");
        //Log.getLogInfo(this);
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                    mSettingsClient.checkLocationSettings(locationSettingsRequest);

            locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
                Log.i(TAG, "check location settings success");
                mFusedLocationProviderClient
                        .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                        .addOnSuccessListener(aVoid -> {
                            Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());

                        });
            }).addOnFailureListener(e -> {
                Log.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                int errCode = ((ApiException) locationSettingsResponseTask.getException()).getStatusCode();
                if ((errCode == AvailableCode.USER_ALREADY_KNOWS_SERVICE_UNAVAILABLE) ||
                        (errCode == AvailableCode.CURRENT_SHOWING_SERVICE_UNAVAILABLE) ||
                        (errCode == CommonCode.ErrorCode.CLIENT_API_INVALID)) {

                    // There is no HMS Core APK on the phone, no need to handle
                    Log.e(TAG, " BVB NO HMS");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
            //Task<LocationSettingsResponse> Locationdd = LocationSettingsStatusCodes.===



        }
    }

    /**
     * Removed when the location update is no longer required.
     */
    private void removeLocationUpdatesWithCallback() {
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage());

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_requestLocationUpdatesWithCallback:
                    requestLocationUpdatesWithCallback();
                    break;
                case R.id.location_removeLocationUpdatesWithCallback:
                    removeLocationUpdatesWithCallback();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:" + e);
        }
    }


}