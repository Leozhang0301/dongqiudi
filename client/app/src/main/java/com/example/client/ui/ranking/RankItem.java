package com.example.client.ui.ranking;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;

public class RankItem {
    private String paiming;
    private String getIconURL;
    private String qiudui;
    private String changci;
    private String sheng;
    private String ping;
    private String fu;
    private String jinqiu;
    private String shiqiu;
    private String jifen;

    public RankItem(String paiming, String team_icon, String qiudui, String changci, String sheng, String ping, String fu, String jinqiu, String shiqiu, String jifen) {
        this.paiming = paiming;
        this.getIconURL=team_icon;
        this.qiudui = qiudui;
        this.changci = changci;
        this.sheng = sheng;
        this.ping = ping;
        this.fu = fu;
        this.jinqiu = jinqiu;
        this.shiqiu = shiqiu;
        this.jifen = jifen;
    }

    public String getPaiming() {
        return paiming;
    }

    public void setPaiming(String paiming) {
        this.paiming = paiming;
    }

    public String getGetIconURL() {
        return getIconURL;
    }

    public void setGetIconURL(String getIconURL) {
        this.getIconURL = getIconURL;
    }

    public String getQiudui() {
        return qiudui;
    }

    public void setQiudui(String qiudui) {
        this.qiudui = qiudui;
    }

    public String getChangci() {
        return changci;
    }

    public void setChangci(String changci) {
        this.changci = changci;
    }

    public String getSheng() {
        return sheng;
    }

    public void setSheng(String sheng) {
        this.sheng = sheng;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getFu() {
        return fu;
    }

    public void setFu(String fu) {
        this.fu = fu;
    }

    public String getJinqiu() {
        return jinqiu;
    }

    public void setJinqiu(String jinqiu) {
        this.jinqiu = jinqiu;
    }

    public String getShiqiu() {
        return shiqiu;
    }

    public void setShiqiu(String shiqiu) {
        this.shiqiu = shiqiu;
    }

    public String getJifen() {
        return jifen;
    }

    public void setJifen(String jifen) {
        this.jifen = jifen;
    }


}

