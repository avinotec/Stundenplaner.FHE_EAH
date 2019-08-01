package de.fhe.fhemobile.vos.news;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 01.02.14.
 */
public class NewsItemVo implements Parcelable {

    public NewsItemVo() {
    }

    public NewsItemVo(String _title) {
        mTitle       = _title;
        mLink        = "";
        mDescription = "Description";
        mEncoded     = "Encoded";
        mAuthor      = "Paul";
        mPubDate     = "Wed, 08 Apr 2015 08:15:00 +0200";
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String mLink) {
        this.mLink = mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getEncoded() {
        return mEncoded;
    }

    public void setEncoded(String mEncoded) {
        this.mEncoded = mEncoded;
    }

    public String[] getCategories() {
        return mCategories;
    }

    public void setCategories(String[] mCategories) {
        this.mCategories = mCategories;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getPubDate() {
        return mPubDate.substring(4, 22);
    }

    public void setPubDate(String mPubDate) {
        this.mPubDate = mPubDate;
    }


    @SerializedName("title")
    private String mTitle;

    @SerializedName("link")
    private String mLink;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("encoded")
    private String mEncoded;

    @SerializedName("category")
    private String[] mCategories;

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("pubDate")
    private String mPubDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mLink);
        dest.writeString(this.mDescription);
        dest.writeString(this.mEncoded);
        dest.writeStringArray(this.mCategories);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mPubDate);
    }

    private NewsItemVo(Parcel in) {
        this.mTitle = in.readString();
        this.mLink = in.readString();
        this.mDescription = in.readString();
        this.mEncoded = in.readString();
        this.mCategories = in.createStringArray();
        this.mAuthor = in.readString();
        this.mPubDate = in.readString();
    }

    public static final Parcelable.Creator<NewsItemVo> CREATOR = new Parcelable.Creator<NewsItemVo>() {
        public NewsItemVo createFromParcel(Parcel source) {
            return new NewsItemVo(source);
        }

        public NewsItemVo[] newArray(int size) {
            return new NewsItemVo[size];
        }
    };
}
