package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.neu.madcourse.zhiyaojin.finalproject.R;
import edu.neu.madcourse.zhiyaojin.finalproject.project.fragments.MyMissionsFragment;

public class GameRecordsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_records);

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mission_records, new MyMissionsFragment());
        ft.commit();
    }

}
