package vatsalchavda.mobileusagerestriction;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailAndPassword extends LoginActivity{

    FirebaseAuth mAuth;
    boolean LoginResult, RegisterResult, ForgotPasswordResult, EmailVerification;
    EmailAndPassword(){
        mAuth = FirebaseAuth.getInstance();
        LoginResult = false;
        RegisterResult = false;
        ForgotPasswordResult = false;
        EmailVerification = false;
    }

    //Login method
    public boolean methodLogin(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginResult = true;
                    }
                });
        return LoginResult;
    }

    //Register method
    public boolean methodRegister(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        RegisterResult = true;
                        sendVerificationEmail();
                    }
                });

        return RegisterResult;
    }


    public boolean methodForgotPassword(String email){

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ForgotPasswordResult = true;
                        } else {
                            ForgotPasswordResult = false;
                        }
                    }
                });
        return ForgotPasswordResult;
    }
    private boolean sendVerificationEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        EmailVerification = true;
                    }
                });
        return EmailVerification;
    }
}
