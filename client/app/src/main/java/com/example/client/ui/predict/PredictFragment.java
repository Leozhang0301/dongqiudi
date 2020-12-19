package com.example.client.ui.predict;

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

public class PredictFragment extends Fragment {
    private PredictViewModel predictViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        predictViewModel=new ViewModelProvider(this).get(PredictViewModel.class);
        View root=inflater.inflate(R.layout.fragment_predict,container,false);
        final TextView textView=root.findViewById(R.id.text_predict);
        predictViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
