package com.att.hackaroomie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v4.app.Fragment;

import com.att.hackaroomie.model.User;
import com.att.hackaroomie.util.ImageLoader;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.att.hackaroomie.util.LogUtils.makeLogTag;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer, login and authentication, Action Bar tweaks, amongst others.
 * All our activities need to inherit from this.
 */
public abstract class BaseActivity extends ActionBarActivity {
    private static final String TAG = makeLogTag(BaseActivity.class);

    protected User mUser;
    private Toolbar mToolbar;
    ImageLoader mImageLoader;
    private Fragment[] mFragments;
    private int mCurrentFragmentIndex;
    private CharSequence mTitle;
    private boolean isResumed = false;
    private static final int REAUTH_ACTIVITY_CODE = 100;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Create our logged in user
        mUser = new User();
        mTitle = getTitle();

        mImageLoader = new ImageLoader(this);

        // Check for an open session
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        if(mToolbar != null) {
            mToolbar.setTitle(mTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public final void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {

            if (state.isOpened()) {
                // If the session state is open:
                if(session != null){
                    makeMeRequest(session);
                }
            } else if (state.isClosed()) {
                // If the session state is closed:
            }
        }

        handleSessionStateChange(state);
    }

    /**
     * Child classes should perform their respective functions
     * @param state
     */
    protected void handleSessionStateChange(SessionState state){
    }

    /**
     * Returns the current User
     * @return
     */
    public User getLoggedInUser(){
        return mUser;
    }

    /**
     * Sets the action bar
     * @param toolbar
     * @param handleBackground
     */
    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        mToolbar = (Toolbar) toolbar;

        if(mToolbar != null){
            final ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeButtonEnabled(true);
            }
        }
    }

    /**
     * Simple getter for the actionbar Toolbar
     * @return
     */
    protected Toolbar getActionBarToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    /**
     * Default Getter for ImageLoader
     * @return
     */
    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    protected void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        mFragments = getActivityFragments();

        for (int i = 0; i < mFragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(mFragments[i]);
                mCurrentFragmentIndex = i;
            } else {
                transaction.hide(mFragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

        // Set the Navigation Icon based on the fragment
        setNavigationIconForFragment();
    }

    /**
     * Let the inheriting activity classes return the correct fragments
     * @return
     */
    protected Fragment[] getActivityFragments(){
        return new Fragment[0];
    }

    /**
     * Let the child activity handle what to do with the set user info
     */
    protected void onUserInfoFetched(){
    }

    /**
     * Sets the navigation icon for fragment
     */
    protected void setNavigationIconForFragment(){
    }

    /**
     * Return the index of the current Fragment being displayed. Each child activity can manage a setup of fragments that could be switched based on different
     * @return
     */
    protected int getCurrentFragmentIndex(){
        return mCurrentFragmentIndex;
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {

                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                mUser.setFacebookUser(user);

                                mUser.setId(user.getId());
                                mUser.setDisplayName(user.getName());
                                mUser.setBirthDate(user.getBirthday());

                                List<String> permissions = session.getPermissions();

                                // Look into the batch requests to prevent multiple round abouts for fetches.
                                fetchCoverPic(session);
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();


    }

    /**
     * Make a request using facebook graphobject to fetch the user cover pic
     * @param session
     */
    private void fetchCoverPic(final Session session){
        Bundle params = new Bundle();
        params.putString("fields", "cover");

        String requestId = "4";     // 4 is the unique id for Mark Zuckerberg, ha. Question is who is 1, 2 & 3.

        requestId = mUser.getFacebookUser().getId();

        Request newReq = new Request(session, requestId, params, HttpMethod.GET, new Request.Callback() {
            public void onCompleted(Response response) {
                GraphObject graphObject = response.getGraphObject();
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Log.e("Error", error.getErrorMessage());
                }
                else{
                    String coverPicUrl = graphObject.getPropertyAs("cover", GraphObject.class ).getProperty("source").toString();

                    Log.v(TAG, "Cover Pic is : " + coverPicUrl);

                    mUser.setCoverPhoto(coverPicUrl);

                    // Relay the fetched information downward for it to be handled
                    onUserInfoFetched();
                }
            }
        });

        newReq.executeAsync();
    }

    /**
     * Fetch the Friends of the current user post login
     */
    private void makeFriendsRequest() {
        Request myFriendsRequest = Request.newMyFriendsRequest(Session.getActiveSession(),
                new Request.GraphUserListCallback() {

                    @Override
                    public void onCompleted(List<GraphUser> users, Response response) {
                        if (response.getError() == null) {
                            // Handle response
                        }

                    }

                });

        // Add birthday to the list of info to get.
        Bundle requestParams = myFriendsRequest.getParameters();
        requestParams.putString("fields", "name,birthday");
        myFriendsRequest.setParameters(requestParams);
        myFriendsRequest.executeAsync();
    }

    public static GraphObject getPropertyGraphObject(GraphObject graphObject, String property) {
        if (graphObject == null) {
            return null;
        }
        return graphObject.getPropertyAs(property, GraphObject.class);
    }
}