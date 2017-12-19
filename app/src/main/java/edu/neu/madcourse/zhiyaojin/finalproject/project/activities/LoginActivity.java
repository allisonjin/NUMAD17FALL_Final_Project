package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.neu.madcourse.zhiyaojin.finalproject.R;
import edu.neu.madcourse.zhiyaojin.finalproject.project.helpers.ProgressBarHelper;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signUpLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.login_email);
        passwordText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_btn);
        signUpLink = findViewById(R.id.signup_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            jumpToMain();
        }
    }

    private void jumpToMain() {
        startActivity(new Intent(LoginActivity.this, MainMapActivity.class));
    }

    private void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
//            Toast.makeText(LoginActivity.this, "Login failed",
//                    Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        final ProgressBarHelper progressBarHelper = new ProgressBarHelper(this);
        progressBarHelper.show();
//        showProgressBar();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, user.getEmail() + "login successful",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        loginButton.setEnabled(true);
                        progressBarHelper.hide();
//                        hideProgressBar();

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            jumpToMain();
                        }

                    }
                });
    }

    private boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("enter a password");
            valid = false;
        } else {
            passwordText.setError(null);
        }

//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            passwordText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        }

        return valid;
    }

}
