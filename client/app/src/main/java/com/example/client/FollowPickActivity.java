package com.example.client;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.client.ui.follows.FollowFragmentAdapter;
import com.example.client.ui.ranking.MyFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class FollowPickActivity extends AppCompatActivity {
    private TabLayout tabLayout;

    private ViewPager viewPager;
    private FollowFragmentAdapter myFragmentAdapter;

    private TabLayout.Tab yingchaoTab;
    private TabLayout.Tab yijiaTab;
    private TabLayout.Tab xijiaTab;
    private TabLayout.Tab dejiaTab;
    private TabLayout.Tab zhongchaoTab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_follow_pick);
        viewPager=findViewById(R.id.followViewpeger);
        tabLayout=findViewById(R.id.league_layout);

        myFragmentAdapter= new FollowFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        yingchaoTab=tabLayout.getTabAt(0);
        xijiaTab=tabLayout.getTabAt(2);
        yijiaTab=tabLayout.getTabAt(1);
        dejiaTab=tabLayout.getTabAt(3);
        zhongchaoTab=tabLayout.getTabAt(4);
    }
}
