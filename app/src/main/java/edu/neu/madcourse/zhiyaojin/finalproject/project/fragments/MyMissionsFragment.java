package edu.neu.madcourse.zhiyaojin.finalproject.project.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.zhiyaojin.finalproject.R;
import edu.neu.madcourse.zhiyaojin.finalproject.common.listeners.OnGetDataListener;
import edu.neu.madcourse.zhiyaojin.finalproject.project.activities.GameRecordsActivity;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.MissionDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.dao.UserDao;
import edu.neu.madcourse.zhiyaojin.finalproject.project.entities.Mission;

public class MyMissionsFragment extends Fragment {

    private final static String TAG = "MyMissionsFragment";

    private MissionDao mMissionDao;
    private UserDao mUserDao;

    public MyMissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = ((GameRecordsActivity)getActivity()).getCurrentUser().getEmail();
        Log.d(TAG, email);
        mMissionDao = new MissionDao();
        mUserDao = new UserDao(email);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_my_missions, container, false);
        final List<Mission> missions = new ArrayList<>();
        mUserDao.getMyMissions(new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                final ListView listView = rootView.findViewById(R.id.mission_list);
                for (DataSnapshot missionSnapShot : dataSnapshot.getChildren()) {
                    String missionId = missionSnapShot.getKey();
                    Log.d(TAG, missionId);
                    mMissionDao.getMission(missionId, new OnGetDataListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "find mission");
                            missions.add(dataSnapshot.getValue(Mission.class));
                            listView.setAdapter(new MyMissionsAdapter(getActivity(), missions));
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private class MyMissionsAdapter extends ArrayAdapter {

        private final Activity context;
        private final List<Mission> myMissions;

        public MyMissionsAdapter(Activity context, List<Mission> missions) {
            super(context, R.layout.fragment_my_missions, missions);
            this.context = context;
            this.myMissions = missions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.my_mission_single, null, true);
            Mission mission = myMissions.get(position);
            ImageView thumb = rowView.findViewById(R.id.my_mission_thumb);
            TextView createdTimeText = rowView.findViewById(R.id.my_mission_created_time);
            TextView statusText = rowView.findViewById(R.id.my_mission_status);

            DownloadImageTask task = new DownloadImageTask(thumb);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mission.getThumbURL());

            createdTimeText.setText("Created Time: " + mission.getCreatedTime());
            String status = mission.isCompleted() ? "Found" : "Not Found";
            Log.d(TAG, status);
            statusText.setText("Status: " + status);

            return rowView;
        }


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            return downloadImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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
    }

}
