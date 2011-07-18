package org.passwordmaker.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordMakerProForAndroidActivity extends Activity {
	PasswordMaker pwm;
	PwmProfileList pwmProfiles = new PwmProfileList();
	
	public static final String EXTRA_PROFILE = PasswordMakerEditProfile.EXTRA_PROFILE;
	private static final int EDIT_PROFILE = 0x04;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pwm = new PasswordMaker();
        pwmProfiles.set(pwm.getProfile());
        TextView text = (TextView)findViewById(R.id.txtInput);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        text = (TextView)findViewById(R.id.txtMasterPass);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        Button button = (Button)findViewById(R.id.btnCopy);
        if ( button != null ) button.setOnClickListener(mCopyButtonClick);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.NewProfile:
            newProfile();
            return true;
        case R.id.ChangeProfile:
            changeProfile();
            return true;
        case R.id.EditProfile:
        	editProfile();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void create_profile(Editable text) {
		pwmProfiles.add(text.toString());
		edit_profile(pwmProfiles.get(text.toString()));
	}
	
	private void editProfile() {
    	final CharSequence[] items = pwmProfiles.toProfileNames();

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Pick a profile");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	PwmProfile selProfile = pwmProfiles.get(items[item]);
    	    	edit_profile(selProfile);
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private void edit_profile(PwmProfile profile) {
		Intent intent = new Intent(this, PasswordMakerEditProfile.class);
		intent.putExtra(EXTRA_PROFILE, profile);
		startActivityForResult(intent, EDIT_PROFILE);
		
	}
	
	private void finish_edit_profile(PwmProfile profile) {
		System.out.println( profile.getName() + ": Len of pass: " +  profile.getLengthOfPassword() );
		
		pwmProfiles.set(profile);
		if (pwm.getProfile().getName().equals(profile.getName())) {
			pwm.setProfile(profile);
			updatePassword();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch ( requestCode ) {
		case EDIT_PROFILE:
			PwmProfile changedProfile = (PwmProfile) data.getSerializableExtra(EXTRA_PROFILE);
			finish_edit_profile(changedProfile);
			break;
		}
	}
	
	private void newProfile() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editView = new EditText(this);
		editView.setLines(1);
		editView.setMinimumWidth(80);
		builder.setView(editView);
		builder.setPositiveButton(R.string.AddProfile, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				create_profile(editView.getText());
			}
		});
		builder.setNegativeButton(R.string.Cancel, null);
		final AlertDialog alert = builder.create();
		editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
				
			}
        });
		builder.setCancelable(true);
		alert.show();
	}
    
    private void changeProfile() {
    	final CharSequence[] items = pwmProfiles.toProfileNames();

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Pick a profile");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    	        PwmProfile selProfile = pwmProfiles.get(items[item]);
    	        pwm.setProfile(selProfile);
    	        updatePassword();
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	
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