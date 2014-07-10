package org.passwordmaker.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.google.common.base.Preconditions;


/**
 * An activity representing a single PatternData detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PatternDataListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PatternDataDetailFragment}.
 */
public class PatternDataDetailActivity extends Activity {

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patterndata_detail);

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
            accountId = getIntent().getStringExtra(PatternDataDetailFragment.ARG_ITEM_ID);
            Preconditions.checkNotNull(accountId, "%s was not set in PatternDatDetailActivity: %s",
                    PatternDataDetailFragment.ARG_ITEM_ID, accountId);
            arguments.putString(PatternDataDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(PatternDataDetailFragment.ARG_ITEM_ID));
            arguments.putBoolean(PatternDataDetailFragment.ARG_TWO_PANE_MODE, false);
            arguments.putInt(PatternDataDetailFragment.ARG_PATTERN_POSITION,
                    getIntent().getIntExtra(PatternDataDetailFragment.ARG_PATTERN_POSITION, 0));
            PatternDataDetailFragment fragment = new PatternDataDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.patterndata_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDisplayHomeAsUpEnabled() {
        // prevent the possible nullpointer if getActionBar returns null.
        ActionBar actionBar = getActionBar();
        if ( actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void navigateUp() {

        Intent intent = new Intent(this, PatternDataListActivity.class);
        intent.putExtra(PatternDataListFragment.ARG_ACCOUNT_ID, accountId);
        NavUtils.navigateUpTo(this, intent);
    }
}
