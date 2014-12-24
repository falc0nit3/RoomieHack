package com.att.hackaroomie.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.att.hackaroomie.MatchListingActivity;
import com.att.hackaroomie.fragments.base.BaseFragment;
import com.att.hackaroomie.util.ImageLoader;
import com.bumptech.glide.Glide;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import java.io.IOException;

import com.att.hackaroomie.MainActivity;
import com.att.hackaroomie.R;
import com.att.hackaroomie.model.User;
import com.gc.materialdesign.views.ButtonRectangle;

import static com.att.hackaroomie.util.LogUtils.makeLogTag;

public class WelcomeFragment extends BaseFragment {

    private static final String TAG = makeLogTag(WelcomeFragment.class);

    private MainActivity mMainActivity;
    private User mUser;

    private ProfilePictureView mProfilePictureView;
    private TextView mUserNameVIew;

    private static final int REAUTH_ACTIVITY_CODE = 100;

    private ImageView mCoverPicView;
    ImageLoader mImageLoader;

    private ButtonRectangle mGetStartedButton;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getFragmentActivity();

        mUser = mMainActivity.getLoggedInUser();

        mImageLoader = mMainActivity.getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Find the user's profile picture custom view
        mProfilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        mProfilePictureView.setCropped(true);

        // Find the user's name view
        mUserNameVIew = (TextView) view.findViewById(R.id.selection_user_name);
        mCoverPicView = (ImageView) view.findViewById(R.id.coverPic);

        mGetStartedButton = (ButtonRectangle) view.findViewById(R.id.beginMatching);

        mGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent matchingIntent = new Intent(mMainActivity, MatchListingActivity.class);
                startActivity(matchingIntent);
            }
        });

        return view;
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Refresh the user variable
            mUser = mMainActivity.getLoggedInUser();
        }
    }

    /**
     * Display the relavant User info
     */
    public void onUserInfoFetched(){
        Log.v(TAG, "Setting up basic details");

        // Profile Pic
        mProfilePictureView.setProfileId(mUser.getId());

        // User Name
        mUserNameVIew.setText(mUser.getDisplayName());

        // Cover Pic
        String userCoverPic = mUser.getCoverPhoto();
        if(!userCoverPic.isEmpty()) {
            Log.v(TAG, "Setting User Cover Photo from this url : " + userCoverPic);
            mImageLoader.loadImage(userCoverPic, mCoverPicView);

            /*Glide.with(getActivity())
                    .load(userCoverPic)
                    .centerCrop()
                    .crossFade()
                    .into(mCoverPicView);*/
        }
    }
}