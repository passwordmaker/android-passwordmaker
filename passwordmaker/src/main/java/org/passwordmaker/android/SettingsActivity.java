package org.passwordmaker.android;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import org.passwordmaker.android.preferences.SettingsFragment;


public class SettingsActivity extends Activity {

    public static final String KEY_SHOW_USERNAME = "pref_showUsername";
    public static final String KEY_SAVED_LENGTH = "pref_saveInputs";
    public static final String KEY_MASTERPASSWORD_HASH = "pref_masterPasswordHash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the Up button in the action bar.
        setDisplayHomeAsUpEnabled();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private void setDisplayHomeAsUpEnabled() {
        // prevent the possible nullpointer if getActionBar returns null.
        ActionBar actionBar = getActionBar();
        if ( actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
