package org.passwordmaker;

import android.util.Log;
import junit.framework.TestCase;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AlgorithmType;
import org.daveware.passwordmaker.PasswordMaker;
import org.daveware.passwordmaker.SecureCharArray;
import org.passwordmaker.testPasswordMaker.testpasswordmaker.passwordmaker.AccountManager;

import static org.passwordmaker.TestUtils.saToString;

public class AccountManagerTest extends TestCase {
    public void testAccountManagerDefaultAccount() {
        AccountManager manager = new AccountManager();
        Log.i("PWM", "The fucking default account is: " + manager.getDefaultAccount().toDebugString());
        assertTrue(manager.getDefaultAccount().isDefault());
    }

    public void testAccountManagerDefaultAccountOnNonMatchingUrl() {
        AccountManager manager = new AccountManager();
        assertSame(manager.getDefaultAccount(), manager.getAccountForInputText("google.com"));
    }

    public void testDefaultAccountUseGivenUrl() throws Exception {
        AccountManager manager = new AccountManager();
        Account account =  manager.getDefaultAccount();
        PasswordMaker pwm = manager.getPwm();
        assertEquals("HRdgNiyh", saToString(pwm.makePassword(new SecureCharArray("happy"), account, "google.com")));
    }

    public void testAccountManagerUsesDefaultAccountForNonMatchingUrl() {
        AccountManager manager = new AccountManager();
        assertEquals("HRdgNiyh", saToString((SecureCharArray)manager.generatePassword("happy", "google.com")));
    }
}
