package vatsalchavda.mobileusagerestriction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SendSMS extends AppCompatActivity {

    private TextView mobileNumber, textMessage;
    Button btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        mobileNumber = findViewById(R.id.textMobileNumber);
        textMessage = findViewById(R.id.textMessage);

        btnSendMessage = findViewById(R.id.btnSendMessage);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mobileNumber.getText().toString()).equals("") || (mobileNumber.getText().toString()).equals("")){
                    Toast.makeText(getApplicationContext(),"Mobile number or Message cannot be Empty!",Toast.LENGTH_SHORT).show();
                }else{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mobileNumber.getText().toString(),null,textMessage.getText().toString(),null,null);
                    Toast.makeText(getApplicationContext(),"Message sent.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
