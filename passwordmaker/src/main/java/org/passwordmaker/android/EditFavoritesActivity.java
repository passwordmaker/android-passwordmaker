package org.passwordmaker.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


public class EditFavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_favorites);
        setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));
        // Show the Up button in the action bar.
        setDisplayHomeAsUpEnabled();
    }

    protected EditFavoritesFragment getAccountListFragment() {
        return ((EditFavoritesFragment) getFragmentManager().findFragmentById(R.id.edit_favorites));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_favs_add) {
            newFavorite();
            return true;
        } else if ( id == android.R.id.home) {
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

    protected void addItem(String title) {
        getAccountListFragment().addItem(title);

    }

    private void newFavorite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editView = new EditText(this);
        editView.setLines(1);
        editView.setMinimumWidth(200);
        builder.setView(editView);
        builder.setPositiveButton(R.string.AddFavorite,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newFav = editView.getText().toString();
                        addItem(newFav);
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

    private void setDisplayHomeAsUpEnabled() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
