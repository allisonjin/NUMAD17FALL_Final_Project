package edu.neu.madcourse.zhiyaojin.finalproject.project.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.zhiyaojin.finalproject.common.dao.BaseDao;
import edu.neu.madcourse.zhiyaojin.finalproject.common.listeners.OnGetDataListener;
import edu.neu.madcourse.zhiyaojin.finalproject.project.utils.CommonUtils;

public class UserDao extends BaseDao {

    private final static String PROJ_PATH = "project";
    private final static String USERS_PATH = "users";
    private final static String USERNAME_PATH = "username";
    private final static String MISSIONS_CREATED_PATH = "missionsCreated";
    private final static String MISSIONS_ATTEMPTED_PATH = "missionsAttempted";
    private final static String MISSIONS_COMPLETED_PATH = "missionsCompleted";
    private final static String ATTEMPT_TIMES = "attemptTimes";

    private DatabaseReference usersRef;
    private DatabaseReference userRef;

    public UserDao(String email) {
        super();
        usersRef = getRootRef().child(PROJ_PATH).child(USERS_PATH);
        String emailPath = CommonUtils.getEmailPath(email);
        userRef = usersRef.child(emailPath);
    }

    public void addUser(String username) {
        userRef.child(USERNAME_PATH).setValue(username);
    }

    public void addUserCreatedMission(String missionId) {
        userRef.child(MISSIONS_CREATED_PATH).child(missionId).setValue(true);
    }

    public void getUsernameByEmail(String email, OnGetDataListener listener) {
        String emailPath = CommonUtils.getEmailPath(email);
        DatabaseReference ref = usersRef.child(emailPath).child(USERNAME_PATH);
        readData(ref, listener);
    }

    public void getAttemptTimes(String missionId, OnGetDataListener listener) {
        DatabaseReference missionRef = userRef.child(MISSIONS_ATTEMPTED_PATH)
                .child(missionId).child(ATTEMPT_TIMES);
        readChangingData(missionRef, listener);
    }

    public void attemptIncrement(String missionId) {
        final DatabaseReference attemptTimeRef = userRef.child(MISSIONS_ATTEMPTED_PATH)
                .child(missionId).child(ATTEMPT_TIMES);
        attemptTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue();
                long attemptTime = data != null ? (long)data : 0;
                attemptTimeRef.setValue(attemptTime + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeAttemptedMission(String missionId) {
        DatabaseReference missionRef = userRef.child(MISSIONS_ATTEMPTED_PATH).child(missionId);
        missionRef.removeValue();
    }

    public void addCompletedMission(String missionId) {
        userRef.child(MISSIONS_COMPLETED_PATH).child(missionId).setValue(true);
    }

    public void getMyMissions(OnGetDataListener listener) {
        DatabaseReference missionRef = userRef.child(MISSIONS_CREATED_PATH);
        readData(missionRef, listener);
    }

}
