package edu.neu.madcourse.zhiyaojin.finalproject.common.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.zhiyaojin.finalproject.common.listeners.OnGetDataListener;

public class BaseDao {

    private static FirebaseDatabase database;
    private DatabaseReference rootRef;

    public BaseDao() {
        rootRef = getInstance().getReference();
    }

    private static FirebaseDatabase getInstance() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    protected DatabaseReference getRootRef() {
        return rootRef;
    }

    protected void readData(DatabaseReference ref, final OnGetDataListener listener) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void readData(Query query, final OnGetDataListener listener) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void readChangingData(DatabaseReference ref, final OnGetDataListener listener) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
