package edu.neu.madcourse.zhiyaojin.finalproject.project.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.zhiyaojin.finalproject.common.dao.BaseDao;
import edu.neu.madcourse.zhiyaojin.finalproject.common.listeners.OnGetDataListener;
import edu.neu.madcourse.zhiyaojin.finalproject.project.entities.Mission;

public class MissionDao extends BaseDao {

    private final static String TAG = "MissionDao";

    private final static String PROJ_PATH = "project";
    private final static String MISSION_PATH = "missions";
    private final static String COMPLETED = "completed";

    private DatabaseReference missionRef;

    public MissionDao() {
        super();
        missionRef = getRootRef().child(PROJ_PATH).child(MISSION_PATH);
    }

    public String addMission(Mission mission) {
        String missionId = missionRef.push().getKey();
        mission.setMissionId(missionId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String createdTime = sdf.format(new Date());
        mission.setCreatedTime(createdTime);
        missionRef.child(missionId).setValue(mission);
        return missionId;
    }

    public void getMission(String missionId, OnGetDataListener listener) {
        DatabaseReference ref = missionRef.child(missionId);
        readData(ref, listener);
    }

    public void getMissionsInCamera( double minLat, double maxLat, OnGetDataListener listener) {
        Query latQuery = missionRef.orderByChild("latitude").startAt(minLat).endAt(maxLat);
        latQuery.keepSynced(true);
        readData(latQuery, listener);
    }

    public void setMissionCompleted(String missionId, boolean isCompleted) {
        missionRef.child(missionId).child(COMPLETED).setValue(isCompleted);
    }

}
