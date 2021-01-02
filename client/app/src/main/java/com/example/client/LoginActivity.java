package com.example.client;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.ui.ranking.DeJiaRankFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private Button regiBtn;
    private EditText userName;
    private EditText password;
    private String userNameText;
    private String passwordText;
    private String URL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        userName=(EditText) findViewById(R.id.user);
        password=(EditText)findViewById(R.id.pw);
        loginBtn=(Button)findViewById(R.id.login);
        regiBtn=(Button)findViewById(R.id.regist);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameText = userName.getText().toString();
                passwordText = password.getText().toString();
                URL="http://8.129.27.254:8000/checkuser?username="+userNameText+"&pwd="+passwordText;
                Log.d("kwwl", "URL:" + URL);
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(8000, TimeUnit.MILLISECONDS)
                        .build();
                Request request = new Request.Builder()
                        .get()
                        .url(URL)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("kwwl", "onFailure" + e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.d("kwwl", "onResponse" + response.code());
                        String respon = response.body().string();
                        Log.d("kwwl", "response content:" + respon);
                        if (respon.equals("用户不存在") ) {
                            showNormalDialog(2);

                        } else if (respon.equals("密码错误")) {
                            showNormalDialog(1);

                        } else if (respon.equals("密码正确")) {
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }
                });
            }
        });
        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    //提示框
    private void showNormalDialog(final int code){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("错误");
        //密码错误
        if(code==1){
            dialog.setMessage("密码错误");
        }
        //用户不存在
        else if(code==2){
            dialog.setMessage("用户不存在");
        }
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            EditText password=(EditText)findViewById(R.id.pw);
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code==1){
                    password.setText("");
                }else if (code==2){
                    Intent intent=new Intent();
                    intent.setClass(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

    }
}
