package com.example.client.ui.follows;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FollowFragmentAdapter extends FragmentPagerAdapter {
    private String[] leagueList=new String[]{"英超","意甲","西甲","德甲","中超"};
    public FollowFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new YingChaoFollowFragment();
        }else if(position==1){
            return new YiJiaFollowFragment();
        }else if(position==2){
            return new XiJiaFollowFragment();
        }else if (position==3){
            return new DeJiaFollowFragment();
        }else if(position==4){
            return new ZhongChaoFollowFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return leagueList.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return leagueList[position];
    }
}
