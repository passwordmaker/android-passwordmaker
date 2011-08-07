/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordMakerProForAndroidActivity extends Activity {
	private static final String REPO_KEY_PROFILES = "profiles";
	private static final String REPO_KEY_CURRENT_PROFILES = "currentProfile";
	private static final String REPO_KEY_SAVED_INPUTS = "savedInputs";
	private static final String REPO_KEY_SAVED_LENGTH = "savedLength";
	private static final String REPO_KEY_SAVED_INPUT_UNTIL = "savedInputUnilt";
	private static final String REPO_KEY_SAVED_INPUT_PASSWORD = "savedInputPass";
	private static final String REPO_KEY_SAVED_INPUT_INPUTTEXT = "savedInputInputText";

	private static String LOG_TAG = "PasswordMakerProForAndroidActivity";
	PasswordMaker pwm;
	PwmProfileList pwmProfiles = new PwmProfileList();

	public static final String EXTRA_PROFILE = PasswordMakerEditProfile.EXTRA_PROFILE;
	private static final int EDIT_PROFILE = 0x04;

	private CheckBox chkSaveInputs;
	private TextView lblInputTimeout;
	private EditText txtInputTimeout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		chkSaveInputs = (CheckBox) findViewById(R.id.chkSaveInputs);
		chkSaveInputs.setOnCheckedChangeListener(onSaveInputCheckbox);
		txtInputTimeout = (EditText) findViewById(R.id.txtSaveInputTime);
		lblInputTimeout = (TextView) findViewById(R.id.lblSaveForLength);
		try {
			pwmProfiles = PrivateSettingsStorage.getInstance().getObject(this,
					REPO_KEY_PROFILES, pwmProfiles);
		} catch (IOException e) {
			Log.e(LOG_TAG,
					"Error occured while attempting to load saved profiles from PrivateStore",
					e);
		}
		if (pwmProfiles.isEmpty())
			pwmProfiles.set(new PwmProfile("Default"));

		pwm = new PasswordMaker();

		String currentProfile = null;
		currentProfile = getPreferences(MODE_PRIVATE).getString(
				REPO_KEY_CURRENT_PROFILES, currentProfile);
		PwmProfile prof = pwmProfiles.get(currentProfile);
		if (prof != null)
			pwm.setProfile(prof);
		else
			pwm.setProfile(pwmProfiles.get("Default"));

		TextView text = (TextView) findViewById(R.id.txtInput);
		if (text != null)
			text.setOnKeyListener(mUpdatePasswordKeyListener);
		if (text != null)
			text.setOnFocusChangeListener(mUpdatePasswordFocusListener);
		text = (TextView) findViewById(R.id.txtMasterPass);
		if (text != null)
			text.setOnKeyListener(mUpdatePasswordKeyListener);
		if (text != null)
			text.setOnFocusChangeListener(mUpdatePasswordFocusListener);
		Button button = (Button) findViewById(R.id.btnCopy);
		if (button != null)
			button.setOnClickListener(mCopyButtonClick);
		button = (Button) findViewById(R.id.btnFavorites);
		if (button != null)
			button.setOnClickListener(mFavoritesClick);

		loadDefaultValueForFields();
	}

	@Override
	protected void onPause() {
		super.onPause();
		final Editor prefs = getPreferences(Activity.MODE_PRIVATE).edit();
		try {
			final String currentProfile = pwm.getProfile().getName();
			prefs.putString(REPO_KEY_CURRENT_PROFILES, currentProfile);
			prefs.putBoolean(REPO_KEY_SAVED_INPUTS, chkSaveInputs.isChecked());
			if (chkSaveInputs.isChecked()) {
				String strMin = txtInputTimeout.getText().toString();
				final int minutes = (strMin == null || strMin.length() < 0) ? 5
						: Integer.parseInt(strMin);
				final Calendar cal = Calendar.getInstance();
				long curTime = cal.getTimeInMillis();
				cal.add(Calendar.MINUTE, minutes);
				final long time = cal.getTimeInMillis();
				Log.i(LOG_TAG, "Current time:" + Long.toString(curTime) + ", Expire Time: " + Long.toString(time));
				prefs.putInt(REPO_KEY_SAVED_LENGTH, minutes);
				prefs.putLong(REPO_KEY_SAVED_INPUT_UNTIL, time);
				prefs.putString(REPO_KEY_SAVED_INPUT_PASSWORD,
						getInputPassword());
				prefs.putString(REPO_KEY_SAVED_INPUT_INPUTTEXT, getInputText());
			} else {
				prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
				prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
				prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
			}
			PrivateSettingsStorage.getInstance().putObject(this,
					REPO_KEY_PROFILES, pwmProfiles);
		} catch (IOException e) {
			Log.e(LOG_TAG,
					"Error occured while attempting to store user profiles to PrivateStore",
					e);
		} finally {
			prefs.commit();
		}
	}

	private void loadDefaultValueForFields() {
		try {
			final int minutes = getPreferences(MODE_PRIVATE).getInt(
					REPO_KEY_SAVED_LENGTH, 5);
			txtInputTimeout.setText(Integer.toString(minutes));
			chkSaveInputs.setChecked(getPreferences(MODE_PRIVATE).getBoolean(
					REPO_KEY_SAVED_INPUTS, false));
			final long time = getPreferences(MODE_PRIVATE).getLong(
					REPO_KEY_SAVED_INPUT_UNTIL, -1);
			if (time != -1 && chkSaveInputs.isChecked()) {
				Calendar cal = Calendar.getInstance();
				if (time > cal.getTimeInMillis()) {
					final String savedPass = getPreferences(MODE_PRIVATE)
							.getString(REPO_KEY_SAVED_INPUT_PASSWORD, "");
					final String savedInputText = getPreferences(MODE_PRIVATE)
							.getString(REPO_KEY_SAVED_INPUT_INPUTTEXT, "");
					setInputPassword(savedPass);
					setInputText(savedInputText);
					updatePassword();
				} else {
					// expired clear from preferences
					final Editor prefs = getPreferences(Activity.MODE_PRIVATE)
							.edit();
					prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
					prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
					prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
					prefs.commit();
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not load default values", e);
			final Editor prefs = getPreferences(Activity.MODE_PRIVATE).edit();
			prefs.remove(REPO_KEY_SAVED_LENGTH);
			prefs.remove(REPO_KEY_SAVED_INPUT_UNTIL);
			prefs.remove(REPO_KEY_SAVED_INPUT_PASSWORD);
			prefs.remove(REPO_KEY_SAVED_INPUT_INPUTTEXT);
			prefs.commit();
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
		final List<String> favs = new ArrayList<String>(pwm.getProfile()
				.getFavorites());
		favs.add(getString(R.string.AddFavorite));
		final CharSequence[] items = favs.toArray(new CharSequence[0]);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a Favorite");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item >= 0 && item < items.length - 1) {
					TextView inputText = (TextView) findViewById(R.id.txtInput);
					inputText.setText(items[item]);
					updatePassword();
				} else if (item == items.length - 1) {
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
		builder.setPositiveButton(R.string.AddFavorite,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						pwm.getProfile().addFavorite(
								editView.getText().toString());
						TextView inputText = (TextView) findViewById(R.id.txtInput);
						inputText.setText(editView.getText());
						updatePassword();
					}
				});
		builder.setNegativeButton(R.string.Cancel, null);
		final AlertDialog alert = builder.create();
		editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alert.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
		switch (requestCode) {
		case EDIT_PROFILE:
			PwmProfile changedProfile = (PwmProfile) data
					.getSerializableExtra(EXTRA_PROFILE);
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
		builder.setPositiveButton(R.string.AddProfile,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						create_profile(editView.getText());
					}
				});
		builder.setNegativeButton(R.string.Cancel, null);
		final AlertDialog alert = builder.create();
		editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alert.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
				Toast.makeText(getApplicationContext(), items[item],
						Toast.LENGTH_SHORT).show();
				PwmProfile selProfile = pwmProfiles.get(items[item]);
				pwm.setProfile(selProfile);
				updatePassword();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public final void updatePassword() {
		TextView text = (TextView) findViewById(R.id.txtPassword);
		final String inputText = getInputText();
		final String masterPassword = getInputPassword();
		if (pwm.matchesPasswordHash(masterPassword)) {
			String output = pwm.generatePassword(inputText, masterPassword);
			text.setText(output);
		} else {
			text.setText("Password Hash Mismatch");
		}
	}

	private String getInputPassword() {
		TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
		return masterPass.getText().toString();
	}

	private void setInputPassword(String value) {
		TextView masterPass = (TextView) findViewById(R.id.txtMasterPass);
		masterPass.setText(value);
	}

	private String getInputText() {
		TextView inputText = (TextView) findViewById(R.id.txtInput);
		return inputText.getText().toString();
	}

	private void setInputText(String value) {
		TextView inputText = (TextView) findViewById(R.id.txtInput);
		inputText.setText(value);
	}

	private OnFocusChangeListener mUpdatePasswordFocusListener = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus)
				updatePassword();

		}
	};

	private OnKeyListener mUpdatePasswordKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			updatePassword();
			return false;
		}
	};

	private OnClickListener mCopyButtonClick = new OnClickListener() {

		public void onClick(View v) {
			updatePassword();
			final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			TextView text = (TextView) findViewById(R.id.txtPassword);
			clipboard.setText(text.getText());
		}
	};

	private OnClickListener mFavoritesClick = new OnClickListener() {

		public void onClick(View v) {
			if (pwm.getProfile().getFavorites().isEmpty())
				newFavorite();
			else
				selectFavorite();
		}
	};

	private OnCheckedChangeListener onSaveInputCheckbox = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			final int visibility = isChecked ? View.VISIBLE : View.GONE;
			txtInputTimeout.setVisibility(visibility);
			lblInputTimeout.setVisibility(visibility);
		}

	};

}