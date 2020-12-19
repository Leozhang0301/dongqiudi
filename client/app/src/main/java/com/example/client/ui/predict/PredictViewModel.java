package com.example.client.ui.predict;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PredictViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public PredictViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是预测页面");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
