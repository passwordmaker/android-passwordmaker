package org.passwordmaker.android;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import org.daveware.passwordmaker.Account;

import java.util.ArrayList;


/**
 * An activity representing a single Account detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link AccountListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link AccountDetailFragment}.
 */
public class AccountDetailActivity extends Activity {
    private static String LOG_TAG = "ADA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        String accId = getIntent().getStringExtra(AccountDetailFragment.ARG_ITEM_ID);

        // Show the Up button in the action bar.
        setDisplayHomeAsUpEnabled();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(AccountDetailFragment.ARG_ITEM_ID, accId);
            AccountDetailFragment fragment = new AccountDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.account_detail_container, fragment)
                    .commit();
        }
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
            Intent intent = new Intent(this, AccountListActivity.class);
            ArrayList<String> accStack = getIntent().getStringArrayListExtra(AccountListFragment.STATE_ACCOUNT_STACK);
            if ( accStack != null && !accStack.isEmpty() )
                intent.putStringArrayListExtra(AccountListFragment.STATE_ACCOUNT_STACK, accStack);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDisplayHomeAsUpEnabled() {
        // prevent the possible nullpointer if getActionBar returns null.
        ActionBar actionBar = getActionBar();
        if ( actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
