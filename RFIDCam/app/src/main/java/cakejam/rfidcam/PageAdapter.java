package cakejam.rfidcam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by wleeper on 4/8/2017.
 */

public class PageAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;

    public PageAdapter(FragmentManager fragmentManager, int numTabs){
        super(fragmentManager);
        this.mNumOfTabs = numTabs;
    }
//    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
//        super(fm);
//        this.mNumOfTabs = NumOfTabs;
//    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EditFragment editTab = new EditFragment();
                return editTab;
            case 1:
                SwipeFragment swipeTab = new SwipeFragment();
                return swipeTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
