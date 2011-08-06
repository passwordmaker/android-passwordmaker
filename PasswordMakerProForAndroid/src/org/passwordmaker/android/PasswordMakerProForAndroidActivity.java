package org.passwordmaker.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Editable;
import android.util.Log;
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
	private static final String REPO_KEY_PROFILES = "profiles";
	private static final String REPO_KEY_CURRENT_PROFILES = "currentProfile";
	private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
	PasswordMaker pwm;
	PwmProfileList pwmProfiles = new PwmProfileList();
	
	public static final String EXTRA_PROFILE = PasswordMakerEditProfile.EXTRA_PROFILE;
	private static final int EDIT_PROFILE = 0x04;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
			pwmProfiles = PrivateSettingsStorage.getInstance().getObject(getApplicationContext(), REPO_KEY_PROFILES, pwmProfiles);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error occured while attempting to load saved profiles from PrivateStore", e);
		}
		if ( pwmProfiles.isEmpty() ) 
			pwmProfiles.set(new PwmProfile("Default") ) ;
		
        pwm = new PasswordMaker();
        
		try {
			String currentProfile = null;
			currentProfile = PrivateSettingsStorage.getInstance().getObject(getApplicationContext(), REPO_KEY_CURRENT_PROFILES, currentProfile);
			PwmProfile prof = pwmProfiles.get(currentProfile);
			if ( prof != null ) 
				pwm.setProfile(prof);
			else
				pwm.setProfile(pwmProfiles.get("Default"));
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error occured while attempting to load current profile from PrivateStore", e);
		}
        
        TextView text = (TextView)findViewById(R.id.txtInput);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        text = (TextView)findViewById(R.id.txtMasterPass);
        if ( text != null ) text.setOnKeyListener(mUpdatePasswordKeyListener);
        Button button = (Button)findViewById(R.id.btnCopy);
        if ( button != null ) button.setOnClickListener(mCopyButtonClick);
        button = (Button)findViewById(R.id.btnFavorites);
        if ( button != null ) button.setOnClickListener(mFavoritesClick);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
			PrivateSettingsStorage.getInstance().putObject(getApplicationContext(), REPO_KEY_PROFILES, pwmProfiles);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error occured while attempting to store user profiles to PrivateStore", e);
		}
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
	
	private void selectFavorite() {
		final List<String> favs = new ArrayList<String>(pwm.getProfile().getFavorites());
		favs.add( getString(R.string.AddFavorite) );
    	final CharSequence[] items = favs.toArray(new CharSequence[0]);

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Pick a Favorite");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	if ( item >= 0 && item < items.length - 1 ) {
    	    		TextView inputText = (TextView)findViewById(R.id.txtInput);
    	    		inputText.setText(items[item]);
    	    		updatePassword();
    	    	} else if ( item == items.length - 1 ) {
    	    		newFavorite();
    	    	}
    	    } 
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private void newFavorite() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editView = new EditText(this);
		editView.setLines(1);
		editView.setMinimumWidth(200);
		builder.setView(editView);
		builder.setPositiveButton(R.string.AddFavorite, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				pwm.getProfile().addFavorite(editView.getText().toString());
	    		TextView inputText = (TextView)findViewById(R.id.txtInput);
	    		inputText.setText(editView.getText());
	    		updatePassword();
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
	
	private void edit_profile(PwmProfile profile) {
		Intent intent = new Intent(this, PasswordMakerEditProfile.class);
		intent.putExtra(EXTRA_PROFILE, profile);
		startActivityForResult(intent, EDIT_PROFILE);
		
	}
	
	private void finish_edit_profile(PwmProfile profile) {
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
    	final String masterPassword = masterPass.getText().toString();
		if ( pwm.matchesPasswordHash(masterPassword) ) { 
			String output = pwm.generatePassword(inputText.getText().toString(), masterPassword);
			text.setText(output);
		} else {
			text.setText("Password Hash Mismatch");
		}
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
    
    private OnClickListener mFavoritesClick = new OnClickListener() {
    	
    	public void onClick(View v) {
    		if ( pwm.getProfile().getFavorites().isEmpty() )
    			newFavorite();
    		else
    			selectFavorite();
        }
    };
}