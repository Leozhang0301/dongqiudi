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

import com.example.client.R;
import com.example.client.ui.news.NewsViewModel;

public class RankingFragment extends Fragment {
    private RankingViewModel rankingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rankingViewModel=new ViewModelProvider(this).get(RankingViewModel.class);
        View root=inflater.inflate(R.layout.fragment_ranking,container,false);
        final TextView textView=root.findViewById(R.id.text_ranking);
        rankingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
