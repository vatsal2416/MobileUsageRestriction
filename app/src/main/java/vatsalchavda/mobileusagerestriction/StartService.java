package vatsalchavda.mobileusagerestriction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);
        final Button startLocation = LocationActivity.startLocation;
        final Button stopLocation = LocationActivity.stopLocation;
        final Button logout = LocationActivity.logoutBtn;
        Button logoutBtn = findViewById(R.id.logoutBtn2);


        final Button startServices = findViewById(R.id.startServices);
        final Button stopServices = findViewById(R.id.stopServices);
        stopServices.setClickable(false);
        startServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation.performClick();
                startServices.setClickable(false);
                stopServices.setClickable(true);
            }
        });

        stopServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocation.performClick();
                startServices.setClickable(true);
                stopServices.setClickable(false);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout.performClick();
            }
        });

    }
}
