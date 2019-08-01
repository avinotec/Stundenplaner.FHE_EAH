package de.fhe.fhemobile.vos.mensa;

import com.google.gson.annotations.SerializedName;


/**
 * Created by paul on 13.02.14.
 */
public class MensaChoiceItemVo {

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

    public String getUrlPath() {
        return mUrlPath;
    }

    public void setUrlPath(String mUrl) {
        this.mUrlPath = mUrl;
    }

    @SerializedName("id")
    private Integer mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("urlPath")
    private String mUrlPath;

}
