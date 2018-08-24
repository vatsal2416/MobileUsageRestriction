package vatsalchavda.mobileusagerestriction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

public class LoginActivity extends AppCompatActivity {


    private TextView textEmail;
    private TextView textPassword;
    private Button forgotPassword,loginBtn,registerBtn;
    EmailAndPassword emailAndPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());

        // Initialize all the controls
        textEmail = findViewById(R.id.editTextEmail);
        textPassword = findViewById(R.id.editTextPassword);
        forgotPassword = findViewById(R.id.btnForgotPassword);
        loginBtn = findViewById(R.id.btnLogin);
        registerBtn = findViewById(R.id.btnRegister);

        //initialize instance of EmailAndPassword class
        emailAndPassword = new EmailAndPassword();

        //Login with Email and Password
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailAndPassword.methodLogin(String.valueOf(textEmail.getText()),String.valueOf(textPassword.getText()))){
                    Intent intent = new Intent(LoginActivity.this, LoginSuccess.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Authentication Failed!"
                            +"\nPlease check your username and Password.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailAndPassword.methodRegister(String.valueOf(textEmail.getText()),String.valueOf(textPassword.getText()))){
                    Toast.makeText(getApplicationContext(),"You have successfully registered."
                            +"\nCheck your email for Verification.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Something went wrong please try again.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(textEmail.getText()).equals("")){
                    Toast.makeText(getApplicationContext(),"Enter email address!",Toast.LENGTH_SHORT).show();
                }else{
                    if(emailAndPassword.methodForgotPassword(String.valueOf(textEmail.getText()))){
                        Toast.makeText(getApplicationContext(),"Successfully Sent Password reset link.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Failed! Email address doesn't exist.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
