package com.example.client.ui.follows;

public class TeamFollowItem {
    private String teamName;
    private String iconURL;

    public TeamFollowItem(String teamName, String iconURL) {
        this.teamName = teamName;
        this.iconURL = iconURL;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
