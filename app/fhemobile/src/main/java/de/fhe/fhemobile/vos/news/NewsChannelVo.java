package de.fhe.fhemobile.vos.news;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 03.03.15.
 */
public class NewsChannelVo {

    public NewsChannelVo() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public NewsItemVo[] getNewsItems() {
        return mNewsItems;
    }

    public void setNewsItems(NewsItemVo[] _newsItems) {
        mNewsItems = _newsItems;
    }

    @SerializedName("title")
    private String mTitle;

    @SerializedName("item")
    private NewsItemVo[] mNewsItems;
}
