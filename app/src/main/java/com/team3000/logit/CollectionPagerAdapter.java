package com.team3000.logit;

import androidx.annotation.Nullable;
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
        switch (position) {
            case 0 :
                return new CollectionLogFragment(collectionName, "note");
            case 1 :
                return new CollectionLogFragment(collectionName, "task");
            case 2 :
                return new CollectionLogFragment(collectionName, "event");
            default:
                return null;
        }
        /* For debugging
        CollectionLogFragment fragment;

        switch (position) {
            case 0 :
                Log.i(TAG, "In note fragment");
                fragment = new CollectionLogFragment(collectionName, "note");
                break;
            case 1 :
                Log.i(TAG, "In task fragment");
                fragment =  new CollectionLogFragment(collectionName, "task");
                break;
            case 2 :
                Log.i(TAG, "In event fragment");
                fragment = new CollectionLogFragment(collectionName, "event");
                break;
            default:
                fragment = null;
        }

        return fragment;
        */
    }

    @Nullable
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
