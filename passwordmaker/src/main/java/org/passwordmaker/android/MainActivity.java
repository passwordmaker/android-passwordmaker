package org.passwordmaker.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountManager;
import org.daveware.passwordmaker.AccountManagerListener;
import org.daveware.passwordmaker.SecureCharArray;
import org.passwordmaker.android.adapters.SubstringArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements AccountManagerListener {

    private static final String REPO_KEY_CURRENT_PROFILES = "currentProfile";
    private static final int MIN_PASSWORD_LEN_FOR_VERIFICATION_CODE = 8;

    private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
    private AccountManager accountManager;

    private static final int EDIT_FAVORITE  = 0x01;
    private static final int LIST_ACCOUNTS = 0x02;

    private TextView lblInputTimeout;
    private EditText txtInputTimeout;

    private ArrayAdapter<String> favoritesAdapter;
    private ArrayList<String> favoritesList = new ArrayList<String>();

    private void loadOldProfiles() {
        // load up the old profiles from the older version of the application
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = PwmApplication.getInstance().getAccountManager();
        setContentView(R.layout.activity_main);

        // this must be done before we do any loading of settings to make sure we get events
        accountManager.addListener(this);

        CheckBox chkSaveInputs = (CheckBox) findViewById(R.id.chkSaveInputs);
        chkSaveInputs.setOnCheckedChangeListener(onSaveInputCheckbox);
        txtInputTimeout = (EditText) findViewById(R.id.txtSaveInputTime);
        lblInputTimeout = (TextView) findViewById(R.id.lblSaveForLength);
        loadOldProfiles();
        loadAccountDatabase();
        createFavoritesList();


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
    private void refreshList() {
        Set<String> allthings = new HashSet<String>();
        allthings.addAll(PwmApplication.getInstance().getAccountManager().getFavoriteUrls());
        allthings.addAll(PwmApplication.getInstance().getAllAccountsUrls());
        favoritesList.clear();
        favoritesList.addAll(allthings);
        favoritesAdapter.notifyDataSetChanged();
    }
    private void createFavoritesList() {
        favoritesAdapter = new SubstringArrayAdapter(this, android.R.layout.simple_list_item_1, favoritesList);
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoritesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PwmApplication.getInstance().saveSettings(this);
    }

    private void loadDefaultValueForFields() {

    }

    private void loadAccountDatabase() {
        PwmApplication.getInstance().loadSettingsOnce(this);
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
        if (id == R.id.action_import_export) {
            showImportExport();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateSelectedProfileText() {
        Account account = accountManager.getAccountForInputText(getInputText());
        TextView text = (TextView) findViewById(R.id.lblCurrentProfile);
        String value = account.getName();
        if ( accountManager.isAutoSelectingAccount() ) {
            value += " (AutoSelected)";
        }
        text.setText(value);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCurrentProfile(String profileId) {
        accountManager.selectAccountById(profileId);
    }

    private String getInputPassword() {
        TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
        return masterPass.getText().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setInputPassword(String value, boolean requireMinLengthForVerificationCode) {
        TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
        masterPass.setText(value);
        updateVerificationCode(requireMinLengthForVerificationCode);
    }

    private String getInputText() {
        TextView inputText = (TextView) findViewById(R.id.txtInput);
        return inputText.getText().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
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
                updatePassword(false);

        }
    };

    private void updatePassword(boolean requireMinLength) {
        updateVerificationCode(requireMinLength);
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

    public void updateVerificationCode(boolean requireMin) {
        final String masterPassword = getInputPassword();
        try {
            if ( !requireMin || masterPassword.length() >= MIN_PASSWORD_LEN_FOR_VERIFICATION_CODE) {
                setVerificationCode(accountManager.getPwm().generateVerificationCode(new SecureCharArray(masterPassword)));
            } else {
                setVerificationCode("");
            }
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

    private void showFavorites() {
        Intent intent = new Intent(this, EditFavoritesActivity.class);
        startActivityForResult(intent, EDIT_FAVORITE);
    }

    private void showImportExport() {
        Intent intent = new Intent(this, ImportExportRdf.class);
        startActivity(intent);
    }

    private View.OnKeyListener mUpdatePasswordKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            updatePassword(true);
            return false;
        }
    };

    private View.OnClickListener mCopyButtonClick = new View.OnClickListener() {

        // This is suppressed because I still want to support older android phones
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            updatePassword(false);
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            TextView text = (TextView) findViewById(R.id.txtPassword);
            clipboard.setText(text.getText());
            Toast.makeText(MainActivity.this, "Copied password to the clipboard", Toast.LENGTH_SHORT).show();
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
