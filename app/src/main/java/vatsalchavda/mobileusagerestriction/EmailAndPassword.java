package vatsalchavda.mobileusagerestriction;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAndPassword extends LoginActivity{

    FirebaseAuth mAuth;
    FirebaseUser user;
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

         user = mAuth.getCurrentUser();

        if(isEmailValid(email)){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(user.isEmailVerified()){
                            LoginResult = true;
                        }else{
                            LoginResult = false;
                        }
                     }
                });
            return LoginResult;
        }else
            return false;
    }
    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    //Register method
    public boolean methodRegister(String email, String password){
        if(isEmailValid(email)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            RegisterResult = true;
                            sendVerificationEmail();
                        }
                });

            return RegisterResult;
        }else
            return false;
    }

    public boolean methodForgotPassword(String email){
        if(isEmailValid(email)){
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
        }else
            return false;
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
