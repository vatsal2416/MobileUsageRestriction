package vatsalchavda.mobileusagerestriction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DatabaseActivity extends AppCompatActivity {

    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        myDB = new DatabaseHelper(this);
        Toast.makeText(getApplicationContext(),"Database Created",Toast.LENGTH_SHORT).show();
    }
}
