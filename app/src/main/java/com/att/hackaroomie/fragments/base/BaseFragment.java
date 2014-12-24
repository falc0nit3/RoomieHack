package com.att.hackaroomie.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.att.hackaroomie.util.ImageLoader;

import static com.att.hackaroomie.util.LogUtils.makeLogTag;

/**
 * Created by Rohith Ravindranath on 12/21/14.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = makeLogTag(BaseFragment.class);

    private ImageLoader mImageLoader;
    private Activity mActivity;

    public BaseFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
    }

    /**
     * Default Getter for ImageLoader
     * @return
     */
    protected ImageLoader getImageLoader(){
        return mImageLoader;
    }

    /**
     * Default getter for the activity that invoked this fragment
     * @return
     */
    protected Activity getFragmentActivity(){
        return mActivity;
    }

}
