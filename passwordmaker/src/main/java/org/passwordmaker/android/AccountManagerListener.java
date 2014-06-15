package org.passwordmaker.android;

import org.daveware.passwordmaker.Account;

public interface AccountManagerListener {
    public void onSelectedProfileChange(Account newProfile);
}
