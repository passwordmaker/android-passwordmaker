package org.passwordmaker.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountManager;
import org.daveware.passwordmaker.AccountManagerListener;
import org.daveware.passwordmaker.SecureCharArray;
import org.passwordmaker.android.adapters.SubstringArrayAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AccountManagerListener {

    private static final String REPO_KEY_PROFILES = "profiles";
    private static final String REPO_KEY_CURRENT_PROFILES = "currentProfile";
    private static final String REPO_KEY_SAVED_INPUTS = "savedInputs";
    private static final String REPO_KEY_SAVED_LENGTH = "savedLength";
    private static final String REPO_KEY_SAVED_INPUT_UNTIL = "savedInputUnilt";
    private static final String REPO_KEY_SAVED_INPUT_PASSWORD = "savedInputPass";
    private static final String REPO_KEY_SAVED_INPUT_INPUTTEXT = "savedInputInputText";

    private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
    private AccountManager accountManager;


    public static final String EXTRA_PROFILE = "";//= PasswordMakerEditProfile.EXTRA_PROFILE;
    private static final int EDIT_PROFILE = 0x04;
    private static final int EDIT_FAVORITE  = 0x08;
    private static final int LIST_ACCOUNTS = 0x10;

    private CheckBox chkSaveInputs;
    private TextView lblInputTimeout;
    private EditText txtInputTimeout;

    private ArrayAdapter<String> favoritesAdapter;

    private void loadOldProfiles() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = PwmApplication.getInstance().getAccountManager();
        setContentView(R.layout.activity_main);

        // this must be done before we do any loading of settings to make sure we get events
        accountManager.addListener(this);

        chkSaveInputs = (CheckBox) findViewById(R.id.chkSaveInputs);
        chkSaveInputs.setOnCheckedChangeListener(onSaveInputCheckbox);
        txtInputTimeout = (EditText) findViewById(R.id.txtSaveInputTime);
        lblInputTimeout = (TextView) findViewById(R.id.lblSaveForLength);
        loadOldProfiles();
        loadAccountDatabase();
        favoritesAdapter = new SubstringArrayAdapter(this, android.R.layout.simple_list_item_1,
                PwmApplication.getInstance().getAccountManager().getFavoriteUrls());


        String currentProfile = getPreferences(MODE_PRIVATE).getString(
                REPO_KEY_CURRENT_PROFILES, null);
        try {
            accountManager.selectAccountById(currentProfile);
        } catch (IllegalArgumentException e) {
            System.out.println("While loading settings: " + e.getMessage());
        }

        AutoCompleteTextView inputText = (AutoCompleteTextView) findViewById(R.id.txtInput);
        if (inputText != null) {
            inputText.setOnKeyListener(mUpdatePasswordKeyListener);
            inputText.setOnFocusChangeListener(mUpdatePasswordFocusListener);
            inputText.setAdapter(favoritesAdapter);
            inputText.setThreshold(1);
        }
        TextView text = (TextView) findViewById(R.id.txtMasterPass);
        if (text != null)
            text.setOnKeyListener(mUpdatePasswordKeyListener);
        if (text != null)
            text.setOnFocusChangeListener(mUpdatePasswordFocusListener);
        Button button = (Button) findViewById(R.id.btnCopy);
        if (button != null)
            button.setOnClickListener(mCopyButtonClick);

        loadDefaultValueForFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoritesAdapter.notifyDataSetChanged();
    }

    private void loadDefaultValueForFields() {

    }

    private void loadAccountDatabase() {

    }

    public void showProfiles() {
        Intent intent = new Intent(this, AccountListActivity.class);
        startActivityForResult(intent, LIST_ACCOUNTS);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_profiles) {
            showProfiles();
            return true;
        }
        if (id == R.id.action_favorites) {
            showFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSelectedProfileText() {
        Account account = accountManager.getAccountForInputText(getInputText());
        TextView text = (TextView) findViewById(R.id.lblCurrentProfile);
        String value = account.getName();;
        if ( accountManager.isAutoSelectingAccount() ) {
            value += " (AutoSelected)";
        }
        text.setText(value);
    }

    public void setCurrentProfile(String profileId) {
        accountManager.selectAccountById(profileId);
    }

    private String getInputPassword() {
        TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
        return masterPass.getText().toString();
    }

    private void setInputPassword(String value) {
        TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
        masterPass.setText(value);
        updateVerificationCode();
    }

    private String getInputText() {
        TextView inputText = (TextView) findViewById(R.id.txtInput);
        return inputText.getText().toString();
    }

    private void setInputText(String value) {
        Log.i(LOG_TAG, "Setting input text to \"" + value + "\"");
        TextView inputText = (TextView) findViewById(R.id.txtInput);
        inputText.setText(value);
    }

    @Override
    public void onSelectedProfileChange(Account newProfile) {
        TextView text = (TextView) findViewById(R.id.lblCurrentProfile);
        text.setText(newProfile.getName());
    }


    private View.OnFocusChangeListener mUpdatePasswordFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus)
                updatePassword();

        }
    };

    private void updatePassword() {
        updateVerificationCode();
        updateSelectedProfileText();
        TextView text = (TextView) findViewById(R.id.txtPassword);
        final String inputText = getInputText();
        final String masterPassword = getInputPassword();
        if (accountManager.matchesPasswordHash(masterPassword)) {
            CharSequence output = accountManager.generatePassword(masterPassword, inputText);
            text.setText(output);
        } else {
            text.setText("Password Hash Mismatch");
        }
    }

    public void updateVerificationCode() {
        final String masterPassword = getInputPassword();
        try {
            setVerificationCode(accountManager.getPwm().generateVerificationCode(new SecureCharArray(masterPassword)));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error generating verification code", e);
            setVerificationCode("ERROR");
        }
    }

    private void setVerificationCode(SecureCharArray code) {
        setVerificationCode(new String(code.getData()));
    }

    private void setVerificationCode(String code) {
        TextView verificationText = (TextView) findViewById(R.id.lblVerificationCode);
        verificationText.setText(code);
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
                        accountManager.addFavoriteUrl(
                                editView.getText().toString());
                        TextView inputText = (TextView) findViewById(R.id.txtInput);
                        inputText.setText(editView.getText());
                        updatePassword();
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

    private void selectFavorite() {
        final List<String> favs = new ArrayList<String>(accountManager.getFavoriteUrls());
        favs.add(getString(R.string.AddFavorite));
        favs.add(getString(R.string.EditFavorites));
        final CharSequence[] items = favs.toArray(new CharSequence[favs.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a Favorite");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item >= 0 && item < items.length - 2) {
                    TextView inputText = (TextView) findViewById(R.id.txtInput);
                    inputText.setText(items[item]);
                    updatePassword();
                } else if (item == items.length - 2) {
                    newFavorite();
                } else if (item == items.length - 1) {
                    showFavorites();
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showFavorites() {
        Intent intent = new Intent(this, EditFavoritesActivity.class);
        startActivityForResult(intent, EDIT_FAVORITE);
    }

    private View.OnKeyListener mUpdatePasswordKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            updatePassword();
            return false;
        }
    };

    private View.OnClickListener mCopyButtonClick = new View.OnClickListener() {

        // This is suppressed because I still want to support older android phones
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            updatePassword();
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            TextView text = (TextView) findViewById(R.id.txtPassword);
            clipboard.setText(text.getText());
        }
    };

    private View.OnClickListener mFavoritesClick = new View.OnClickListener() {

        public void onClick(View v) {
            if (accountManager.getFavoriteUrls().isEmpty())
                newFavorite();
            else
                selectFavorite();
        }
    };

    private CompoundButton.OnCheckedChangeListener onSaveInputCheckbox = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            final int visibility = isChecked ? View.VISIBLE : View.GONE;
            txtInputTimeout.setVisibility(visibility);
            lblInputTimeout.setVisibility(visibility);
        }

    };
}
