package com.example.client.ui.matches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MatchesViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MatchesViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是比赛页面");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
