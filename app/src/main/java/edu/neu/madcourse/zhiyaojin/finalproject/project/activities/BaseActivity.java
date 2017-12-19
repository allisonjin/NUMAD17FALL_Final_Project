package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    private final static String TAG = "BaseActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
    }

    protected FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    protected boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            int permissionState = ActivityCompat.checkSelfPermission(this, permission);
//            Log.d(TAG, permission + ": " + permissionState);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected void askForPermission(String[] permissions, int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, permissionCode);
        }
    }

    protected void signOut() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
