package de.fhe.fhemobile;

import android.app.Application;
import android.content.Context;

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


    //MS 201908 Multidex apk introduced
    // Or if you do override the Application class but it's not possible to change the base class,
    // then you can instead override the attachBaseContext() method and call MultiDex.install(this) to enable multidex:
    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        androidx.multidex.MultiDex.install(this);
    }

}
