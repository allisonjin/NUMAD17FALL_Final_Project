package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.MissionDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.UserDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.entities.Mission;
import edu.neu.madcourse.zhiyaojin.finalproject.project.helpers.ProgressBarHelper;

public class PhotoCaptureUploadActivity extends BaseLocationActivity {

    private final static String TAG = "PhotoCaptureUploadAct";
    private final static int PERMISSIONS_CODE = 2;
    private final static int REQUEST_CAMERA = 1;

    private Mission mMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMission = new Mission();
        FirebaseUser user = getCurrentUser();
        mMission.setUserId(user.getEmail());

        String[] permissions = new String[] {
                Manifest.permission.CAMERA
        };
        if (!checkPermissions(permissions)) {
            askForPermission(permissions, PERMISSIONS_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                backToMap();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            byte[] imageBytes, thumbnailBytes;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Log.d(TAG, "size: " + bitmap.getByteCount() / 1000);
                Bitmap rotatedBitmap = rotateBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                imageBytes = baos.toByteArray();

                Bitmap thumbnail = (Bitmap)data.getExtras().get("data");
                baos.reset();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumbnailBytes = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            uploadImage(imageBytes, thumbnailBytes);

        } else if (data == null || data.getData() == null) {
            Toast.makeText(this, "Sorry, your device doesn't support this feature!",
                    Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            backToMap();
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    private void uploadImage(byte[] imageBytes, byte[] thumbnailBytes) {
        final ProgressBarHelper progressBar = new ProgressBarHelper(this);
        progressBar.show();

        final String imageName = generateImageName();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images").child(imageName);
        StorageReference thumbRef = storageRef.child("thumb").child("thumb_" + imageName);

        UploadTask thumbUploadTask = thumbRef.putBytes(thumbnailBytes);
        thumbUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                mMission.setThumbURL(downloadURL.toString());
            }
        });

        UploadTask imageUploadTask = imageRef.putBytes(imageBytes);
        imageUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(PhotoCaptureUploadActivity.this,
                        "Photo upload failed!", Toast.LENGTH_SHORT);
                progressBar.hide();

                backToMap();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mMission.setImageURL(downloadUrl.toString());
                Log.d(TAG, downloadUrl.toString());
                progressBar.hide();

                setMissionLocation();

                if (mMission.getImageURL() != null && mMission.getThumbURL() != null) {
                    addMission();

                    Toast.makeText(PhotoCaptureUploadActivity.this, "Photo uploaded!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhotoCaptureUploadActivity.this,
                            "Photo upload failed!", Toast.LENGTH_SHORT);
                }

                backToMap();
            }
        });
    }

    private String generateImageName() {
        return UUID.randomUUID() + ".jpg";
    }

    private void setMissionLocation() {
        Location location = getCurrentLocation();
        mMission.setLatitude(location.getLatitude());
        mMission.setLongitude(location.getLongitude());
    }

    private void addMission() {
        MissionDao missionDao = new MissionDao();
        UserDao userDao = new UserDao(getCurrentUser().getEmail());
        String missionId = missionDao.addMission(mMission);
        userDao.addUserCreatedMission(missionId);
    }

    @Override
    public void onGetLocationSuccess(Location location) {
        if (location != null) {
            Log.d(TAG, "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
        }
    }

    @Override
    public void onBackPressed() {
        backToMap();
    }

    private void backToMap() {
//        startActivity(new Intent(this, MainMapActivity.class));
        finish();
    }

}
