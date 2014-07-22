package org.passwordmaker.android.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.daveware.passwordmaker.AccountManager;
import org.jetbrains.annotations.NotNull;
import org.passwordmaker.android.PwmApplication;
import org.passwordmaker.android.R;

public class MasterPasswordPreference extends DialogPreference {
    private View dlgView;
    public MasterPasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_set_pwd_hash);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(@NotNull View view) {
        super.onBindDialogView(view);
        dlgView = view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            EditText password = (EditText)dlgView.findViewById(R.id.password);
            EditText confirmed = (EditText)dlgView.findViewById(R.id.confirm_password);
            if ( ! password.getText().toString().equals(confirmed.getText().toString())) {
                Toast.makeText(getContext(), "Password Mismatch", Toast.LENGTH_SHORT).show();
                return;
            }
            AccountManager accountManager = PwmApplication.getInstance().getAccountManager();
            if ( password.getText().length() == 0 ) {
                accountManager.disablePasswordHash();
                persistBoolean(false);
            } else {
                accountManager.setCurrentPasswordHashPassword(password.getText().toString());
                persistBoolean(true);
            }
            PwmApplication.getInstance().saveSettings(getContext());
        }
    }
}
