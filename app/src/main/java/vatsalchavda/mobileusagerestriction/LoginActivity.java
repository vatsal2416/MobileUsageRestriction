package vatsalchavda.mobileusagerestriction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {


    private TextView textEmail;
    private TextView textPassword;
    Button forgotPassword,loginBtn,registerBtn;
    private EmailAndPassword emailAndPassword;
    SignInButton googleBtn;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    public static String accountName;
    //Facebook
    private CallbackManager callbackManager;
    LoginButton loginButton;
    private static final String EMAIL = "email";
    private boolean login, register, ForgotPassword;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Required Permissions
        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.facebookLogin);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        login = false;
        register = false;
        ForgotPassword = false;

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, LocationActivity.class));
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
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

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
                login = emailAndPassword.methodLogin(String.valueOf(textEmail.getText()),String.valueOf(textPassword.getText()));
                Toast.makeText(getApplicationContext(),"Login : "+login,Toast.LENGTH_SHORT).show();
                if(login){
                    Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
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
                register = emailAndPassword.methodRegister(String.valueOf(textEmail.getText()),String.valueOf(textPassword.getText()));

                try {
                    //wait(100);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(String.valueOf(textEmail.getText()).equals("") || String.valueOf(textPassword.getText()).equals("")){
                    Toast.makeText(getApplicationContext(),"Email and Password cannot be blank.",Toast.LENGTH_SHORT).show();
                }else{
                    if(register){
                        Toast.makeText(getApplicationContext(),"You have successfully registered."
                                +"\nCheck your email for Verification.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Something went wrong please try again.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword = emailAndPassword.methodForgotPassword(String.valueOf(textEmail.getText()));
                if(String.valueOf(textEmail.getText()).equals("")){
                    Toast.makeText(getApplicationContext(),"Enter email address!",Toast.LENGTH_SHORT).show();
                }else{
                    if(ForgotPassword){
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

        //for Facebook Login
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        //for Google Login
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

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                               accountName = account.getDisplayName();
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this,LocationActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
