package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;

public class TimelineActivity extends AppCompatActivity {
    FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mFragmentManager = getSupportFragmentManager();




        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = TimelineFragment.newInstance();
            mFragmentManager.beginTransaction().add(R.id.fragment_container,fragment,TimelineFragment.TAG).commit();
        }
        else{
            mFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment,TimelineFragment.TAG).commit();
        }
    }

}
