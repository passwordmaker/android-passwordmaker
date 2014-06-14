package org.passwordmaker.testPasswordMaker.testpasswordmaker.passwordmaker;

import android.util.Log;
import org.daveware.passwordmaker.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountManager implements DatabaseListener {

    // http://stackoverflow.com/questions/1658702/how-do-i-make-a-class-extend-observable-when-it-has-extended-another-class-too
    private final CopyOnWriteArrayList<AccountManagerListener> listeners = new CopyOnWriteArrayList<AccountManagerListener>();
    private PasswordMaker pwm = new PasswordMaker();
    private Database pwmProfiles = new Database();
    private Account selectedProfile = null;
    private Account defaultProfile = null;
    private List<String> favoriteUrls = new ArrayList<String>();

    private String currentPasswordHash;
    private String passwordSalt;
    private boolean storePasswordHash;

    private static String LOG_TAG = "PasswordMakerProForAndroidActivity";

    private static Account makeDefaultAccount() {
        Account account = Account.makeDefaultAccount();
        account.setAlgorithm(AlgorithmType.MD5);
        account.setCharacterSet(CharacterSets.ALPHANUMERIC);
        return account;
    }

    public AccountManager() {
        // load database
        if ( pwmProfiles.findAccountById(Account.DEFAULT_ACCOUNT_URI) == null ) {
            try {
                pwmProfiles.addAccount(pwmProfiles.getRootAccount(), makeDefaultAccount());
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error creating a default account", e);
            }
        }
        defaultProfile = pwmProfiles.findAccountById(Account.DEFAULT_ACCOUNT_URI);
        if ( defaultProfile == null ) {
            defaultProfile = pwmProfiles.getRootAccount();
        }
    }

    public boolean isAutoSelectingAccount() {
        return selectedProfile == null;
    }

    public CharSequence generatePassword(CharSequence masterPassword, String inputText) {
        SecureCharArray securedMasterPassword;
        if ( ! (masterPassword instanceof SecureCharArray) ) {
            securedMasterPassword = new SecureCharArray(masterPassword.toString());
        } else {
            securedMasterPassword = (SecureCharArray)masterPassword;
        }
        SecureCharArray result = null;
        try {
            Account accountToUse = getAccountForInputText(inputText);
            Log.i(LOG_TAG, "Using " + inputText + " as input text and " + accountToUse.toDebugString());
            result = pwm.makePassword(securedMasterPassword, accountToUse, inputText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ( result != null ) {
            return result;
        }
        return "";
    }

    // Public for testing
    public Account getAccountForInputText(String inputText) {
        if ( selectedProfile != null ) return selectedProfile;
        Account account = pwmProfiles.findAccountByUrl(inputText);
        if ( account == null ) return getDefaultAccount();
        return account;
    }

    public void selectAccountById(String id) throws IllegalArgumentException {
        if ( id == null ) {
            clearSelectedAccount();
            return;
        }
        Account profile = pwmProfiles.findAccountById(id);
        if ( profile != null ) {
            selectedProfile = profile;

        } else {
            throw new IllegalArgumentException("Profile by the ID of " + id + " is not found.");
        }
    }

    private void clearSelectedAccount() {
        selectedProfile = null;
    }

    public void selectAccountByUrl(String url) throws IllegalArgumentException {
        Account profile = pwmProfiles.findAccountByUrl(url);
        if ( profile != null ) {
            selectedProfile = profile;
        } else {
            throw new IllegalArgumentException("No profile matching the url of " + url + " was not found.");
        }
    }

    public PasswordMaker getPwm() {
        return pwm;
    }

    public Database getPwmProfiles() {
        return pwmProfiles;
    }

    public Account getSelectedProfile() {
        return selectedProfile;
    }

    @Override
    public void accountAdded(Account parent, Account account) {

    }

    @Override
    public void accountRemoved(Account parent, Account account) {
        // The account for the selected profile was removed, so we should no longer reference it
        if ( account.equals(selectedProfile) ) {
            selectedProfile = null;
        }
    }

    @Override
    public void accountChanged(Account account) {

    }

    @Override
    public void dirtyStatusChanged(boolean b) {
        // may want to save this
    }

    public void addDatabaseListener(DatabaseListener listener) {
        pwmProfiles.addDatabaseListener(listener);
    }

    public void removeDatabaseListener(DatabaseListener listener)
    {
        pwmProfiles.removeDatabaseListener(listener);
    }

    public void addListener(AccountManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    public void removeListener(AccountManagerListener listener) {
        listeners.remove(listener);
    }

    public List<String> getFavoriteUrls() {
        return favoriteUrls;
    }

    public void addFavoriteUrl(String url) {
        favoriteUrls.add(url);
    }

    public void removeFavoriteUrl(String url) {
        favoriteUrls.remove(url);
    }

    public Account getDefaultAccount() {
        return defaultProfile;
    }

    public void setDefaultAccount(Account account) {
        if ( account == null ) {
            defaultProfile = pwmProfiles.getRootAccount();
            return;
        }
        if ( ! account.isDefault() )
            throw new IllegalArgumentException("Only accounts marked as the default account can be set as the default account.");
        defaultProfile = account;
    }

    public boolean matchesPasswordHash(String masterPassword) {
        if ( ! hasPasswordHash() ) {
            return true;
        }
        String testPassHash = null;
        try {
            SecureCharArray secureCharArray = pwm.makePassword(getPasswordSaltAsSecureCharArray(),
                    pwmProfiles.getRootAccount(), masterPassword);
            testPassHash = new String(secureCharArray.getData());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error validating master password hash", e);
        }
        return testPassHash == null || testPassHash.equals(getCurrentPasswordHash());
    }

    public String getCurrentPasswordHash() {
        return currentPasswordHash;
    }

    public void setCurrentPasswordHash(String currentPasswordHash, String salt) {
        this.currentPasswordHash = currentPasswordHash;
        this.passwordSalt = salt;
        setStorePasswordHash(true);
    }

    public String getPasswordSalt() {
        return this.passwordSalt;
    }

    public SecureCharArray getPasswordSaltAsSecureCharArray() {
        return new SecureCharArray(this.passwordSalt);
    }

    public boolean hasPasswordHash() {
        return shouldStorePasswordHash() && this.passwordSalt != null && this.passwordSalt.length() > 0 && this.currentPasswordHash != null && this.currentPasswordHash.length() > 0;
    }

    public boolean shouldStorePasswordHash() {
        return storePasswordHash;
    }

    public void setStorePasswordHash(boolean storePasswordHash) {
        this.storePasswordHash = storePasswordHash;
        if ( !storePasswordHash ) {
            this.passwordSalt = null;
            this.currentPasswordHash = null;
        }
    }

    public void disablePasswordHash() {
        setStorePasswordHash(false);
    }
}
