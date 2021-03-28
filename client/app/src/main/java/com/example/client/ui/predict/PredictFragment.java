package com.example.client.ui.predict;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.client.LoginActivity;
import com.example.client.MainActivity;
import com.example.client.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PredictFragment extends Fragment {
    private PredictViewModel predictViewModel;
    private SharedPreferences sharedPreferences;
    private TextView textView;
    private  Button loginBtn;
    private EditText homeTeam;
    private EditText awayTeam;
    private Button predictBtn;
    private TextView resultText;

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
            initView(root);
        }

        return root;
    }

    private void initView(View root) {
        homeTeam=(EditText)root.findViewById(R.id.home_team_predict);
        awayTeam=(EditText)root.findViewById(R.id.away_team_predict);
        predictBtn=(Button)root.findViewById(R.id.predict_btn);
        resultText=(TextView) root.findViewById(R.id.result);
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String home=homeTeam.getText().toString();
                String away=awayTeam.getText().toString();
                if (home.equals("")||away.equals("")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"请输入两支球队",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String URL="http://8.129.27.254:8000/predict?team1="+home+"&team2="+away;
                    Log.d("kwwl",URL);
                    OkHttpClient okHttpClient= MainActivity.okHttpClient;
                    Request request=new Request.Builder()
                            .get()
                            .url(URL)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d("kwwl","onFailure"+e.toString());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.d("kwwl","onResponse"+response.code());
                            String respon=response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (respon.equals("1")){
                                        resultText.setText("胜");
                                        resultText.setVisibility(View.VISIBLE);
                                    }else if (respon.equals("-1")){
                                        resultText.setText("负");
                                        resultText.setVisibility(View.VISIBLE);
                                    }else if (respon.equals("0")){
                                        resultText.setText("平");
                                        resultText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });


//                            try {
//                                JSONArray jsonArray=new JSONArray(respon);
//                                for (int i=0;i<jsonArray.length();i++){
//                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
//                                    respon=jsonObject.getString("result");
//                                    if (respon.equals("1")){
//                                        resultText.setText("胜");
//                                        resultText.setVisibility(View.VISIBLE);
//                                    }else if (respon.equals("-1")){
//                                        resultText.setText("负");
//                                        resultText.setVisibility(View.VISIBLE);
//                                    }else if (respon.equals("0")){
//                                        resultText.setText("平");
//                                        resultText.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                    });
                }
            }
        });
    }
}
