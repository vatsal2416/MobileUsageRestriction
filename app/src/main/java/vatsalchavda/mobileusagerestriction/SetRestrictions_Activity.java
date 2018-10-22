package vatsalchavda.mobileusagerestriction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetRestrictions_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_restrictions_);

        Button btnLimit, btnSMS, btnBack;
        final TextView txtLimit, txtSMS;

        btnLimit = findViewById(R.id.btnLimit);
        btnSMS = findViewById(R.id.btnSMS);
        btnBack = findViewById(R.id.btnBack);

        txtLimit = findViewById(R.id.txtSpeedLimit);
        txtSMS = findViewById(R.id.txtSMS_Text);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetRestrictions_Activity.this,LocationActivity.class));
            }
        });

        btnLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int limit = Integer.parseInt(txtLimit.getText().toString());
                if(limit==0){
                    Toast.makeText(getApplicationContext(), "Speed cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    IncomingCallReceiver.speedLimit = limit;
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMessage1 = txtSMS.getText().toString();
                if(textMessage1.equals("")){
                    Toast.makeText(getApplicationContext(), "TextBox cannot be Empty!", Toast.LENGTH_SHORT).show();
                    LocationActivity.customSMSset = false;
                }else{
                    //Toast.makeText(getApplicationContext(), "Text Message : "+textMessage1, Toast.LENGTH_SHORT).show();

                    LocationActivity.customSMS_String = textMessage1;
                    LocationActivity.customSMSset = true;
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
