package com.att.hackaroomie;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.att.hackaroomie.adapter.MatchingAdapter;
import com.att.hackaroomie.bluelist.UserItem;
import com.att.hackaroomie.fragments.ParallaxFragment;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.List;

import bolts.Continuation;
import bolts.Task;

import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

public class MatchListingActivity extends BaseActivity {

    private static final String TAG = "MatchListingActivity";

    ViewPager mPager;
    MatchingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_matching);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setBackgroundColor(0xFF000000);

        // creating the parallaxTransformer, you only need to pass the id of the View (or ViewGroup) you want to do the parallax effect


        mPager.setPageTransformer(false, new ParallaxPagerTransformer(R.id.parallaxContent));
        mAdapter = new MatchingAdapter(this, getSupportFragmentManager());
        mAdapter.setPager(mPager);

        // FAKE DATA
        getFakeData();

    }

    private void getFakeData(){
        Bundle bNina = new Bundle();
        bNina.putInt("image", R.drawable.kristina);
        bNina.putString("name", "Nina");
        ParallaxFragment pfNina = new ParallaxFragment();
        pfNina.setArguments(bNina);

        Bundle bNiju = new Bundle();
        bNiju.putInt("image", R.drawable.megs);
        bNiju.putString("name", "Ninu Junior");
        ParallaxFragment pfNiju = new ParallaxFragment();
        pfNiju.setArguments(bNiju);

        Bundle bYuki = new Bundle();
        bYuki.putInt("image", R.drawable.dino);
        bYuki.putString("name", "Yuki");
        ParallaxFragment pfYuki = new ParallaxFragment();
        pfYuki.setArguments(bYuki);

        Bundle bKero = new Bundle();
        bKero.putInt("image", R.drawable.raymond);
        bKero.putString("name", "Kero");
        ParallaxFragment pfKero = new ParallaxFragment();
        pfKero.setArguments(bKero);

        mAdapter.add(pfNina);
        mAdapter.add(pfNiju);
        mAdapter.add(pfYuki);
        mAdapter.add(pfKero);
        mPager.setAdapter(mAdapter);
    }

    /**
     * Refreshes itemList from data service.
     *
     * An IBMQuery is used to find all the list items.
     */
    public void listItems() {
        try {
            IBMQuery<UserItem> query = IBMQuery.queryForClass(UserItem.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<UserItem>, Void>() {

                @Override
                public Void then(Task<List<UserItem>> task) throws Exception {
                    final List<UserItem> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()){
                        Log.e(TAG, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(TAG, "Exception : " + task.getError().getMessage());
                    }


                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        /*itemList.clear();
                        for(IBMDataObject item:objects) {
                            itemList.add((UserItem) item);
                        }
                        sortItems(itemList);
                        lvArrayAdapter.notifyDataSetChanged();*/
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);

        }  catch (IBMDataException error) {
            Log.e(TAG, "Exception : " + error.getMessage());
        }
    }

}
