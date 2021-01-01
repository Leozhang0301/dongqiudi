package com.example.client.ui.matches;

public class MatchItem {
    private String date;
    private String time;
    private String homeTeam;
    private String awayTeam;
    private String result;
    private String league;
    private String homeTeamIconURL;
    private String awayTeamIconURL;

    public MatchItem(String date, String time, String homeTeam, String awayTeam, String result, String league, String homeTeamIconURL, String awayTeamIconURL) {
        this.date = date;
        this.time = time;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.result = result;
        this.league = league;
        this.homeTeamIconURL = homeTeamIconURL;
        this.awayTeamIconURL = awayTeamIconURL;
    }

    public String getHomeTeamIconURL() {
        return homeTeamIconURL;
    }

    public void setHomeTeamIconURL(String homeTeamIconURL) {
        this.homeTeamIconURL = homeTeamIconURL;
    }

    public String getAwayTeamIconURL() {
        return awayTeamIconURL;
    }

    public void setAwayTeamIconURL(String awayTeamIconURL) {
        this.awayTeamIconURL = awayTeamIconURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }
}
