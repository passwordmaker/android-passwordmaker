package org.passwordmaker.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import org.daveware.passwordmaker.AccountPatternData;


/**
 * An activity representing a list of PatternData. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PatternDataDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PatternDataListFragment} and the item details
 * (if present) is a {@link PatternDataDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PatternDataListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PatternDataListActivity extends Activity
        implements PatternDataListFragment.Callbacks {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = "PDLA";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patterndata_list);
        // Show the Up button in the action bar.
        setDisplayHomeAsUpEnabled();

        if (findViewById(R.id.patterndata_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            getListFragment().setActivateOnItemClick();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        accountId = getIntent().getStringExtra(PatternDataListFragment.ARG_ACCOUNT_ID);
        getListFragment().setAccountId(accountId);
    }

    protected PatternDataListFragment getListFragment() {
        return ((PatternDataListFragment) getFragmentManager()
                .findFragmentById(R.id.patterndata_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pattern_data_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pattern_add) {
            getListFragment().createNewPattern();
            return true;
        } else if (id == android.R.id.home) {
            nagivateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link PatternDataListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int position, AccountPatternData patternData) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PatternDataDetailFragment.ARG_ITEM_ID, accountId);
            arguments.putBoolean(PatternDataDetailFragment.ARG_TWO_PANE_MODE, mTwoPane);
            arguments.putInt(PatternDataDetailFragment.ARG_PATTERN_POSITION, position);
            PatternDataDetailFragment fragment = new PatternDataDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.patterndata_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PatternDataDetailActivity.class);
            detailIntent.putExtra(PatternDataDetailFragment.ARG_ITEM_ID, accountId);
            detailIntent.putExtra(PatternDataDetailFragment.ARG_TWO_PANE_MODE, mTwoPane);
            detailIntent.putExtra(PatternDataDetailFragment.ARG_PATTERN_POSITION, position);
            startActivity(detailIntent);
        }
    }

    private void nagivateUp() {
        Intent intent;
        if ( mTwoPane ) {
            intent = new Intent(this, AccountListActivity.class);

        } else {
            intent = new Intent(this, AccountDetailActivity.class);
        }
        if (accountId != null) {
            intent.putExtra(AccountDetailFragment.ARG_ITEM_ID, accountId);
        }
        NavUtils.navigateUpTo(this, intent);
    }

    private void setDisplayHomeAsUpEnabled() {
        // prevent the possible nullpointer if getActionBar returns null.
        ActionBar actionBar = getActionBar();
        if ( actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
