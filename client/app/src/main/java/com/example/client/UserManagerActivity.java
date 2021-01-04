package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserManagerActivity extends AppCompatActivity {

    private Button logOffbtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_manager);
        logOffbtn=(Button)findViewById(R.id.logoff);
        logOffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("user-info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.clear();
                editor.commit();

                Intent intent=new Intent();
                intent.setClass(UserManagerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
