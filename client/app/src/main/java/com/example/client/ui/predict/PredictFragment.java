package com.example.client.ui.predict;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.client.LoginActivity;
import com.example.client.R;

public class PredictFragment extends Fragment {
    private PredictViewModel predictViewModel;
    private SharedPreferences sharedPreferences;
    private TextView textView;
    private  Button loginBtn;
    private EditText homeTeam;
    private EditText awayTeam;
    private Button predictBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        predictViewModel=new ViewModelProvider(this).get(PredictViewModel.class);
//        View root=inflater.inflate(R.layout.fragment_predict,container,false);
//        final TextView textView=root.findViewById(R.id.text_predict);
//        predictViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
        View root=null;
        sharedPreferences=getActivity().getSharedPreferences("user-info", Context.MODE_PRIVATE);
        String isLogin=sharedPreferences.getString("user_name","none");
        //没有登陆
        if (isLogin.equals("none")){
            root=inflater.inflate(R.layout.not_logined,container,false);
            textView=(TextView)root.findViewById(R.id.text_follow);
            loginBtn=(Button)root.findViewById(R.id.login_btn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            textView.setText("还没有登录哦~");
        }else {
            root=inflater.inflate(R.layout.fragment_predict,container,false);

        }
        initView(root);
        return root;
    }

    private void initView(View root) {
        homeTeam=(EditText)root.findViewById(R.id.home_team_predict);
        awayTeam=(EditText)root.findViewById(R.id.away_team_predict);
        predictBtn=(Button)root.findViewById(R.id.predict_btn);
    }
}
