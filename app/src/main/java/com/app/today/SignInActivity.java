package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.util.Patterns.EMAIL_ADDRESS;

public class SignInActivity extends AppCompatActivity {
    //UI attributes
    EditText email, password, realName;
    Button signIn;
    ConstraintLayout credentialsGroup, registerExpand, registerGroup, authenticating;
    ImageView regArrow;
    ProgressBar authLoad;
    TextView authTxt;

    //Create an instance of Firebase authenticator
    protected static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Local sign-in/register variables
    private String emailStr = "";
    private String passStr = "";
    private String displayName = "";
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initialize the UI attributes
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regArrow = findViewById(R.id.regArrow);
        credentialsGroup = findViewById(R.id.credentialsGroup);
        registerExpand = findViewById(R.id.registerExpand);
        registerGroup = findViewById(R.id.registerGroup);
        authenticating = findViewById(R.id.authenticating);
        authLoad = findViewById(R.id.authLoad);
        authTxt = findViewById(R.id.authTxt);
        realName = findViewById(R.id.realName);
        signIn = findViewById(R.id.signInBtn);

        cleanView();

        //Button to change to register an account instead of signing in
        registerExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user is not already registering, enter register mode
                //Else exit register mode
                if(!isRegistering) {
                    isRegistering = true;
                    //Animates the reveal of the register views
                    regArrow.animate().rotation(180);
                    registerGroup.setVisibility(View.VISIBLE);
                    registerGroup.animate().alpha(1).setDuration(300).setListener(null);
                    //Changes the sign-in button text
                    signIn.setText(R.string.registerBtn);
                }
                else {
                    regArrow.animate().rotation(0);
                    registerGroup.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cleanView();
                        }
                    });
                }
            }
        });

        //Button used to sign-in/register
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If password is not blank and is valid, continue
                //Else request re-entry
                if(!password.getText().toString().trim().equals("") && isValidPassword(password.getText().toString().trim())) {
                    //If email is not blank and is valid, continue
                    //Else request re-entry
                    if(!email.getText().toString().trim().equals("") && isValidEmail(email.getText().toString().trim())) {
                        AppUtilities.hideKeyboardFrom(getApplicationContext(), signIn.getRootView().findFocus());

                        //If the user is registering, validate their display name then attempt to register them
                        //Else attempt sign in
                        if(isRegistering) {
                            if(!realName.getText().toString().trim().equals("") && isValidName(realName.getText().toString().trim())) {
                                emailStr = email.getText().toString().trim();
                                passStr = password.getText().toString().trim();
                                displayName = realName.getText().toString().trim();
                                credentialsGroup.setVisibility(View.GONE);
                                authenticating.setVisibility(View.VISIBLE);
                                createAccount();
                            } else
                                Toast.makeText(SignInActivity.this, "Invalid name (we only need your first name)", Toast.LENGTH_SHORT).show();
                        } else {
                            emailStr = email.getText().toString().trim();
                            passStr = password.getText().toString().trim();
                            credentialsGroup.setVisibility(View.GONE);
                            authenticating.setVisibility(View.VISIBLE);
                            signIn();
                        }
                    } else
                        Toast.makeText(SignInActivity.this, "Invalid email, please re-enter", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SignInActivity.this, "Invalid password, please re-enter", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method used to register the user using Firebase
    private void createAccount() {
        //Set the signing in view text to...
        authTxt.setText(R.string.authReg);

        //Utilizes the Firebase create account task to create the users account
        mAuth.createUserWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //If the registration was successful, update the local sign in details of the app then go to the MainActivity
                //Else display an error and return to the sign in page
                if(task.isSuccessful()) {
                    Log.d("Firebase Register", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    //Set the users display name to what they entered upon registering
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                    assert user != null;
                    user.updateProfile(request);

                    Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Log.w("Firebase Register", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Failed to register account...", Toast.LENGTH_SHORT).show();
                    authenticating.setVisibility(View.GONE);
                    credentialsGroup.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Method used to sign-in an existing user
    private void signIn() {
        //Set the signing in view text to..
        authTxt.setText(R.string.authSign);

        //Utilizes the Firebase sing-in task to sign the user into an existing account
        mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //If the sign-in was successful, send the user to the main activity
                //Else display an error and return them to the sign in page
                if(task.isSuccessful()) {
                    Log.d("Firebase Login", "signInWithEmail:success");
                    Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Log.w("Firebase Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed...", Toast.LENGTH_SHORT).show();
                    authenticating.setVisibility(View.GONE);
                    credentialsGroup.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Used to determine whether the users email matches the traditional email format using Android's
    //EMAIL_ADDRESS utility
    private boolean isValidEmail(String str) {
        return EMAIL_ADDRESS.matcher(str).matches();
    }

    //Used to check the users password
    private boolean isValidPassword(String str) {
        //For every char in the password field, check it it is a valid char
        for(int i = 0; i < str.length(); i++)
            if(!isValidChar(str.charAt(i)))
                return false;
        return true;
    }
    //Used to check each char in a password, mainly to check for illegal, syntax-altering chars
    private boolean isValidChar(char c) {
        return c != '(' && c != ')' && c != '\"' && c != '%' && c != '\'' && c != '\\' && c != '/' && c != ' ';
    }

    //Used to determine if the display name is valid by:
    private boolean isValidName(String str) {
        boolean result = true;
        String[] indexes;
        indexes = str.split(" ");
        //Checking if there is more than one word in the name or the name does not match a regex of a-z and A-Z
        if(indexes.length != 1 || !str.matches("[A-Za-z]+"))
            result = false;
        return result;
    }

    //Used to clean the register views
    private void cleanView() {
        if(isRegistering) {
            isRegistering = false;
            registerGroup.setVisibility(View.GONE);
            realName.getText().clear();
            regArrow.setRotation(0);
            registerGroup.setAlpha(0);
            signIn.setText(R.string.signInBtn);
        }
    }
}