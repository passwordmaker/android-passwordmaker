package org.passwordmaker.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.TextView;

public class PasswordMakerProForAndroidActivity extends Activity {
	PasswordMaker pwm;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pwm = new PasswordMaker();
        TextView text = (TextView)findViewById(R.id.txtInput);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        text = (TextView)findViewById(R.id.txtMasterPass);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        Button button = (Button)findViewById(R.id.btnCopy);
        if ( button != null ) button.setOnClickListener(mCopyButtonClick);
    }
    
    public final void updatePassword() {
    	TextView text = (TextView)findViewById(R.id.txtPassword);
    	TextView inputText = (TextView)findViewById(R.id.txtInput);
    	TextView masterPass = (TextView)findViewById(R.id.txtMasterPass);
    	
    	String output = pwm.generatePassword(inputText.getText().toString(), masterPass.getText().toString());
    	text.setText(output);
    }
    
    private OnKeyListener mUpdatePasswordKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			updatePassword();
			return false; 
		}
	};
    
	
    private OnClickListener mCopyButtonClick = new OnClickListener() {
    	
    	public void onClick(View v) {
    		final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
    		TextView text = (TextView)findViewById(R.id.txtPassword);
    		clipboard.setText(text.getText());
        }
    };
}