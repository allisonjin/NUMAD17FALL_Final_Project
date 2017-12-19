package edu.neu.madcourse.zhiyaojin.finalproject.common.listeners;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface OnGetDataListener {

    public void onStart();
    public void onSuccess(DataSnapshot dataSnapshot);
    public void onFailed(DatabaseError databaseError);

}
