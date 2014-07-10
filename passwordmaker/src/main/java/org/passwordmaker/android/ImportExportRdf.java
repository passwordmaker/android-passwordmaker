package org.passwordmaker.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.IncompatibleException;
import org.daveware.passwordmaker.RDFDatabaseReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ImportExportRdf extends Activity {
    private static final String LOG_TAG = "ImpExPort";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export_rdf);
        getImportButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImportClick();
            }
        });
        getExportButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExportClick();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void onExportClick() {
        try {
            String str = PwmApplication.getInstance().serializeSettings();
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(str);
            Toast.makeText(this, "Exported profiles to clipboard", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error exporting database", e);
            Toast.makeText(this, "Error exporting to RDF", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("deprecation")
    private void onImportClick() {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (!clipboard.hasText()) {
            Toast.makeText(this, "No text in clipboard to export", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String str = clipboard.getText().toString();
            List<IncompatibleException> errors = new ArrayList<IncompatibleException>();
            Database db = PwmApplication.getInstance().deserializeSettings(str, convertIsChecked(), errors);
            PwmApplication.getInstance().getAccountManager().getPwmProfiles().swapAccounts(db);
            PwmApplication.getInstance().loadFavoritesFromGlobalSettings();
            String originalInstructions = getResources().getString(R.string.lblImportInstructions);
            if ( !errors.isEmpty()) {
                originalInstructions += "Accounts not imported: \n" + convertToString(errors);
            }
            getInstructionsView().setText(originalInstructions);
            Toast.makeText(this, "Successfully imported RDF from clipboard", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error importing database", e);
            Toast.makeText(this, "Error importing RDF from clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    public TextView getInstructionsView() {
        return (TextView)findViewById(R.id.lblInstructions);
    }

    public Button getImportButton() {
        return (Button)findViewById(R.id.btnImport);
    }

    public Button getExportButton() {
        return (Button)findViewById(R.id.btnExport);
    }

    public boolean convertIsChecked() {
        CheckBox chkBox = (CheckBox)findViewById(R.id.chkConvertBadAlgo);
        return chkBox.isChecked();
    }

    public String convertToString(List<IncompatibleException> errors) {
        Iterable<String> errorStrs = Iterables.transform(errors, new Function<IncompatibleException, String>() {
            @Override
            public String apply(IncompatibleException input) {
                String msg = input.getMessage();
                int loc = msg.indexOf(':', msg.indexOf(':') + 1);
                return msg.substring(0, loc);
            }
        });
        return Joiner.on("\n").join(errorStrs);
    }
}
