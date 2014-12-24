package com.att.hackaroomie;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Rohith Ravindranath on 12/21/14.
 */
public class DisplayHandler {

    private final ActionBarActivity mActivity;

    public DisplayHandler(ActionBarActivity activity){
        mActivity = activity;
    }

    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    public boolean popEntireFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            fm.popBackStack();
        }
        return backStackCount > 0;
    }

    private void showFragmentFromDrawer(Fragment fragment, int replaceFragmentId) {
        popEntireFragmentBackStack();

        /*if(replaceFragmentId == -1)
            replaceFragmentId = R.id.fragment_main;

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(replaceFragmentId, fragment)
                .commit();*/
    }

    private void showFragment(Fragment fragment) {
        /*mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();*/
    }
}
