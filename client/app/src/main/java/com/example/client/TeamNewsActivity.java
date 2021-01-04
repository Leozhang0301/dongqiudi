package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TeamNewsActivity extends AppCompatActivity {
    private Bundle bundle;
    private Intent intent;
    private TextView team;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_team_news);
        team=(TextView)findViewById(R.id.team_news_name);
        intent=getIntent();
        bundle=intent.getExtras();
        team.setText(bundle.getString("team"));
    }
}
