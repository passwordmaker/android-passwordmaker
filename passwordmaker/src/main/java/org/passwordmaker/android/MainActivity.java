package org.passwordmaker.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements AccountManagerListener {

    private static final String REPO_KEY_SAVED_INPUTS = "savedInputs";
    private static final String REPO_KEY_SAVED_LENGTH = "savedLength";
    private static final String REPO_KEY_SAVED_INPUT_UNTIL = "savedInputUnilt";
    private static final String REPO_KEY_SAVED_INPUT_PASSWORD = "savedInputPass";
    private static final String REPO_KEY_SAVED_INPUT_INPUTTEXT = "savedInputInputText";

    private static final String REPO_KEY_CURRENT_PROFILES = "currentProfile";
    private static final int MIN_PASSWORD_LEN_FOR_VERIFICATION_CODE = 8;

    private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
    private AccountManager accountManager;

    private static final int EDIT_FAVORITE  = 0x01;
    private static final int LIST_ACCOUNTS = 0x02;

    private TextView lblInputTimeout;
    private EditText txtInputTimeout;
    private CheckBox chkSaveInputs;

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

        chkSaveInputs = (CheckBox) findViewById(R.id.chkSaveInputs);
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
        loadDefaultValueForFields();
        favoritesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveDefaultValuesForFields();
        PwmApplication.getInstance().saveSettings(this);
    }

    private void saveDefaultValuesForFields() {
        final SharedPreferences.Editor prefs = getPreferences(Activity.MODE_PRIVATE).edit();
        try {
            prefs.putBoolean(REPO_KEY_SAVED_INPUTS, chkSaveInputs.isChecked());
            if (chkSaveInputs.isChecked()) {
                String strMin = txtInputTimeout.getText().toString();
                final int minutes = (strMin == null || strMin.length() < 0) ? 5
                        : Integer.parseInt(strMin);
                final Calendar cal = Calendar.getInstance();
                long curTime = cal.getTimeInMillis();
                cal.add(Calendar.MINUTE, minutes);
                final long time = cal.getTimeInMillis();
                Log.i(LOG_TAG, "Current time:" + Long.toString(curTime) + ", Expire Time: " + Long.toString(time));
                prefs.putInt(REPO_KEY_SAVED_LENGTH, minutes);
                prefs.putLong(REPO_KEY_SAVED_INPUT_UNTIL, time);
                prefs.putString(REPO_KEY_SAVED_INPUT_PASSWORD,
                        getInputPassword());
                prefs.putString(REPO_KEY_SAVED_INPUT_INPUTTEXT, getInputText());
            } else {
                prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
                prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
                prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
            }
        } finally {
            prefs.commit();
        }
    }

    private void loadDefaultValueForFields() {
        try {
            final int minutes = getPreferences(MODE_PRIVATE).getInt(
                    REPO_KEY_SAVED_LENGTH, 5);
            txtInputTimeout.setText(Integer.toString(minutes));
            chkSaveInputs.setChecked(getPreferences(MODE_PRIVATE).getBoolean(
                    REPO_KEY_SAVED_INPUTS, false));
            final long time = getPreferences(MODE_PRIVATE).getLong(
                    REPO_KEY_SAVED_INPUT_UNTIL, -1);
            if (time != -1 && chkSaveInputs.isChecked()) {
                Calendar cal = Calendar.getInstance();
                if (time > cal.getTimeInMillis()) {
                    final String savedPass = getPreferences(MODE_PRIVATE)
                            .getString(REPO_KEY_SAVED_INPUT_PASSWORD, "");
                    final String savedInputText = getDefaultInputText(true);
                    setInputPassword(savedPass, false);
                    setInputText(savedInputText);
                    updatePassword(false);
                    return;
                }
            }
            // expired clear from preferences
            final SharedPreferences.Editor prefs = getPreferences(Activity.MODE_PRIVATE)
                    .edit();
            prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
            prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
            prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
            prefs.commit();
            final String savedInputText = getDefaultInputText(false);
            setInputText(savedInputText);
            updatePassword(false);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not load default values", e);
            final SharedPreferences.Editor prefs = getPreferences(Activity.MODE_PRIVATE).edit();
            prefs.remove(REPO_KEY_SAVED_LENGTH);
            prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
            prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
            prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
            prefs.commit();
        }
    }

    private String getDefaultInputText(boolean readFromSettings) {
        Intent intent = getIntent();
        final String webPageUrl;
        if ( intent != null ) {
            if ( intent.getAction().equals("android.intent.action.SEND") ) {
                webPageUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            } else {
                if ( readFromSettings )
                    webPageUrl = getPreferences(MODE_PRIVATE).getString(REPO_KEY_SAVED_INPUT_INPUTTEXT, "");
                else
                    webPageUrl = "";
            }
        } else {
            if ( readFromSettings )
                webPageUrl = getPreferences(MODE_PRIVATE).getString(REPO_KEY_SAVED_INPUT_INPUTTEXT, "");
            else {
                webPageUrl = "";
            }
        }
        return webPageUrl;
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
        if ( id == R.id.action_set_master_password_hash ) {
            setMasterPasswordHash();
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
        final SecureCharArray masterPassword = new SecureCharArray(getInputPassword());
        try {
            if (accountManager.matchesPasswordHash(masterPassword)) {
                CharSequence output = accountManager.generatePassword(masterPassword, inputText);
                text.setText(output);
            } else {
                text.setText("Password Hash Mismatch");
            }
        } finally {
            masterPassword.erase();
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

    private void setMasterPasswordHash() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = inflater.inflate(R.layout.dialog_set_pwd_hash, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.Save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText password = (EditText)dialogView.findViewById(R.id.password);
                        EditText confirmed = (EditText)dialogView.findViewById(R.id.confirm_password);
                        if ( ! password.getText().toString().equals(confirmed.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ( password.getText().length() == 0 ) {
                            accountManager.disablePasswordHash();
                        } else {
                            accountManager.setCurrentPasswordHashPassword(password.getText().toString());
                        }
                        PwmApplication.getInstance().updateMasterPasswordHash();
                        updatePassword(true);
                    }
                });
        builder.setNegativeButton(R.string.Cancel, null);
        final AlertDialog alert = builder.create();
        builder.setCancelable(true);
        alert.show();
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
            saveDefaultValuesForFields();
        }

    };
}
