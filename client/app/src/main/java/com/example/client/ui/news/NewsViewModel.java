package com.example.client.ui.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public NewsViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是新闻页面");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
