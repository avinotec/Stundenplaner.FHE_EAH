package de.fhe.fhemobile;

import android.app.Application;
import androidx.annotation.StringRes;

import de.fhe.fhemobile.utils.feature.FeatureProvider;


/**
 * Created by paul on 22.01.14.
 */
public class Main extends Application {

    private static final String LOG_TAG = Main.class.getSimpleName();
    private static Application mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // load active features from xml
        FeatureProvider.loadFeatures(this);
    }

    public static String getSafeString(@StringRes int _ResId) {
        return mAppContext.getString(_ResId);
    }

    public static Application getAppContext() {
        return mAppContext;
    }
}
