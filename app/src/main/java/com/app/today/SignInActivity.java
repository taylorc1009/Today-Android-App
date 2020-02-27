package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.mortbay.jetty.security.Constraint;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Patterns.EMAIL_ADDRESS;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email, password, pName;
    Button signIn;
    ConstraintLayout registerExpand, registerGroup;
    ImageView rExArrow;

    //private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private String emailStr = "";
    private String passStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rExArrow = findViewById(R.id.rExArrow);
        registerExpand = findViewById(R.id.registerExpand);
        registerGroup = findViewById(R.id.registerGroup);
        pName = findViewById(R.id.pName);
        signIn = findViewById(R.id.signInBtn);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        cleanView();

        registerExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registerGroup.getVisibility() == View.GONE) {
                    rExArrow.animate().rotation(180);
                    registerGroup.setVisibility(View.VISIBLE);
                    registerGroup.animate().alpha(1);
                    signIn.setText(R.string.registerBtn);
                }
                else {
                    rExArrow.animate().rotation(0);
                    registerGroup.animate().alpha(0).setDuration(1).withEndAction(cleanView()); // doesn't fade out then clear
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailStr = email.getText().toString().trim();
                passStr = password.getText().toString().trim();
                if(!passStr.equals("") && isValidPassword(passStr)) {
                    if(!emailStr.equals("") && isValidEmail(emailStr)) {
                        if(registerGroup.getVisibility() == View.VISIBLE)
                            createAccount();
                        else
                            signIn();
                        //signIn();
                    } else
                        Toast.makeText(SignInActivity.this, "Invalid email, please re-enter", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(SignInActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Firebase Login", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user); // <-- display login in UI
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Firebase Login", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    //updateUI(null); // <-- display login in UI
                }

                // ...
            }
        });
    }
    private void signIn() {
        mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FIrebase Login", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FIrebase Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }

                // ...
            }
        });
    }
    public boolean isSignedIn() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }
    private boolean isValidEmail(String str) {
        return EMAIL_ADDRESS.matcher(str).matches();
    }
    private boolean isValidPassword(String str) {
        for(int i = 0; i < str.length(); i++)
            if(!isValidChar(str.charAt(i)))
                return false;
        return true;
    }
    private boolean isValidChar(char c) {
        return c != '(' && c != ')' && c != '\"' && c != '=' && c != '\'' && c != '\\' && c != ' ';
    }
    private Runnable cleanView() {
        if(registerGroup.getVisibility() != View.GONE) {
            registerGroup.setVisibility(View.GONE);
            pName.getText().clear();
            rExArrow.setRotation(0);
            registerGroup.setAlpha(0);
            signIn.setText(R.string.signInBtn);
        }
        return null;
    }
}
