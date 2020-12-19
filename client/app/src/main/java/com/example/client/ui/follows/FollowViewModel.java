package com.example.client.ui.follows;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FollowViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public FollowViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是关注页面");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
