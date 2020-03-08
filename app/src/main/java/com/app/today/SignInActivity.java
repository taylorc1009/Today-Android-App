package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    EditText email, password, pName;
    Button signIn;
    ConstraintLayout credentialsGroup, registerExpand, registerGroup, authenticating;
    ImageView rExArrow;
    ProgressBar authLoad;
    TextView authTxt;

    protected static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String emailStr = "";
    private String passStr = "";
    private String realName = "";
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rExArrow = findViewById(R.id.rExArrow);
        credentialsGroup = findViewById(R.id.credentialsGroup);
        registerExpand = findViewById(R.id.registerExpand);
        registerGroup = findViewById(R.id.registerGroup);
        authenticating = findViewById(R.id.authenticating);
        authLoad = findViewById(R.id.authLoad);
        authTxt = findViewById(R.id.authTxt);
        pName = findViewById(R.id.pName);
        signIn = findViewById(R.id.signInBtn);
        cleanView();
        registerExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRegistering) {
                    isRegistering = true;
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
                if(!password.getText().toString().trim().equals("") && isValidPassword(password.getText().toString().trim())) {
                    if(!email.getText().toString().trim().equals("") && isValidEmail(email.getText().toString().trim())) {
                        if(isRegistering) {
                            if(!pName.getText().toString().trim().equals("") && isValidName(pName.getText().toString().trim())) {
                                emailStr = email.getText().toString().trim();
                                passStr = password.getText().toString().trim();
                                realName = pName.getText().toString().trim();
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
    private void createAccount() {
        authTxt.setText(R.string.authReg);
        mAuth.createUserWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("Firebase Register", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(realName).build();
                    assert user != null;
                    user.updateProfile(request);
                    Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Log.w("Firebase Register", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    authenticating.setVisibility(View.GONE);
                    credentialsGroup.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void signIn() {
        authTxt.setText(R.string.authSign);
        mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("Firebase Login", "signInWithEmail:success");
                    Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Log.w("Firebase Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    authenticating.setVisibility(View.GONE);
                    credentialsGroup.setVisibility(View.VISIBLE);
                }
            }
        });
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
    private boolean isValidName(String str) {
        boolean result = true;
        String[] indexes;
        indexes = str.split(" ");
        if(indexes.length != 1 || !str.matches("[A-Za-z]+"))
            result = false;
        return result;
    }
    private Runnable cleanView() {
        if(isRegistering) {
            isRegistering = false;
            registerGroup.setVisibility(View.GONE);
            pName.getText().clear();
            rExArrow.setRotation(0);
            registerGroup.setAlpha(0);
            signIn.setText(R.string.signInBtn);
        }
        return null;
    }
}
