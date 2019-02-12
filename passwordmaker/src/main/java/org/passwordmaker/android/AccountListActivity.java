package org.passwordmaker.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.daveware.passwordmaker.Account;
import org.jetbrains.annotations.NotNull;


/**
 * An activity representing a list of Accounts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AccountDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AccountListFragment} and the item details
 * (if present) is a {@link AccountDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link AccountListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AccountListActivity extends AppCompatActivity
        implements AccountListFragment.Callbacks {


    private static final String LOG_TAG = Logtags.ACCOUNT_LIST_ACTIVITY.getTag();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));
        setDisplayHomeAsUpEnabled();

        if (findViewById(R.id.account_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            getAccountListFragment().setActivateOnItemClick();
        }
        // TODO: If exposing deep links into your app, handle intents here.
    }

    protected AccountListFragment getAccountListFragment() {
        return ((AccountListFragment) getFragmentManager().findFragmentById(R.id.fragment_account_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_account_add) {
            addNewAccount();
            return true;
        } else if ( id == R.id.action_account_folder_add ) {
            addNewFolder();
            return true;
        } else if (id == android.R.id.home) {
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

    private void addNewAccount(String accountName) {
        getAccountListFragment().createNewAccount(accountName);
    }

    private void addNewAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editView = new EditText(this);
        editView.setLines(1);
        editView.setMinimumWidth(200);
        builder.setView(editView);
        builder.setPositiveButton(R.string.AddProfile,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newProfile = editView.getText().toString();
                        addNewAccount(newProfile);
                    }
                });
        builder.setNegativeButton(R.string.Cancel, null);
        final AlertDialog alert = builder.create();
        editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alert.getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }

            }
        });
        builder.setCancelable(true);
        alert.show();
    }

    private void addNewFolder(String folderName) {
        getAccountListFragment().createNewFolder(folderName);
    }

    private void addNewFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editView = new EditText(this);
        editView.setLines(1);
        editView.setMinimumWidth(200);
        builder.setView(editView);
        builder.setPositiveButton(R.string.AddProfile,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newFolder = editView.getText().toString();
                        addNewFolder(newFolder);
                    }
                });
        builder.setNegativeButton(R.string.Cancel, null);
        final AlertDialog alert = builder.create();
        editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alert.getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }

            }
        });
        builder.setCancelable(true);
        alert.show();
    }

    /**
     * Callback method from {@link AccountListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemView(Account account) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(AccountDetailFragment.ARG_ITEM_ID, account.getId());
            AccountDetailFragment fragment = new AccountDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.account_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, AccountDetailActivity.class);
            detailIntent.putExtra(AccountDetailFragment.ARG_ITEM_ID, account.getId());
            getAccountListFragment().saveAccountListState(detailIntent);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onFolderSelected(Account account) {
        // do anything that we need to do here
    }

    @Override
    public void onItemManuallySelected(Account account) {
        PwmApplication.getInstance().getAccountManager().selectAccountById(account.getId());
        NavUtils.navigateUpFromSameTask(this);
        Toast.makeText(this, "Manually selected '" + account.getName() + "'",  Toast.LENGTH_SHORT).show();
    }

    private void setDisplayHomeAsUpEnabled() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
