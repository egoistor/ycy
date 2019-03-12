package com.example.ycy.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;
    public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments){
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public int getCount() {
        return fragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }
}
