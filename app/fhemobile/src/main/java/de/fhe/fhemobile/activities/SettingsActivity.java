package de.fhe.fhemobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.mensa.MensaSettingsFragment;
import de.fhe.fhemobile.fragments.news.NewsCategoriesFragment;
import de.fhe.fhemobile.utils.feature.FeatureId;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.activity_settings);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mSettingsId = intent.getIntExtra(EXTRA_SETTINGS_ID, -1);
        }

        loadFragments(mSettingsId);
    }

    public void loadFragments(Integer _Id) {
        switch(_Id) {
            case FeatureId.NEWS:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, NewsCategoriesFragment.newInstance())
                        .commit();
                break;
            case FeatureId.MENSA:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, MensaSettingsFragment.newInstance())
                        .commit();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SETTINGS_ID, mSettingsId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mSettingsId = savedInstanceState.getInt(STATE_SETTINGS_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public static final String  EXTRA_SETTINGS_ID  = "extraSettingsId";

    private static final String STATE_SETTINGS_ID  = "settingsId";

    private Integer mSettingsId;

}
