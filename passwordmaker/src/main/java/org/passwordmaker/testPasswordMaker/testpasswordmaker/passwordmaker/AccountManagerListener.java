package org.passwordmaker.testPasswordMaker.testpasswordmaker.passwordmaker;

import org.daveware.passwordmaker.Account;

public interface AccountManagerListener {
    public void onSelectedProfileChange(Account newProfile);
}
