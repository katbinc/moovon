package by.katbinc.moovon.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NavigationModel {
    private String hash;

    @SerializedName("navigation")
    private ArrayList<NavigationItemModel> navigationItems;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ArrayList<NavigationItemModel> getNavigationItems() {
        return navigationItems;
    }

    public void setNavigationItems(ArrayList<NavigationItemModel> navigationItems) {
        this.navigationItems = navigationItems;
    }
}
