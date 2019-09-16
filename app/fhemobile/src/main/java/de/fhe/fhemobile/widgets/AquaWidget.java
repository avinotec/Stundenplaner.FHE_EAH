package de.fhe.fhemobile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.TimeZone;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.CafeAquaResponse;
import hirondelle.date4j.DateTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Paul Cech on 01.04.15.
 */
public class AquaWidget extends RelativeLayout {

    public AquaWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        preInitialize();
    }

    public void update(boolean _forceUpdate) {

        if (_forceUpdate ||
            DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault()) - mLastTimeUpdated > UPDATE_INTERVALL) {

            if (!mStartedFetching) {
                mStartedFetching = true;
                NetworkHandler.getInstance().fetchCafeAquaStatus(mResponseCallback);
            }
        }
    }

    private void preInitialize() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_aqua, this, true);
        setOnClickListener(mClickListener);
    }

    private void postInitialize() {
        update(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStatusLabel = (ImageView) findViewById(R.id.aquaStatus);

        postInitialize();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        update(false);
    }



    private Callback<CafeAquaResponse> mResponseCallback = new Callback<CafeAquaResponse>() {
        @Override
        public void onResponse(Call<CafeAquaResponse> call, Response<CafeAquaResponse> response) {
            mStartedFetching = false;
            if (mStatusLabel != null && response.body() != null ) {
                mStatusLabel.setImageResource(response.body().isOpen() ? R.drawable.cafe_aqua_open : R.drawable.cafe_aqua_closed);
                mLastTimeUpdated = DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault());
            }
        }

        @Override
        public void onFailure(Call<CafeAquaResponse> call, Throwable t) {
            mStartedFetching = false;
        }
    };

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            update(true);
        }
    };

    private static final String LOG_TAG             = AquaWidget.class.getSimpleName();

    private static final long   UPDATE_INTERVALL    =  900000;

    private Context     mContext;

    private ImageView   mStatusLabel;

    private long        mLastTimeUpdated = 0;
    private boolean     mStartedFetching = false;

}
