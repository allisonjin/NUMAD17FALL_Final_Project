package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.zhiyaojin.finalproject.R;
import edu.neu.madcourse.zhiyaojin.finalproject.common.listeners.OnGetDataListener;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.MissionDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.UserDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.entities.Mission;
import edu.neu.madcourse.zhiyaojin.finalproject.project.helpers.ProgressBarHelper;
import edu.neu.madcourse.zhiyaojin.finalproject.project.utils.CommonUtils;
import edu.neu.madcourse.zhiyaojin.finalproject.project.utils.LocationUtils;

public class MainMapActivity extends BaseLocationActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMapClickListener {

    private final static String TAG = "MainMapActivity";
    private final static int MAP_PERMISSIONS_CODE = 111;
    private final static long MAX_TIME = 3600;

    private GoogleMap mMap;
    private MissionDao mMissionDao;
    private UserDao mUserDao;
    private ProgressBarHelper mProgressBar;
    private Mission currentMission;

    private LinearLayout missionDetailsLayout;
    private LinearLayout menuLayout;
    private ImageView mLargeImage;
    private TextView timeRemainingText;
    private TextView attemptTimesText;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private CountdownTask countdownTask;

    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);

        missionDetailsLayout = findViewById(R.id.mission_details);
        hideView(missionDetailsLayout);
        attemptTimesText = findViewById(R.id.attempt_time);
        timeRemainingText = findViewById(R.id.time_remaining);

        menuLayout = findViewById(R.id.user_menu);
        hideView(menuLayout);

        mLargeImage = findViewById(R.id.large_image);
        hideView(mLargeImage);

        mMissionDao = new MissionDao();
        mUserDao = new UserDao(getCurrentUser().getEmail());
        mProgressBar = new ProgressBarHelper(this);

        missionDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMission != null) {
                    DownloadLargeImageTask task = new DownloadLargeImageTask(mLargeImage);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentMission.getImageURL());
                    mProgressBar.show();
                }
            }
        });

        mLargeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideView(mLargeImage);
            }
        });

        ImageButton menuButton = findViewById(R.id.map_menu_btn);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCountdown();
                showView(menuLayout);
                hideView(missionDetailsLayout);
            }
        });

        ImageButton cameraButton = findViewById(R.id.map_camera_btn);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMapActivity.this,
                        PhotoCaptureUploadActivity.class));
            }
        });

        Button signOutButton = findViewById(R.id.menu_sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Button gameHistoryButton = findViewById(R.id.menu_game_history);
        gameHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMapActivity.this, GameRecordsActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        moveCameraToMyLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        if (getCurrentLocation() != null) {
            initMyLocationLayer();
        } else {
            mProgressBar.show();
            LatLng center = new LatLng(47.6062, -122.3321);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        }
    }

    @SuppressLint("MissingPermission")
    private void initMyLocationLayer() {
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            moveCameraToMyLocation();
        }
    }

    private void moveCameraToMyLocation() {
        moveCamera(getCurrentLocation());
    }

    private void moveCamera(Location location) {
        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                    location.getLongitude())));
        }
    }

    private void drawMissionMarkers(List<Mission> missions) {
        Iterator<Marker> iterator = markers.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            Mission mission = (Mission)marker.getTag();
            if (missions.contains(mission)) {
                missions.remove(mission);
            } else {
                marker.remove();
                iterator.remove();
            }
        }

        for (Mission mission : missions) {
            double[] coarseLatLng = LocationUtils.getCoarseLatLng(mission.getLatitude(),
                    mission.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(coarseLatLng[0], coarseLatLng[1]))
                                    .title(mission.getLatitude() + " " + mission.getLongitude()));
            marker.setTag(mission);
            markers.add(marker);
        }
    }

    private void updateMissionsInCamera() {
        final List<Mission> missions = new ArrayList<>();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        final double minLat = bounds.southwest.latitude;
        final double maxLat = bounds.northeast.latitude;
        final double minLng = bounds.southwest.longitude;
        final double maxLng = bounds.northeast.longitude;
        Log.d(TAG, "" + minLat + " " + maxLat + " " + minLng + " " + maxLng);

        mMissionDao.getMissionsInCamera(minLat, maxLat, new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot latDataSnapshot : dataSnapshot.getChildren()) {
//                    Log.d(TAG, latDataSnapshot.toString());
                    Mission mission = latDataSnapshot.getValue(Mission.class);
                    double lng = mission.getLongitude();
                    long timeDiff = getTimeDiff(mission.getCreatedTime());
//                    Log.d(TAG, "" + timeDiff);
                    if (!mission.isCompleted() && timeDiff <= MAX_TIME && lng >= minLng && lng <= maxLng) {
                        missions.add(mission);
                    }
                }
                drawMissionMarkers(missions);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });
    }

    private long getTimeDiff(String createdTime) {
        Date createdDate = null;
        try {
            createdDate = sdf.parse(createdTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return createdDate == null ? 0 : CommonUtils.getDateDiff(createdDate, new Date(), TimeUnit.SECONDS);

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "camera moved");
        updateMissionsInCamera();
    }

//    @Override
//    public void onLocationPermissionsResult() {
//        initMyLocationLayer();
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        cancelCountdown();
        Mission mission = (Mission)marker.getTag();
        currentMission = mission;
        updateMissionDetails(mission);

        return true;
    }
    
    private void cancelCountdown() {
        if (countdownTask != null) {
            Log.d(TAG, "cancel countdown");
            countdownTask.cancel(true);
        }
    }

    private void updateMissionDetails(final Mission mission) {
        ImageView imageView = findViewById(R.id.mission_thumb);
        imageView.setImageBitmap(null);
        Log.d(TAG, mission.getThumbURL());
        new DownloadImageTask(imageView).execute(mission.getThumbURL());

        String createdTime = mission.getCreatedTime();
        long diffInSec = getTimeDiff(createdTime);
        if (diffInSec != 0) {
           countdownTask = new CountdownTask();
           countdownTask.execute(diffInSec);
        }

        final TextView createdByText = findViewById(R.id.created_by);
        mUserDao.getUsernameByEmail(mission.getUserId(), new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                createdByText.setText("Created By: " + dataSnapshot.getValue());
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        final long[] attemptTimesArray = new long[1];
        mUserDao.getAttemptTimes(mission.getMissionId(), new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                long attemptTimes = dataSnapshot.getValue() != null ? (long)dataSnapshot.getValue() : 0;
                attemptTimesText.setText("Attempt: " + attemptTimes);
                attemptTimesArray[0] = attemptTimes;
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        final String missionId = mission.getMissionId();
        Button findButton = findViewById(R.id.find_btn);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attemptTimesArray[0] == 10) {
                    Toast.makeText(MainMapActivity.this,
                            "You have exceeded the maximum number of attempts!",
                            Toast.LENGTH_SHORT).show();
                } else if (currentMission != null
                        && currentMission.getUserId().equals(getCurrentUser().getEmail())) {
                    Toast.makeText(MainMapActivity.this, "This is your own mission :)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    boolean isFound = LocationUtils.isLocationClose(getCurrentLocation(),
                            LocationUtils.newLocation(mission.getLatitude(), mission.getLongitude()),
                            2);
                    if (isFound) {
                        cancelCountdown();
                        Toast.makeText(MainMapActivity.this, "Found it!",
                                Toast.LENGTH_SHORT).show();
                        completeMission(missionId);
                        hideView(missionDetailsLayout);
                    } else {
                        Toast.makeText(MainMapActivity.this, "Incorrect! Please try again :(",
                                Toast.LENGTH_SHORT).show();
                        mUserDao.attemptIncrement(missionId);
                    }
                }
            }
        });
    }

    private void completeMission(String missionId) {
        mUserDao.removeAttemptedMission(missionId);
        mUserDao.addCompletedMission(missionId);
        mMissionDao.setMissionCompleted(missionId, true);
    }

    @Override
    public void onMapClick(LatLng point) {
        cancelCountdown();
        hideView(missionDetailsLayout);
        hideView(menuLayout);
    }

    private void showView(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(@NonNull View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void onGetLocationSuccess(Location location) {
        if (mMap != null && !mMap.isMyLocationEnabled()) {
            initMyLocationLayer();
        }

        if (mMap != null){
            updateMissionsInCamera();
        }

        mProgressBar.hide();
    }

    @Override
    public void onBackPressed() {

    }

    private Bitmap downloadImage(String url) {
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            Log.d(TAG, "thumb constructor");
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d(TAG, "do in bg");
            String url = urls[0];
            return downloadImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d(TAG, "thumb");
            bmImage.setImageBitmap(result);
            showView(missionDetailsLayout);
        }
    }

    private class DownloadLargeImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadLargeImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            return downloadImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d(TAG, "large:" + result.getByteCount());
            mLargeImage.setImageBitmap(result);
            mProgressBar.hide();
            showView(mLargeImage);
        }
    }

    private class CountdownTask extends AsyncTask<Long, Long, Void> {

        @Override
        protected Void doInBackground(Long... time) {
            long startTime = MAX_TIME - time[0];
            for (long i = startTime; i > 0; i--) {
                publishProgress(i);
                if (isCancelled()) {
                    Log.d(TAG, "countdown break");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            long time = progress[0];
            long min = time / 60;
            long second = time % 60;
            String text = String.format(Locale.getDefault(),
                    "Time Remaining: %d:%02d", min, second);
            timeRemainingText.setText(text);
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "countdown stops");
        }
    }

}
