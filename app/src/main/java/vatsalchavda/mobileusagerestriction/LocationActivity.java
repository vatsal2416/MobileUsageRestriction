package vatsalchavda.mobileusagerestriction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationActivity extends AppCompatActivity {

    private TextView textCounter;
    private boolean count = false;
    private NotificationManager mNotificationManager;
    public Button logoutBtn, btnCallBlock, startLocation, stopLocation, getLastLocation, setCustomSMS;
    private GoogleSignInOptions gso;
    LoginManager loginManager;
    public static int callBlockPermission = 0;
    public static double calSpeed = 0;
    public static final String TAG = LocationActivity.class.getSimpleName();
    public static boolean customSMSset = false;
    DatabaseHelper databaseHelper;

    @BindView(R.id.btn_start_location_updates)
    TextView btnStartUpdates;

    @BindView(R.id.btn_stop_location_updates)
    TextView btnStopUpdates;

    public static String customSMS_String;
    //location last updated
    private String mLastUpdateTime;

    //location updates interval in miliseconds
    private static final long UPDATE_INTERVAL_IN_MILISECONDS = 500;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILISECONDS = 500;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    //location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean locationAquired;
    double startLatitude,startLongitude,endLongitude,endLatitude,tripDistance;
    String startTime, endTime;
    private TextView distance, speed;
    public TextView customSMS;

    //boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private SimpleDateFormat mdformat;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        locationAquired = false;
        SetRestrictions_Activity.sendSMS = true;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        databaseHelper = new DatabaseHelper(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //get Time
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("HH:mm:ss");

        Button retriveData = findViewById(R.id.button3);
        retriveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationActivity.this,RetriveData.class));
            }
        });

        textCounter = findViewById(R.id.textCounter);

        //customSMS = findViewById(R.id.txtCustomSMS);
        setCustomSMS = findViewById(R.id.btnSetCustomSMS);
        startLocation = findViewById(R.id.btn_start_location_updates);
        stopLocation = findViewById(R.id.btn_stop_location_updates);
        getLastLocation = findViewById(R.id.btn_get_last_location);

        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationButtonClick();
                callBlockPermission = 1;
                count = true;
               // startCounter();
                Toast.makeText(getApplicationContext(),"Database Created",Toast.LENGTH_SHORT).show();
            }
        });

        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationButtonClick();
                callBlockPermission = 0;
            }
        });

        setCustomSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationActivity.this, SetRestrictions_Activity.class));
              /*  if((customSMS.getText().toString().equals(""))){
                    Toast.makeText(getApplicationContext(),"Custom SMS cannot be Empty",Toast.LENGTH_LONG).show();
                    customSMSset = false;
                }else{
                    customSMS_String = customSMS.getText().toString().trim();
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                    customSMSset = true;
                }
              */
            }
        });

        getLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastKnownLocation();
            }
        });
        distance = findViewById(R.id.textView3);
        speed = findViewById(R.id.textView4);
        logoutBtn = findViewById(R.id.logoutBtn);
        btnCallBlock = findViewById(R.id.btnCallBlock);
        //initialize the libraries
        init();

        //Facebook
        loginManager = LoginManager.getInstance();

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        logoutBtn.setVisibility(View.VISIBLE);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                loginManager.logOut();
                System.out.println("Signed out Successfully.");
                startActivity(new Intent(LocationActivity.this, LoginActivity.class));
            }
        });

        restoreValuesFromBundle(savedInstanceState);

    }

    private void signOut() {
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Logged out",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);


        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                //Location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    //get Distance
    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }

    //Restoring values from saved instance state
    private void restoreValuesFromBundle(Bundle savedInstanceState){
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("is_requesting_updates")){
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }
            if(savedInstanceState.containsKey("last_known_location")){
                mCurrentLocation=savedInstanceState.getParcelable("last_known_location");
            }
            if(savedInstanceState.containsKey("last_updated_on")){
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    //Update the UI displaying the data and buttons

    @SuppressLint("SetTextI18n")
    private void updateLocationUI(){
        if(mCurrentLocation != null){
            if(!locationAquired){
                startLatitude = mCurrentLocation.getLatitude();
                startLongitude = mCurrentLocation.getLongitude();
                locationAquired=true;
            }


            endLongitude = mCurrentLocation.getLongitude();
            endLatitude = mCurrentLocation.getLatitude();

            tripDistance = getDistance(startLatitude,startLongitude,endLatitude,endLongitude);

            calSpeed = mCurrentLocation.getSpeed() * 3.6;
          //  distance.setText("Distance : "+String.valueOf(Math.round(tripDistance)));
            speed.setText("Speed (KMPH) : "+Math.round(calSpeed));

            //givina a blink animation on TextView
          //  txtLocationResult.setAlpha(0);
          //  txtLocationResult.animate().alpha(1).setDuration(500);

            //location last updated time
          //  txtUpdatedOn.setText("Last updated on: "+mLastUpdateTime);
        }
        toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);
    }

    public void toggleButtons(){
        if(mRequestingLocationUpdates){
            btnStartUpdates.setEnabled(false);
            btnStopUpdates.setEnabled(true);
            speed.setVisibility(View.VISIBLE);
            setCustomSMS.setEnabled(false);
      //      customSMS.setEnabled(false);
        }else{
            btnStartUpdates.setEnabled(true);
            btnStopUpdates.setEnabled(false);
            speed.setVisibility(View.INVISIBLE);
            setCustomSMS.setEnabled(true);
//            customSMS.setEnabled(true);
        }
    }

    //Starting location updates
    //check whether location settings are satisfied
    //and then location update will  be requested
    private void startLocationUpdates(){
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied");

                        //Toast.makeText(getApplicationContext(),"Started location updates!", Toast.LENGTH_SHORT).show();
                        changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
                        Toast.makeText(getApplicationContext(),"All notification disabled.",Toast.LENGTH_LONG).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        distance.setText(null);
                        //startTime = SystemClock.elapsedRealtime();
                        //startTime = mdformat.format(calendar.getTime());
                        startTime = DateFormat.getDateTimeInstance().format(new Date());
                        btnCallBlock.performClick();
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statuscode = ((ApiException) e).getStatusCode();
                        switch(statuscode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try{

                                   // txtUpdatedOn.setText("Failed to get Permission");
                                    //show the dialog by calling startResolutionForResult(), and check the
                                    //result in OnActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                                }catch(IntentSender.SendIntentException sie){
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inqdequate, and cannot be "+
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(LocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                        updateLocationUI();
                    }
                });
    }

    public void startLocationButtonClick(){
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            //open device settings when the permission is denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void stopLocationButtonClick(){
        mRequestingLocationUpdates = false;
        changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
//        Toast.makeText(getApplicationContext(),"All notification Enables.",Toast.LENGTH_LONG).show();
        stopLocationUpdates();
       // endTime = mdformat.format(calendar.getTime());
        //endTime = SystemClock.elapsedRealtime();
        endTime = DateFormat.getDateTimeInstance().format(new Date());
        boolean result = databaseHelper.insertUserData(startLatitude,endLatitude,startLongitude,endLongitude,startTime,endTime,tripDistance);
        if(result){
            Toast.makeText(getApplicationContext(),"Trip details recorded and inserted in Firebase.",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Something went wrong while inserting data!",Toast.LENGTH_SHORT).show();
        }
    }
    protected void changeInterruptionFiler(int interruptionFilter){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){ // If api level minimum 23
            /*
                boolean isNotificationPolicyAccessGranted ()
                    Checks the ability to read/modify notification policy for the calling package.
                    Returns true if the calling package can read/modify notification policy.
                    Request policy access by sending the user to the activity that matches the
                    system intent action ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS.

                    Use ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED to listen for
                    user grant or denial of this access.

                Returns
                    boolean

            */
            // If notification policy access granted for this package
            if(mNotificationManager.isNotificationPolicyAccessGranted()){
                /*
                    void setInterruptionFilter (int interruptionFilter)
                        Sets the current notification interruption filter.

                        The interruption filter defines which notifications are allowed to interrupt
                        the user (e.g. via sound & vibration) and is applied globally.

                        Only available if policy access is granted to this package.

                    Parameters
                        interruptionFilter : int
                        Value is INTERRUPTION_FILTER_NONE, INTERRUPTION_FILTER_PRIORITY,
                        INTERRUPTION_FILTER_ALARMS, INTERRUPTION_FILTER_ALL
                        or INTERRUPTION_FILTER_UNKNOWN.
                */

                // Set the interruption filter
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            }else {
                /*
                    String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                        Activity Action : Show Do Not Disturb access settings.
                        Users can grant and deny access to Do Not Disturb configuration from here.

                    Input : Nothing.
                    Output : Nothing.
                    Constant Value : "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"
                */
                // If notification policy access not granted for this package
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    public void stopLocationUpdates(){
        //Removing location updates
        btnCallBlock.performClick();
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      //  Toast.makeText(getApplicationContext(), "Location update stopped!", Toast.LENGTH_SHORT).show();
                        toggleButtons();
                       // databaseHelper.insertUserData(startLatitude,endLatitude,startLongitude,endLongitude);
                    }
                });
    }

    public void showLastKnownLocation(){
        if(mCurrentLocation != null){
            Toast.makeText(getApplicationContext(), "Latitude: "+mCurrentLocation.getLatitude()
                    +", Longitude: "+mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Last known location is not Available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent date){
        switch(requestCode){
            case REQUEST_CHECK_SETTINGS:
                switch(resultCode){
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        //nothing to do. startLocationUpdates()
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();

        //Resuming location updates depending on button state and allowed permissions
        if(mRequestingLocationUpdates && checkPermissions()){
            startLocationUpdates();
        }
        updateLocationUI();
    }

    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(mRequestingLocationUpdates){
            //pausing location updates
            stopLocationUpdates();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);

    }

    public void startCounter(){
        int counter = 0;
        for(int i = 0 ; count!=false ; i++){
            try{
                Thread.sleep(1000);
                counter++;
               Toast.makeText(getApplicationContext(),"Counter : "+counter,Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

