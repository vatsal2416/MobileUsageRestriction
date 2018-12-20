package vatsalchavda.mobileusagerestriction;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RetriveData extends AppCompatActivity {

    private TextView startLatitude,stopLatitude,startLongitude,stopLongitude,startTime,stopTime,distance;
    public DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive_data);

        databaseHelper = new DatabaseHelper(this);

        startLatitude = findViewById(R.id.txtStartLatitude);
        stopLatitude = findViewById(R.id.txtStopLatitude);
        startLongitude = findViewById(R.id.txtStartLongitude);
        stopLongitude = findViewById(R.id.txtStopLongitude);
        startTime = findViewById(R.id.txtStartTime);
        stopTime = findViewById(R.id.txtStopTime);
        distance = findViewById(R.id.txtDistanceTravelled);

        Button retriveClick = findViewById(R.id.btnRetriveData);

        retriveClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayData();
            }
        });
    }

    public void displayData(){
        Cursor cursor = databaseHelper.allData();
        if(cursor.getCount()==0){
            Toast.makeText(getApplicationContext(),"No Data Present",Toast.LENGTH_LONG).show();
        }else{
            while(cursor.moveToNext()){
                startLatitude.setText("Start Latitude : "+cursor.getString(1));
                stopLatitude.setText("Stop Latitude : "+cursor.getString(2));
                startLongitude.setText("Start Longitude : "+cursor.getString(3));
                stopLongitude.setText("Stop Longitude : "+cursor.getString(4));
                startTime.setText("Start Time : "+cursor.getString(5));
                stopTime.setText("Stop Time : "+cursor.getString(6));
                distance.setText("Distance : "+cursor.getString(7));
            }
        }
    }

}
