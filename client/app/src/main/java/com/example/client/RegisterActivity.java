package com.example.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button regiBtn;
    private Button resetBtn;
    private EditText accont;
    private EditText pwd;
    private EditText repwd;
    private EditText name;
    private String accontText;
    private String pwdText;
    private String repwdText;
    private String nameText;
    private String URL;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        regiBtn=(Button)findViewById(R.id.ready);
        resetBtn=(Button)findViewById(R.id.reset);
        accont=(EditText)findViewById(R.id.user);
        pwd=(EditText)findViewById(R.id.pw);
        repwd=(EditText)findViewById(R.id.check);
        name=(EditText)findViewById(R.id.name);
        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accontText=accont.getText().toString();
                pwdText=pwd.getText().toString();
                repwdText=repwd.getText().toString();
                nameText=name.getText().toString();
                if(!repwdText.equals(pwdText)){
                    showNomalDialog(1);

                }else {
                    URL="http://8.129.27.254:8000/register?accont="+accontText+"&pwd="+pwdText+"&name="+nameText;
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
                            if (respon.equals("账户已存在") ) {
                                showNomalDialog(2);

                            } else if (respon.equals("用户名已使用")) {
                                showNomalDialog(4);

                            } else if (respon.equals("成功")) {
                                showNomalDialog(3);
                                SharedPreferences sp=getSharedPreferences("user-info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putString("user_name",nameText);
                                editor.commit();


                            }
                        }
                    });
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accont.setText("");
                pwd.setText("");
                repwd.setText("");
                name.setText("");
            }
        });
    }

    public void showNomalDialog(final int code){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        if(code==1) {
            dialog.setTitle("错误");
            dialog.setMessage("密码不一致");
        }
        else if(code==2){
            dialog.setTitle("错误");
            dialog.setMessage("用户已存在");
        }
        else if(code==3){
            dialog.setTitle("成功");
            dialog.setMessage("注册成功");
        }
        else if(code==4){
            dialog.setTitle("错误");
            dialog.setMessage("名称已经被使用");
        }
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code==1){
                    repwd.setText("");
                    name.setText("");
                }else if (code==2){
                    accont.setText("");
                    pwd.setText("");
                    repwd.setText("");
                    name.setText("");
                }else if (code==3){
//                    Intent intent=new Intent();
//                    intent.setClass(RegisterActivity.this,MainActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if (code==4){
                    name.setText("");
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
