package org.passwordmaker.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.collect.ImmutableBiMap;
import org.daveware.passwordmaker.*;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

import static org.daveware.passwordmaker.Account.UrlComponents;

/**
 * A fragment representing a single Account detail screen.
 * This fragment is either contained in a {@link AccountListActivity}
 * in two-pane mode (on tablets) or a {@link AccountDetailActivity}
 * on handsets.
 */
public class AccountDetailFragment extends Fragment {
    private static final String LOG_TAG = Logtags.ACCOUNT_DETAIL_FRAGMENT.getTag();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Account mItem;
    private View lastFocusedView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountManager accountManager = PwmApplication.getInstance().getAccountManager();
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = accountManager.getPwmProfiles().findAccountById(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);

        if (mItem != null) {
            ViewGetter viewGetter = new ViewGetter(rootView);
            viewGetter.setMembers();
            viewGetter.fill(mItem);
            viewGetter.setChangeListeners();
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if ( lastFocusedView != null )
            lastFocusedView.getOnFocusChangeListener().onFocusChange(lastFocusedView, false);
    }

    private LeetType getLeetType(int ordinal) {
        return LeetType.TYPES[ordinal];
    }

    private static final int CUSTOM_CHARSET_POSITION = 6;
    // This function must batch the strings.xml array: NamedCharSets
    private final static ImmutableBiMap<String, Integer> charSetToNum = ImmutableBiMap.<String, Integer>builder()
            .put(CharacterSets.BASE_93_SET, 0)
            .put(CharacterSets.ALPHANUMERIC, 1)
            .put(CharacterSets.HEX, 2)
            .put(CharacterSets.NUMERIC, 3)
            .put(CharacterSets.ALPHA, 4)
            .put(CharacterSets.SPECIAL_CHARS, 5)
            .put("Custom (Enter your own)", CUSTOM_CHARSET_POSITION).build();

    private static int getCharSetOrdinal(String charSet) {
        Integer result = charSetToNum.get(charSet);
        if ( result == null ) return 6;
        return result;
    }

    @SuppressWarnings("UnusedDeclaration")
    private String getCharSet(int ordinal) {
        return charSetToNum.inverse().get(ordinal);
    }

    public class ViewGetter {
        private final View rootView;
        private EditText txtName;
        private CheckBox chkProtocol;
        private CheckBox chkSubDomain;
        private CheckBox chkDomain;
        private CheckBox chkOthers;
        private Spinner selectHashAlgos;
        private Spinner selectLeet;
        private Spinner selectLeetLevel;
        private EditText passwordLength;
        private EditText txtUsername;
        private EditText tatModifier;
        private Spinner spinnerCharacterSet;
        private EditText txtCustomCharacterSet;
        private EditText txtPrefix;
        private EditText txtSuffix;
        private Button showPatterns;
        private TextView lblUseText;
        private EditText txtUseUrl;
        private EditText txtNotes;
        private ViewGroup frameUrlParts;

        public ViewGetter(@NotNull View rootView) {
            this.rootView = rootView;
        }

        public void setMembers() {
            txtName = (EditText)rootView.findViewById(R.id.txtName);
            chkProtocol = (CheckBox)rootView.findViewById(R.id.chkProtocol);
            chkSubDomain = (CheckBox)rootView.findViewById(R.id.chksubdomain);
            chkDomain = (CheckBox)rootView.findViewById(R.id.chkDomain);
            chkOthers = (CheckBox)rootView.findViewById(R.id.chkOthers);
            txtUseUrl = (EditText)rootView.findViewById(R.id.txtUseUrl);
            lblUseText = (TextView)rootView.findViewById(R.id.lblUseText);
            selectHashAlgos = (Spinner)rootView.findViewById(R.id.selectHashAlgos);
            selectLeet = (Spinner)rootView.findViewById(R.id.selectLeet);
            selectLeetLevel = (Spinner)rootView.findViewById(R.id.spinLeetLevel);
            passwordLength = (EditText)rootView.findViewById(R.id.txtPswordLen);
            txtUsername = (EditText)rootView.findViewById(R.id.txtUsername);
            tatModifier = (EditText)rootView.findViewById(R.id.txtModifier);
            spinnerCharacterSet = (Spinner)rootView.findViewById(R.id.selectCharacterSet);
            txtPrefix = (EditText)rootView.findViewById(R.id.txtPrefix);
            txtSuffix = (EditText)rootView.findViewById(R.id.txtSuffix);
            showPatterns = (Button)rootView.findViewById(R.id.btnShowPatterns);
            frameUrlParts = (ViewGroup)rootView.findViewById(R.id.frameUrlParts);
            txtCustomCharacterSet = (EditText)rootView.findViewById(R.id.txtCustomCharacterSet);
            txtNotes = (EditText)rootView.findViewById(R.id.txtNotes);

        }

        public void fill(Account mItem) {
            txtName.setText(mItem.getName());
            chkProtocol.setChecked(mItem.getUrlComponents().contains(UrlComponents.Protocol));
            chkSubDomain.setChecked(mItem.getUrlComponents().contains(UrlComponents.Subdomain));
            chkDomain.setChecked(mItem.getUrlComponents().contains(UrlComponents.Domain));
            chkOthers.setChecked(mItem.getUrlComponents().contains(UrlComponents.PortPathAnchorQuery));
            selectHashAlgos.setSelection(AlgorithmSelectionValues.getFromAccount(mItem).getStringResourcePosition());
            selectLeet.setSelection(mItem.getLeetType().getOrdinal());
            selectLeetLevel.setSelection(mItem.getLeetLevel().getOrdinal());
            passwordLength.setText(Integer.toString(mItem.getLength()));
            txtUsername.setText(mItem.getUsername());
            tatModifier.setText(mItem.getModifier());
            spinnerCharacterSet.setSelection(getCharSetOrdinal(mItem.getCharacterSet()));
            txtPrefix.setText(mItem.getPrefix());
            txtSuffix.setText(mItem.getSuffix());
            txtUseUrl.setText(mItem.getUrl());
            txtNotes.setText(mItem.getDesc());

            if ( spinnerCharacterSet.getSelectedItemPosition() == CUSTOM_CHARSET_POSITION ) {
                txtCustomCharacterSet.setVisibility(View.VISIBLE);
                txtCustomCharacterSet.setText(mItem.getCharacterSet());
            } else {
                txtCustomCharacterSet.setVisibility(View.GONE);
                txtCustomCharacterSet.setText(mItem.getCharacterSet());
            }

            if ( mItem.isDefault() ) {
                frameUrlParts.setVisibility(View.VISIBLE);
                // for legacy imports, its possible to have the url field filled out, therefore we must show it.
                if ( mItem.getUrl().isEmpty() ) {
                    txtUseUrl.setVisibility(View.GONE);
                    lblUseText.setVisibility(View.GONE);
                } else {
                    txtUseUrl.setVisibility(View.VISIBLE);
                    lblUseText.setVisibility(View.VISIBLE);
                }
            } else {
                frameUrlParts.setVisibility(View.VISIBLE);
                txtUseUrl.setVisibility(View.VISIBLE);
                lblUseText.setVisibility(View.VISIBLE);
            }

        }

        public void setChangeListeners() {
            txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        mItem.setName(txtName.getText().toString());
                    else
                        lastFocusedView = txtName;
                }
            });
            chkProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mItem.addUrlComponent(UrlComponents.Protocol);
                    else
                        mItem.removeUrlComponent(UrlComponents.Protocol);

                }
            });
            chkSubDomain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if ( isChecked )
                        mItem.addUrlComponent(UrlComponents.Subdomain);
                    else
                        mItem.removeUrlComponent(UrlComponents.Subdomain);

                }
            });
            chkOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if ( isChecked )
                        mItem.addUrlComponent(UrlComponents.PortPathAnchorQuery);
                    else
                        mItem.removeUrlComponent(UrlComponents.PortPathAnchorQuery);

                }
            });
            chkDomain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if ( isChecked )
                        mItem.addUrlComponent(UrlComponents.Domain);
                    else
                        mItem.removeUrlComponent(UrlComponents.Domain);

                }
            });
            txtUseUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        mItem.setUrl(txtUseUrl.getText().toString());
                    else
                        lastFocusedView = txtUseUrl;
                }
            });
            txtNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        mItem.setDesc(txtNotes.getText().toString());
                    else
                        lastFocusedView = txtNotes;
                }
            });

            selectLeet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LeetType sel = getLeetType(parent.getSelectedItemPosition());
                    mItem.setLeetType(sel);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            selectLeetLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LeetLevel lvl = LeetLevel.fromInt(parent.getSelectedItemPosition() + 1);
                    mItem.setLeetLevel(lvl);
                }
                public void onNothingSelected(AdapterView<?> arg0) {}
            });
            selectHashAlgos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        AlgorithmSelectionValues selected = AlgorithmSelectionValues.getByPosition(position);
                        selected.setAccountSettings(mItem);
                    } catch (NoSuchElementException e) {
                        Toast.makeText(getActivity(), "Failed to set account algorithm: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {}
            });
            spinnerCharacterSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( position == CUSTOM_CHARSET_POSITION ) {
                        txtCustomCharacterSet.setVisibility(View.VISIBLE);
                    } else {
                        String charSet = getCharSet(position);
                        mItem.setCharacterSet(charSet);
                        txtCustomCharacterSet.setVisibility(View.GONE);
                        txtCustomCharacterSet.setText(charSet);
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            txtCustomCharacterSet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( !hasFocus ) {
                        mItem.setCharacterSet(txtCustomCharacterSet.getText().toString());
                    } else {
                        lastFocusedView = txtCustomCharacterSet;
                    }
                }
            });
            passwordLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( ! hasFocus ) {
                        if ( passwordLength.getText().length() == 0 ) {
                            passwordLength.setText(Integer.toString(mItem.getLength()));
                        } else {
                            try {
                                mItem.setLength(Integer.parseInt(passwordLength.getText().toString()));
                            } catch (NumberFormatException e) {
                                Log.e(LOG_TAG, "Can not set length of password, \"" + passwordLength.getText().toString() + "\" " +
                                        "using existing length of " + mItem.getLength() + " Error: " + e.getMessage());
                                passwordLength.setText(Integer.toString(mItem.getLength()));
                            }
                        }
                    } else
                        lastFocusedView = passwordLength;
                }
            });
            txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( ! hasFocus )
                        mItem.setUsername(txtUsername.getText().toString());
                    else
                        lastFocusedView = txtUsername;
                }
            });
            tatModifier.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        mItem.setModifier(tatModifier.getText().toString());
                    else
                        lastFocusedView = tatModifier;
                }
            });
            // this listener is setup elsewhere
            txtPrefix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( ! hasFocus )
                        mItem.setPrefix(txtPrefix.getText().toString());
                    else
                        lastFocusedView = txtPrefix;
                }
            });
            txtSuffix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if ( ! hasFocus )
                        mItem.setSuffix(txtSuffix.getText().toString());
                    else
                        lastFocusedView = txtSuffix;
                }
            });
            showPatterns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // In single-pane mode, simply start the detail activity
                    // for the selected item ID.
                    Intent patternList = new Intent(getActivity(), PatternDataListActivity.class);
                    patternList.putExtra(PatternDataListFragment.ARG_ACCOUNT_ID, mItem.getId());
                    startActivity(patternList);
                }
            });
        }
    }
}
