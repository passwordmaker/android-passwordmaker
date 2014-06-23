package org.passwordmaker.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.RDFDatabaseReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


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
        Database db = PwmApplication.getInstance().getAccountManager().getPwmProfiles();
        AndroidRDFDatabaseWriter writer = new AndroidRDFDatabaseWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            writer.write(os, db);
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            String str = os.toString();
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
            RDFDatabaseReader reader = new RDFDatabaseReader();
            ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
            PwmApplication.getInstance().getAccountManager().getPwmProfiles().swapAccounts(reader.read(is));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error importing database", e);
            Toast.makeText(this, "Error importing RDF from clipboard", Toast.LENGTH_SHORT).show();
        }
    }


    public Button getImportButton() {
        return (Button)findViewById(R.id.btnImport);
    }

    public Button getExportButton() {
        return (Button)findViewById(R.id.btnExport);
    }
}
