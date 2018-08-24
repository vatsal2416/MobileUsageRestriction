package vatsalchavda.mobileusagerestriction;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {


    private TextView textEmail;
    private TextView textPassword;
    private Button forgotPassword,loginBtn,registerBtn;
    private EmailAndPassword emailAndPassword;
    private SignInButton googleBtn;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
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
        googleBtn = findViewById(R.id.googleBtn);
        mAuth = FirebaseAuth.getInstance();

        //initialize instance of classes
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "You Got an Error",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //Google Button
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                signIn();
                }
        });
    }

    // Google SignIn
    private void signIn() {
        Intent signInIntent =Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(),"Google sign in Failed!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this,LoginSuccess.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
