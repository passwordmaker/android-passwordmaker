package org.passwordmaker.android;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountManager;
import org.daveware.passwordmaker.AlgorithmType;
import org.daveware.passwordmaker.CharacterSets;

import java.util.ArrayList;
import java.util.Map;

/**
 * A fragment representing a single Account detail screen.
 * This fragment is either contained in a {@link AccountListActivity}
 * in two-pane mode (on tablets) or a {@link AccountDetailActivity}
 * on handsets.
 */
public class AccountDetailFragment extends Fragment {
    private static final String LOG_TAG = "ADF";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_PARENT_ID = "parent_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Account mItem;


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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = accountManager.getPwmProfiles().findAccountById(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ViewGetter viewGetter = new ViewGetter(rootView);
            viewGetter.fill(mItem);
        }

        return rootView;
    }

    // This function must batch the strings.xml array: HashAlgos
    private static ImmutableBiMap<AlgorithmType, Integer> typeToNum = ImmutableBiMap.<AlgorithmType, Integer>builder()
            .put(AlgorithmType.MD4, 0)
            .put(AlgorithmType.MD5, 2)
            .put(AlgorithmType.SHA1, 4)
            .put(AlgorithmType.SHA256, 6)
            .put(AlgorithmType.RIPEMD160, 8).build();

    private static int getAlgoOrdinal(AlgorithmType type, boolean isHMac) {
        return typeToNum.get(type) + (isHMac ? 1 : 0);
    }

    private boolean isHMac(int ordinal) {
        return ordinal % 2 != 0;
    }

    private AlgorithmType getAlgoType(int ordinal) {
        // odd is h
        if ( isHMac(ordinal) ) ordinal--;
        return typeToNum.inverse().get(ordinal);
    }

    // This function must batch the strings.xml array: NamedCharSets
    private static ImmutableBiMap<String, Integer> charSetToNum = ImmutableBiMap.<String, Integer>builder()
            .put(CharacterSets.BASE_93_SET, 0)
            .put(CharacterSets.ALPHANUMERIC, 1)
            .put(CharacterSets.HEX, 2)
            .put(CharacterSets.NUMERIC, 3)
            .put(CharacterSets.ALPHA, 4)
            .put(CharacterSets.SPECIAL_CHARS, 5)
            .put("Custom (Enter your own)", 6).build();

    private static int getCharSetOrdinal(String charSet) {
        Integer result = charSetToNum.get(charSet);
        if ( result == null ) return 6;
        return result;
    }

    private String getCharSet(int ordinal) {
        return charSetToNum.inverse().get(ordinal);
    }

    public class ViewGetter {
        private View rootView;
        public ViewGetter(View rootView) {
            this.rootView = rootView;
        }

        public void fill(Account mItem) {
            getTxtName().setText(mItem.getName());
            getChkProtocol().setChecked(mItem.getUrlComponents().contains(Account.UrlComponents.Protocol));
            getChkSubDomain().setChecked(mItem.getUrlComponents().contains(Account.UrlComponents.Subdomain));
            getChkDomain().setChecked(mItem.getUrlComponents().contains(Account.UrlComponents.Domain));
            getChkOthers().setChecked(mItem.getUrlComponents().contains(Account.UrlComponents.PortPathAnchorQuery));
            getSelectHashAlgos().setSelection(getAlgoOrdinal(mItem.getAlgorithm(), mItem.isHmac()));
            getSelectLeet().setSelection(mItem.getLeetType().getOrdinal());
            getSelectLeetLevel().setSelection(mItem.getLeetLevel().getOrdinal());
            getPasswordLength().setText(Integer.toString(mItem.getLength()));
            getUsername().setText(mItem.getUsername());
            getModifier().setText(mItem.getModifier());
            getSelectCharacterSet().setSelection(getCharSetOrdinal(mItem.getCharacterSet()));
            getPrefix().setText(mItem.getPrefix());
            getSuffix().setText(mItem.getSuffix());
        }

        public EditText getTxtName() {
            return (EditText)rootView.findViewById(R.id.txtName);
        }

        public CheckBox getChkProtocol() {
            return (CheckBox)rootView.findViewById(R.id.chkProtocol);
        }

        public CheckBox getChkSubDomain() {
            return (CheckBox)rootView.findViewById(R.id.chksubdomain);
        }

        public CheckBox getChkDomain() {
            return (CheckBox)rootView.findViewById(R.id.chkDomain);
        }

        public CheckBox getChkOthers() {
            return (CheckBox)rootView.findViewById(R.id.chkOthers);
        }

        public Spinner getSelectHashAlgos() {
            return (Spinner)rootView.findViewById(R.id.selectHashAlgos);
        }

        public Spinner getSelectLeet() {
            return (Spinner)rootView.findViewById(R.id.selectLeet);
        }

        public Spinner getSelectLeetLevel() {
            return (Spinner)rootView.findViewById(R.id.spinLeetLevel);
        }

        public EditText getPasswordLength() {
            return (EditText)rootView.findViewById(R.id.txtPasswordLen);
        }

        public EditText getUsername() {
            return (EditText)rootView.findViewById(R.id.txtUsername);
        }

        public EditText getModifier() {
            return (EditText)rootView.findViewById(R.id.txtModifier);
        }

        public Spinner getSelectCharacterSet() {
            return (Spinner)rootView.findViewById(R.id.selectCharacterSet);
        }

        public EditText getPrefix() {
            return (EditText)rootView.findViewById(R.id.txtPrefix);
        }

        public EditText getSuffix() {
            return (EditText)rootView.findViewById(R.id.txtSuffix);
        }
    }
}
