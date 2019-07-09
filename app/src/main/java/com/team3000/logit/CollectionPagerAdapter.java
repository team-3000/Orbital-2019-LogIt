package com.team3000.logit;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CollectionPagerAdapter extends FragmentPagerAdapter {
    private String collectionName;

    public CollectionPagerAdapter(FragmentManager fm, String collectionName) {
        super(fm);
        this.collectionName = collectionName;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("CollectionPagerAdapter", "In getItem");

        Bundle bundle = new Bundle();
        bundle.putString("collectionName", collectionName);

        switch (position) {
            case 0 :
                bundle.putString("logType", "note");
                break;
            case 1 :
                bundle.putString("logType", "task");
                break;
            case 2 :
                bundle.putString("logType", "event");
                break;
            default:
        }

        CollectionLogFragment fragment = new CollectionLogFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0 :
                return "Notes";
            case 1 :
                return "Tasks";
            case 2 :
                return "Events";
            default :
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
