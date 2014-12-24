package com.att.hackaroomie;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.att.hackaroomie.bluelist.UserItem;
import com.att.hackaroomie.fragments.WelcomeFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;

import android.content.Intent;

import static com.att.hackaroomie.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity {
    private static final String TAG = makeLogTag(MainActivity.class);

    private static final int SPLASH = 0;
    private static final int WELCOME = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS +1;
    private boolean isResumed = false;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private DisplayHandler mDisplay;

    private MenuItem settings;

    private static final String APP_ID = "applicationID";
    private static final String APP_SECRET = "applicationSecret";
    private static final String APP_ROUTE = "applicationRoute";
    private static final String PROPS_FILE = "bluelist.properties";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[WELCOME] = fm.findFragmentById(R.id.selectionFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        mDisplay = new DisplayHandler(this);

        // Setup Bluelist
        //setupIBMBlueList();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        final Toolbar toolbar = getActionBarToolbar();
        if(toolbar != null) {

            if(getCurrentFragmentIndex() == WELCOME)
                toolbar.setNavigationIcon(R.drawable.ic_launcher);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[WELCOME].isVisible()) {
            if (menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Take appropriate action for each action item click
        switch (id) {
            case R.id.action_settings:
                showFragment(SETTINGS, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(WELCOME, false);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);
        }
    }

    /**
     * Handles a state change based on facebook login
     * @param state
     */
    @Override
    protected void handleSessionStateChange(SessionState state){
        // Only make changes if the activity is visible
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                showFragment(WELCOME, false);
            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                showFragment(SPLASH, false);
            }
        }
    }



    @Override
    protected Fragment[] getActivityFragments(){
        return fragments;
    }

    /**
     * Sets the navigation icon for fragment
     */
    @Override
    protected void setNavigationIconForFragment(){
        final Toolbar toolbar = getActionBarToolbar();
        if(toolbar != null) {

            if(getCurrentFragmentIndex() == WELCOME)
                toolbar.setNavigationIcon(R.drawable.ic_launcher);
        }
    }


    /**
     * Pass the info that the user info has been fetched to the fragments that care
     */
    @Override
    protected void onUserInfoFetched(){
        // @TODO: Need a cleaner implementation than the fragment cast, perhaps.
        if(getCurrentFragmentIndex() == WELCOME){
            WelcomeFragment fragment = (WelcomeFragment)fragments[WELCOME];
            fragment.onUserInfoFetched();
        }
    }


    /**
     * Setup IBMBlueList initializations.
     */
    private void setupIBMBlueList(){
        // Read from properties file.
        Properties props = new java.util.Properties();
        Context context = getApplicationContext();
        try {
            AssetManager assetManager = context.getAssets();
            props.load(assetManager.open(PROPS_FILE));
            Log.i(TAG, "Found configuration file: " + PROPS_FILE);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "The bluelist.properties file was not found.", e);
        } catch (IOException e) {
            Log.e(TAG,
                    "The bluelist.properties file could not be read properly.", e);
        }
        // Initialize the IBM core backend-as-a-service.
        IBMBluemix.initialize(this, props.getProperty(APP_ID), props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
        // Initialize the IBM Data Service.
        IBMData.initializeService();
        // Register the Item Specialization.
        UserItem.registerSpecialization(UserItem.class);
    }



}
