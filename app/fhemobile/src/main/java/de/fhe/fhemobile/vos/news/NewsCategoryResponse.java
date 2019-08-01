package de.fhe.fhemobile.vos.news;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 03.03.15.
 */
public class NewsCategoryResponse {

    public NewsCategoryResponse() {
    }

    public NewsCategoryVo[] getNewsCategories() {
        return mNewsCategories;
    }

    public void setNewsCategories(NewsCategoryVo[] _newsCategories) {
        mNewsCategories = _newsCategories;
    }

    @SerializedName("entries")
    private NewsCategoryVo[] mNewsCategories;
}
