package com.att.hackaroomie.fragments;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.att.hackaroomie.R;
import com.att.hackaroomie.adapter.MatchingAdapter;
import com.att.hackaroomie.fragments.base.BaseFragment;
import com.att.hackaroomie.widget.FloatingActionButton;

public class ParallaxFragment extends BaseFragment {

    private MatchingAdapter matchingAdapter;

    private FloatingActionButton mFab;

    private boolean mStarred;
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_matching, container, false);
        final ImageView image = (ImageView) v.findViewById(R.id.image);

        image.setImageResource(getArguments().getInt("image"));
        image.post(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                matrix.reset();

                float wv = image.getWidth();
                float hv = image.getHeight();

                float wi = image.getDrawable().getIntrinsicWidth();
                float hi = image.getDrawable().getIntrinsicHeight();

                float width = wv;
                float height = hv;

                if (wi / wv > hi / hv) {
                    matrix.setScale(hv / hi, hv / hi);
                    width = wi * hv / hi;
                } else {
                    matrix.setScale(wv / wi, wv / wi);
                    height= hi * wv / wi;
                }

                matrix.preTranslate((wv - width) / 2, (hv - height) / 2);
                image.setScaleType(ImageView.ScaleType.MATRIX);
                image.setImageMatrix(matrix);
            }
        });


        TextView text = (TextView)v.findViewById(R.id.name);
        text.setText(getArguments().getString("name"));

        //text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/sunshine.ttf"));

        // Sets the FloatingActionButton to a listener. Need to make it animate or give some kinda feedback
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        /*mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = !mStarred;
                final ImageView imageView = (ImageView) mFab.findViewById(R.id.fabIcon);




                    final int imageResId = isCheck
                            ? R.drawable.add_schedule_button_icon_checked
                            : R.drawable.add_schedule_button_icon_unchecked;

                    *//*if (imageView.getTag() != null) {
                        if (imageView.getTag() instanceof Animator) {
                            Animator anim = (Animator) imageView.getTag();
                            anim.end();
                            imageView.setAlpha(1f);
                        }
                    }*//*

                    if (isCheck) {
                        int duration = 200;

                        Animator outAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f);
                        outAnimator.setDuration(duration / 2);
                        outAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imageView.setImageResource(imageResId);
                            }
                        });

                        AnimatorSet inAnimator = new AnimatorSet();
                        outAnimator.setDuration(duration);
                        inAnimator.playTogether(
                                ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f),
                                ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0f, 1f),
                                ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0f, 1f)
                        );

                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(duration);
                        set.playSequentially(outAnimator, inAnimator);
                        set.start();
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(imageResId);
                            }
                        });
                    }
                }

        });*/

        return v;
    }

    public void setAdapter(MatchingAdapter adapter) {
        matchingAdapter = adapter;
    }
}