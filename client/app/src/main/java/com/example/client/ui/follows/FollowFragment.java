package com.example.client.ui.follows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.client.LoginActivity;
import com.example.client.R;
import com.example.client.UserManagerActivity;

public class FollowFragment extends Fragment {
    private TextView textView;
    private Button loginBtn;
    private SharedPreferences sharedPreferences;
    private TextView userNickName;
    private ImageButton imageButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=null;
        sharedPreferences=getActivity().getSharedPreferences("user-info", Context.MODE_PRIVATE);
        String isLogin=sharedPreferences.getString("user_name","none");
        //没有登陆
        if(isLogin.equals("none")){
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
            root=inflater.inflate(R.layout.fragment_follow,container,false);
            userNickName=(TextView)root.findViewById(R.id.user_nick_name);
            userNickName.setText(sharedPreferences.getString("user_name","none"));
            imageButton=(ImageButton) root.findViewById(R.id.user_icon);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), UserManagerActivity.class);
                    startActivity(intent);
                }
            });
        }

        return root;
    }
}
