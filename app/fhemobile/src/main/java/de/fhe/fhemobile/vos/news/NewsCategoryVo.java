package de.fhe.fhemobile.vos.news;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 13.02.14.
 */
public class NewsCategoryVo {

    public NewsCategoryVo() {
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @SerializedName("id")
    private Integer mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("url")
    private String mUrl;
}
