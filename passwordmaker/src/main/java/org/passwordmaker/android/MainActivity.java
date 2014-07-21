package org.passwordmaker.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.daveware.passwordmaker.*;
import org.passwordmaker.android.adapters.SubstringArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class MainActivity extends ActionBarActivity implements AccountManagerListener {
    private static final String REPO_KEY_SAVED_INPUT_UNTIL = "savedInputUnilt";
    private static final String REPO_KEY_SAVED_INPUT_PASSWORD = "savedInputPass";
    private static final String REPO_KEY_SAVED_INPUT_INPUTTEXT = "savedInputInputText";
    private static final int MIN_PASSWORD_LEN_FOR_VERIFICATION_CODE = 8;

    private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
    private AccountManager accountManager;

    private static final int EDIT_FAVORITE  = 0x01;
    private static final int LIST_ACCOUNTS = 0x02;
    private static final int SHOW_SETTINGS  = 0x04;
    private static final int UPDATE_VER_CODE = 0xccaabb;
    private static final int VER_CODE_DELAY = 600;
    private ImageButton btnClearSelectedProfile;

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

        loadOldProfiles();
        loadAccountDatabase();
        createFavoritesList();

        AutoCompleteTextView inputText = (AutoCompleteTextView) findViewById(R.id.txtInput);
        if (inputText != null) {
            inputText.addTextChangedListener(createUpdatePasswordKeyListener());
            inputText.setOnFocusChangeListener(mUpdatePasswordFocusListener);
            inputText.setAdapter(favoritesAdapter);
            inputText.setThreshold(1);
        }
        TextView text = (TextView) findViewById(R.id.txtMasterPass);
        if (text != null)
            text.addTextChangedListener(createUpdatePasswordKeyListener());
        if (text != null)
            text.setOnFocusChangeListener(mUpdatePasswordFocusListener);
        Button button = (Button) findViewById(R.id.btnCopy);
        if (button != null)
            button.setOnClickListener(mCopyButtonClick);

        btnClearSelectedProfile = (ImageButton)findViewById(R.id.btnClearSelected);
        btnClearSelectedProfile.setOnClickListener(mClearProfileButtonClick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == SHOW_SETTINGS ) {
            showUsernameBasedOnPreference();
            showPassStrengthBasedOnPreference();
            saveDefaultValuesForFields();
        }
        super.onActivityResult(requestCode, resultCode, data);

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
        showUsernameBasedOnPreference();
        showPassStrengthBasedOnPreference();
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
            if (isSaveInputEnabled()) {
                final int minutes = getSavedLengthInMinutes();
                final Calendar cal = Calendar.getInstance();
                long curTime = cal.getTimeInMillis();
                cal.add(Calendar.MINUTE, minutes);
                final long time = cal.getTimeInMillis();
                Log.i(LOG_TAG, "Current time:" + Long.toString(curTime) + ", Expire Time: " + Long.toString(time));
                prefs.putLong(REPO_KEY_SAVED_INPUT_UNTIL, time);
                prefs.putString(REPO_KEY_SAVED_INPUT_PASSWORD, getInputPassword());
                prefs.putString(REPO_KEY_SAVED_INPUT_INPUTTEXT, getInputText());
            } else {
                Log.i(LOG_TAG, "Saving input disabled, removing data");
                prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
                prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
                prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
            }
        } finally {
            prefs.commit();
        }
    }

    private boolean isSaveInputEnabled() {
        return getSavedLengthInMinutes() > 0;
    }

    private int getSavedLengthInMinutes() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int minutes = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_SAVED_LENGTH, "0")) / 60;
        Log.i(LOG_TAG, format("Minutes preference set to: %d", minutes));
        return minutes;

    }

    private long getTimeToSaveUntil() {
        return getPreferences(MODE_PRIVATE).getLong(REPO_KEY_SAVED_INPUT_UNTIL, -1);
    }

    private void loadDefaultValueForFields() {
        try {
            final int minutes = getSavedLengthInMinutes();
            final long time = getTimeToSaveUntil();
            if (time != -1 && minutes > 0) {
                Calendar cal = Calendar.getInstance();
                if (time > cal.getTimeInMillis()) {
                    final String savedPass = getPreferences(MODE_PRIVATE)
                            .getString(REPO_KEY_SAVED_INPUT_PASSWORD, "");
                    final String savedInputText = getDefaultInputText(true);
                    setInputPassword(savedPass);
                    setInputText(savedInputText);
                    updatePassword(false);
                    return;
                }
            }
            Log.i(LOG_TAG, "Save time expired, removing data");
            // expired clear from preferences
            final SharedPreferences.Editor prefs = getPreferences(Activity.MODE_PRIVATE)
                    .edit();
            prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
            prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
            prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
            prefs.commit();
            final String savedInputText = getDefaultInputText(false);
            setInputText(savedInputText);
            setInputPassword("");
            updatePassword(false);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not load default values", e);
            final SharedPreferences.Editor prefs = getPreferences(Activity.MODE_PRIVATE).edit();
            prefs.remove(SettingsActivity.KEY_SAVED_LENGTH);
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
        if ( id == R.id.action_display_settings ) {
            displaySettings();
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
            if ( btnClearSelectedProfile.getVisibility() != View.GONE )
                btnClearSelectedProfile.setVisibility(View.GONE);
        } else {
            if ( btnClearSelectedProfile.getVisibility() != View.VISIBLE )
                btnClearSelectedProfile.setVisibility(View.VISIBLE);
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
    private void setInputPassword(String value) {
        TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
        masterPass.setText(value);
    }

    private void showUsernameBasedOnPreference() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show = sharedPref.getBoolean(SettingsActivity.KEY_SHOW_USERNAME, false);
        if ( show )
            showUsernameField();
        else
            hideUsernameField();
    }

    private void showUsernameField() {
        TextView username = (TextView) findViewById(R.id.txtUsername);
        username.setVisibility(View.VISIBLE);
        username = (TextView)findViewById(R.id.lblUsername);
        username.setVisibility(View.VISIBLE);

    }

    private void hideUsernameField() {
        TextView username = (TextView) findViewById(R.id.txtUsername);
        username.setVisibility(View.GONE);
        username = (TextView)findViewById(R.id.lblUsername);
        username.setVisibility(View.GONE);
    }

    private void showPassStrengthBasedOnPreference() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show = sharedPref.getBoolean(SettingsActivity.KEY_SHOW_PASS_STRENGTH, false);
        if ( show )
            showPassStrengthField();
        else
            hidePassStrengthField();
    }


    private void showPassStrengthField() {
        View layPassStrength = findViewById(R.id.layPassStrength);
        layPassStrength.setVisibility(View.VISIBLE);
    }

    private void hidePassStrengthField() {
        View layPassStrength = findViewById(R.id.layPassStrength);
        layPassStrength.setVisibility(View.GONE);
    }

    private void setPassStrengthMeter(SecureCharArray pass) {
        View layPassStrength = findViewById(R.id.layPassStrength);
        if ( layPassStrength.getVisibility() == View.VISIBLE ) {
            double strength = PasswordMaker.calcPasswordStrength(pass);
            Log.i(LOG_TAG, format("Password Strength: %.4f", strength));
            setPassStrengthValue((int)strength);
        }
    }

    private void setPassStrengthValue(int value) {
        View layPassStrength = findViewById(R.id.layPassStrength);
        if ( layPassStrength.getVisibility() == View.VISIBLE ) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progPassStrength);
            TextView txtPassStrength = (TextView)findViewById(R.id.txtPassStrength);
            progressBar.setProgress(value);
            txtPassStrength.setText(Integer.toString(value));
        }
    }

    private void resetPassStrengthMeter() {
        setPassStrengthValue(0);
    }

    private void setUIAccountUsernameFromAccount() {
        TextView username = (TextView) findViewById(R.id.txtUsername);
        Account account = accountManager.getAccountForInputText(getInputText());
        username.setText(account.getUsername());
    }

    private void clearUIAccountUsername() {
        TextView username = (TextView) findViewById(R.id.txtUsername);
        username.setText("");
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
        final SecureCharArray masterPassword = new SecureCharArray(getInputPassword());
        final TextView outputPassword = (TextView) findViewById(R.id.txtPassword);
        if (!requireMinLength || masterPassword.length() >= MIN_PASSWORD_LEN_FOR_VERIFICATION_CODE ) {
            updateVerificationCode();
            updateSelectedProfileText();
            final String inputText = getInputText();
            try {
                if (accountManager.matchesPasswordHash(masterPassword)) {
                    SecureCharArray output = accountManager.generatePassword(masterPassword, inputText);
                    outputPassword.setText(output);
                    setPassStrengthMeter(output);
                    setUIAccountUsernameFromAccount();
                } else {
                    outputPassword.setText("Password Hash Mismatch");
                    clearUIAccountUsername();
                    resetPassStrengthMeter();
                }
            } finally {
                masterPassword.erase();
            }
        } else {
            outputPassword.setText("");
            setVerificationCode("");
            clearUIAccountUsername();
            clearUIAccountUsername();
            resetPassStrengthMeter();
            // if we have one already enqueue reset so that we don't keep on updating uselessly
            updateValidationCodeHandler.removeMessages(UPDATE_VER_CODE);
            updateValidationCodeHandler.sendEmptyMessageDelayed(UPDATE_VER_CODE, VER_CODE_DELAY);
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

    private void showFavorites() {
        Intent intent = new Intent(this, EditFavoritesActivity.class);
        startActivityForResult(intent, EDIT_FAVORITE);
    }

    private void showImportExport() {
        Intent intent = new Intent(this, ImportExportRdf.class);
        startActivity(intent);
    }

    private void displaySettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SHOW_SETTINGS);
    }

    private TextWatcher createUpdatePasswordKeyListener() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                updatePassword(true);
            }
        };
    }

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

    private View.OnClickListener mClearProfileButtonClick = new View.OnClickListener() {

        // This is suppressed because I still want to support older android phones
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            accountManager.clearSelectedAccount();
            updatePassword(true);
            Toast.makeText(MainActivity.this, "Cleared manually selected account", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler updateValidationCodeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                // don't give true here, since it will cause this handler to be called later
                updatePassword(false);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error updating verification code");
            }
            return true;
        }
    });
}
