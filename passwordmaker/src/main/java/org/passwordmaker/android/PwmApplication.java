package org.passwordmaker.android;

import android.content.Context;
import android.util.Log;
import com.google.common.collect.Lists;
import org.daveware.passwordmaker.*;
import org.passwordmaker.AccountManagerSamples;

import java.io.*;
import java.security.Security;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * The page http://developer.android.com/reference/android/app/Application.html
 * suggest that we shouldn't extend from Application unless we need too, so lets give this a whirl and see how far I can
 * get without needing to extend from Application.  Right now, I don't need to know anything else about the application
 * lifestyle.
 *
 * This should only be used from the application's UI thread.  As both this class, and AccountManager isn't thread safe.
 * E.g. any execution point in the application that came from an on::Event:: (e.g. onCreate, onButtonClick, etc).  Should
 * be very careful not to use this from another thread.  Use a method to get an event on the UI thread if need to read/modify
 * this data.
 *
 * See: Activity.runOnUiThread(Runnable)
 *      View.post(Runnable)
 *      View.postDelayed(Runnable, long)
 * on how to get events to the UI thread from a non-UI thread.  Really you should read:
 * http://developer.android.com/guide/components/processes-and-threads.html on better examples, for example use of the
 * AsyncTask ( http://developer.android.com/reference/android/os/AsyncTask.html ) is probably better use of a background
 * task, with something that needs to update something on the UI thread.
 *
 *
 * The reason why this class is lazily loaded, is to ensure that we are created after the android system is setup.  Eg.
 * the first use of this should be from the Main Activity's onCreate() (or later).
 *
 * XXX - This class needs some refactoring already.
 *
 */
public class PwmApplication {

    private static final String LOG_TAG = Logtags.PWM_APPLICATION.getTag();
    private static final String PROFILE_DB_FILE = "profile_database.rdf";

    private static PwmApplication sInstance;
    private boolean firstTimeLoading = true;
    private final AccountManager accountManager;


    public static PwmApplication getInstance() {
        // Lazy load the singleton on first use.
        if ( sInstance == null ) {
            sInstance = new PwmApplication();
        }
        return sInstance;
    }

    private PwmApplication() {
        accountManager = new AccountManager();
        PasswordMaker.setDefaultCryptoProvider("SC");
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        AccountManagerSamples.addSamples(accountManager);
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void saveSettings(Context context) {
        String toWrite = serializeSettings();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(PROFILE_DB_FILE, Context.MODE_PRIVATE);
            fos.write(toWrite.getBytes());
            Log.i(LOG_TAG, "Saved application settings");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Unable to close profile_database.rdf after writing", e);
                }
            }
        }
    }

    protected void loadSettings(Context context) {

        LoadResults results = loadClassic(context);
        if ( results == null ) results = loadFromRDF(context);
        // this only happens if we successfully loaded up the db
        if ( results != null ) {
            Log.i(LOG_TAG, "Loaded application settings");
            accountManager.getPwmProfiles().swapAccounts(results.database);
            loadFavoritesFromGlobalSettings();
            loadMasterPasswordHashFromGlobalSettings();
            if ( results.favorites != null )  accountManager.addFavoriteUrls(results.favorites);
        }
    }

    protected String serializeSettings() {
        AndroidRDFDatabaseWriter writer = new AndroidRDFDatabaseWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            updateFavoritesGlobalSettings();
            updateMasterPasswordHash();
            writer.write(os, accountManager.getPwmProfiles());
            return os.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeSettingsWithOutMasterPassword() {
        SecureCharArray hash = cloneArray(accountManager.getCurrentPasswordHash());
        String salt = nullToEmpty(accountManager.getPasswordSalt());
        try {
            accountManager.disablePasswordHash();
            return serializeSettings();
        } finally {
            try {
                accountManager.replaceCurrentPasswordHash(hash, salt);
            } catch (Exception ignored) {
            }
        }
    }

    protected SecureCharArray cloneArray(SecureCharArray chars) {
        if ( chars != null )
            return new SecureCharArray(chars);
        else
            return new SecureCharArray();
    }

    protected Database deserializedSettings(InputStream is, boolean convertBuggyAlgo, List<IncompatibleException> errors) {
        RDFDatabaseReader reader = new RDFDatabaseReader();
        if ( convertBuggyAlgo ) reader.setBuggyAlgoUseAction(DatabaseReader.BuggyAlgoAction.CONVERT);
        try {
            return reader.read(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            errors.addAll(reader.getIncompatibleAccounts());
        }
    }

    protected Database deserializedSettings(InputStream is, boolean convertBuggyAlgo) {
        return deserializedSettings(is, convertBuggyAlgo, new ArrayList<IncompatibleException>());
    }

    public Database deserializedSettings(String serialized, boolean convertBuggyAlgo, List<IncompatibleException> errors) {
        ByteArrayInputStream is = new ByteArrayInputStream(serialized.getBytes());
        return deserializedSettings(is, convertBuggyAlgo, errors);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Database deserializedSettings(String serialized, boolean convertBuggyAlgo) {
        ByteArrayInputStream is = new ByteArrayInputStream(serialized.getBytes());
        return deserializedSettings(is, convertBuggyAlgo);
    }

    protected void updateFavoritesGlobalSettings() {
        String encodedUrls = accountManager.encodeFavoriteUrls();
        accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.FAVORITES, encodedUrls);
    }

    public void loadFavoritesFromGlobalSettings() {
        String encodedUrls = accountManager.getPwmProfiles().getGlobalSetting(AndroidGlobalSettings.FAVORITES);
        accountManager.decodeFavoritesUrls(encodedUrls, true);
    }

    protected void updateMasterPasswordHash() {
        SecureCharArray pwdSCA = accountManager.getCurrentPasswordHash();
        String pwdHash = pwdSCA != null ? new String(accountManager.getCurrentPasswordHash().getData()) : "";
        String pwdSalt = accountManager.getPasswordSalt();
        boolean pwdStore = accountManager.shouldStorePasswordHash();
        if ( pwdStore && pwdHash.length() > 0 && pwdSalt.length() > 0) {
            accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_HASH, pwdHash);
            accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_SALT, pwdSalt);
        } else {
            // ensure its cleared out
            accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_HASH, "");
            accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_SALT, "");
        }
        accountManager.getPwmProfiles().setGlobalSetting(AndroidGlobalSettings.STORE_MASTER_PASSWORD_HASH,
                Boolean.toString(pwdStore));
    }

    protected void loadMasterPasswordHashFromGlobalSettings() {
        boolean pwdStore = Boolean.parseBoolean(
                accountManager.getPwmProfiles().getGlobalSetting(AndroidGlobalSettings.STORE_MASTER_PASSWORD_HASH));
        if ( !pwdStore ) {
            accountManager.disablePasswordHash();
        } else {
            String pwdHash = accountManager.getPwmProfiles().getGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_HASH);
            String pwdSalt = accountManager.getPwmProfiles().getGlobalSetting(AndroidGlobalSettings.MASTER_PASSWORD_SALT);
            accountManager.replaceCurrentPasswordHash(new SecureCharArray(pwdHash), pwdSalt);
        }
    }

    public void loadSettingsOnce(Context context) {
        if ( firstTimeLoading ) {
            loadSettings(context);
            firstTimeLoading = false;
        }
    }


    public Set<String> getAllAccountsUrls() {
        Set<String> result = new HashSet<String>();
        getAllSubAccountsUrls(accountManager.getPwmProfiles().getRootAccount(), result);
        return result;
    }

    private static void getAllSubAccountsUrls(Account account, Set<String> urls) {
        if ( isNotEmpty(account.getUrl()) ) {
            urls.add(account.getUrl());
        }
        if ( account.hasChildren() ) {
            for (Account child : account.getChildren() ) {
                getAllSubAccountsUrls(child, urls);
            }
        }
    }

    private static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    // This class shouldn't be required, this has bad code smell
    private static class LoadResults {
        private final Database database;
        private final Collection<String> favorites;

        private LoadResults(Database database, Collection<String> favorites) {
            this.database = database;
            this.favorites = favorites;
        }
    }

    private LoadResults loadClassic(Context context) {
        try {
            Collection<String> favorites = Lists.newArrayList();
            Database db = ClassicSettingsImporter.importIntoDatabase(context, favorites);
            if ( db != null)
                return new LoadResults(db, favorites);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error importing classic version database", e);
        }
        return null;
    }

    private LoadResults loadFromRDF(Context context) {
        File f = new File(context.getFilesDir(), PROFILE_DB_FILE);
        if ( ! f.exists() )
            return null;
        if ( ! f.canRead() ) {
            Log.e(LOG_TAG, "Can not read settings file: " + f.getAbsolutePath());
            return null;
        }
        InputStream fis = null;
        try {
            fis = context.openFileInput(PROFILE_DB_FILE);
            return new LoadResults(deserializedSettings(fis, false), null);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Unable to read profile", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Unable to close profile_database.rdf after reading", e);
                }
            }
        }
        return null;
    }

}
