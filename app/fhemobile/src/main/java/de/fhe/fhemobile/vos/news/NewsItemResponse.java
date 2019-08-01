package de.fhe.fhemobile.vos.news;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 03.03.15.
 */
public class NewsItemResponse {

    public NewsItemResponse() {
    }

    public NewsChannelVo getChannel() {
        return mChannel;
    }

    public void setChannel(NewsChannelVo _channel) {
        mChannel = _channel;
    }

    @SerializedName("channel")
    private NewsChannelVo mChannel;
}
