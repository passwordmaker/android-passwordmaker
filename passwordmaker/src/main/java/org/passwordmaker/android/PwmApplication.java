package org.passwordmaker.android;

import android.widget.ArrayAdapter;
import org.daveware.passwordmaker.AccountManager;
import org.passwordmaker.AccountManagerSamples;

import java.util.ArrayList;

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
 */
public class PwmApplication {
    private static PwmApplication sInstance;
    private AccountManager accountManager;


    public static PwmApplication getInstance() {
        // Lazy load the singleton on first use.
        if ( sInstance == null ) {
            sInstance = new PwmApplication();
        }
        return sInstance;
    }

    private PwmApplication() {
        accountManager = new AccountManager();
        AccountManagerSamples.addSamples(accountManager);
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }
}
