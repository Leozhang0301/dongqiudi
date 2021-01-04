package com.example.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.LruCache;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    public static OkHttpClient okHttpClient;
    public static LruCache<String, BitmapDrawable> mImageCache;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

//        //网络代理
//        okHttpClient=new OkHttpClient.Builder()
//                .connectTimeout(8000, TimeUnit.MILLISECONDS)
//                .build();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_news, R.id.navigation_matches, R.id.navigation_follows,R.id.navigation_predict, R.id.navigation_ranking)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //sharedpreferences全局变量
        sharedPreferences=getSharedPreferences("user-info", Context.MODE_PRIVATE);

        //Lrucache全局变量
        //设置缓存大小为最大缓存的1/8
        int maxCache=(int)Runtime.getRuntime().maxMemory();
        int cacheSize=maxCache/8;
        mImageCache=new LruCache<String, BitmapDrawable>(cacheSize){
            //重写缓存大小计算公式
            //以图片大小计算缓存
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };

        //okHttpClient全局变量
        okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .build();
    }

}