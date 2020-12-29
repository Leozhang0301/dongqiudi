package com.example.client.ui.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.client.R;
import com.example.client.ui.news.NewsViewModel;
import com.google.android.material.tabs.TabLayout;

public class RankingFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentAdapter myFragmentAdapter;

    private TabLayout.Tab yingchaoTab;
    private TabLayout.Tab yijiaTab;
    private TabLayout.Tab xijiaTab;
    private TabLayout.Tab dejiaTab;
    private TabLayout.Tab zhongchaoTab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_ranking,container,false);

        viewPager=root.findViewById(R.id.ViewPager);
        myFragmentAdapter=new MyFragmentAdapter(getFragmentManager());
        viewPager.setAdapter(myFragmentAdapter);

        tabLayout=root.findViewById(R.id.TabLayout);
        tabLayout.setupWithViewPager(viewPager);

        yingchaoTab=tabLayout.getTabAt(0);
        xijiaTab=tabLayout.getTabAt(2);
        yijiaTab=tabLayout.getTabAt(1);
        dejiaTab=tabLayout.getTabAt(3);
        zhongchaoTab=tabLayout.getTabAt(4);

        return root;
    }


}
