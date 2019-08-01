package de.fhe.fhemobile.utils;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import de.fhe.fhemobile.Main;

/**
 * Created by paul on 04.03.15.
 */
public class Utils {

    //getStringResource-----------------------------------------------------------------------------

    /**
     * Retrieves a resource string.
     *
     * @param _variableName Resource name of the wanted string
     * @return String of the Resource value
     */
    public static String getStringResource(String _variableName) {
        if (_variableName == null || _variableName.isEmpty()) {
            return _variableName;
        }

        try {
            return Main.getAppContext().getString(getResourceId(_variableName, "string"));
        } catch (Exception e) {
            e.printStackTrace();

            //fallback if not found
            return _variableName;
        }
    }

    //getResourceId---------------------------------------------------------------------------------

    /**
     * Returns the resource ID based on resource name.
     *
     * @param _variableName Resource name identifier
     * @param _resourcename Resource type
     * @return Resource ID
     */
    public static int getResourceId(String _variableName, String _resourcename) {
        try {
            return Main.getAppContext().getResources().getIdentifier(_variableName, _resourcename, Main.getAppContext().getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * hides the current soft keyboard, if it is open.
     */
    public static void hideKeyboard(AppCompatActivity _activity) {
        InputMethodManager inputManager = (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (_activity.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(_activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            inputManager.hideSoftInputFromWindow(null, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showToast(@StringRes int _ResId) {
        showToast(Main.getAppContext(), _ResId);
    }

    public static void showToast(Context _Context, @StringRes int _ResId) {
        Toast.makeText(_Context, _ResId,
                Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String _Text) {
        showToast(Main.getAppContext(), _Text);
    }

    public static void showToast(Context _Context, String _Text) {
        Toast.makeText(_Context, _Text,
                Toast.LENGTH_SHORT).show();
    }

    
}
