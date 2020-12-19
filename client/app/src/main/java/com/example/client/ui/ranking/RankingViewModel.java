package com.example.client.ui.ranking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RankingViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public RankingViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是积分页面");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
