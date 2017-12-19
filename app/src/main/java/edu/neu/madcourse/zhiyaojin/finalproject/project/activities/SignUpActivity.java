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
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.neu.madcourse.zhiyaojin.finalproject.R;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.UserDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.helpers.ProgressBarHelper;

public class SignUpActivity extends AppCompatActivity {

    private final static String TAG = "SignUpActivity";

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private TextView loginLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        nameText = findViewById(R.id.signup_name);
        emailText = findViewById(R.id.signup_email);
        passwordText = findViewById(R.id.signup_password);
        signUpButton = findViewById(R.id.signup_btn);
        loginLink = findViewById(R.id.login_link);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void signUp() {
        Log.d(TAG, "Sign Up");
        if (!validate()) {
            return;
        }

        final ProgressBarHelper progressBarHandler = new ProgressBarHelper(this);
        progressBarHandler.show();
        signUpButton.setEnabled(false);

        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            setName(name);

                            UserDao userDao = new UserDao(email);
                            userDao.addUser(name);

                            startActivity(new Intent(SignUpActivity.this, MainMapActivity.class));
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        progressBarHandler.hide();
                        signUpButton.setEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    private boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty()) {
            nameText.setError("Required");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Required");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Required");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void setName(String name) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}
