package com.mysterysuperhero.notebook.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by dmitri on 21.06.16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int numOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence[] titles, int numOfTabs) {
        super(fm);
        this.titles = titles;
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NotesFragment();
        } else {
            return new CategoriesFragment();
        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
