package org.passwordmaker.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountManager;
import org.daveware.passwordmaker.AccountPatternData;
import org.daveware.passwordmaker.AccountPatternType;
import org.jetbrains.annotations.NotNull;

/**
 * A fragment representing a single PatternData detail screen.
 * This fragment is either contained in a {@link PatternDataListActivity}
 * in two-pane mode (on tablets) or a {@link PatternDataDetailActivity}
 * on handsets.
 */
@SuppressWarnings("ConstantConditions")
public class PatternDataDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_PATTERN_POSITION = "pattern_position";
    public static final String ARG_TWO_PANE_MODE = "two_pane_mode";

    @SuppressWarnings("FieldCanBeLocal")
    private Account account;
    private AccountPatternData patternData;
    private boolean twoPaneMode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PatternDataDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String itemId = getArguments().getString(ARG_ITEM_ID);
            int patternPosition = getArguments().getInt(ARG_PATTERN_POSITION, 0);
            AccountManager accountManager = PwmApplication.getInstance().getAccountManager();
            account = accountManager.getPwmProfiles().findAccountById(itemId);
            patternData = account.getPatterns().get(patternPosition);
            twoPaneMode = getArguments().getBoolean(ARG_TWO_PANE_MODE, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patterndata_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show the dummy content as text in a TextView.
        if (patternData != null) {
            setPatternData(patternData);
        }
        getPrimaryButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePatternData();
                if ( ! twoPaneMode )
                    ((PatternDataDetailActivity)getActivity()).navigateUp();
            }
        });

        if ( ! twoPaneMode ) {
            getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PatternDataDetailActivity)getActivity()).navigateUp();
                }
            });
        } else {
            // two pane mode won't have this since you can just change views later
            getCancelButton().setVisibility(View.INVISIBLE);
        }
    }

    public void setPatternData(@NotNull AccountPatternData pd) {
        patternData = pd;
        getTextDescription().setText(pd.getDesc());
        getTextPattern().setText(pd.getPattern());
        setPatternType(pd.getType());
        getCheckEnabled().setChecked(pd.isEnabled());
    }

    public void savePatternData() {
        patternData.setDesc(getTextDescription().getText().toString());
        patternData.setPattern(getTextPattern().getText().toString());
        patternData.setType(getPatternType());
        patternData.setEnabled(getCheckEnabled().isChecked());
    }

    public TextView getTextDescription() {
        return (TextView)getView().findViewById(R.id.txtPatternDesc);
    }

    public TextView getTextPattern() {
        return (TextView)getView().findViewById(R.id.txtPatternExpression);
    }

    public CheckBox getCheckEnabled() {
        return (CheckBox)getView().findViewById(R.id.chkEnabled);
    }

    public Button getPrimaryButton() {
        return (Button)getView().findViewById(R.id.primary);
    }

    public Button getCancelButton() {
        return (Button)getView().findViewById(android.R.id.closeButton);
    }

    public RadioButton getOptionWildcard() {
        return (RadioButton)getView().findViewById(R.id.optWildcard);
    }

    @SuppressWarnings("UnusedDeclaration")
    public RadioButton getOptionRegex() {
        return (RadioButton)getView().findViewById(R.id.optRegex);
    }

    public AccountPatternType getPatternType() {
        if ( getOptionWildcard().isChecked() ) {
            return AccountPatternType.WILDCARD;
        } else {
            return AccountPatternType.REGEX;
        }
    }

    public void setPatternType(AccountPatternType type) {
        boolean isWildcard = type == AccountPatternType.WILDCARD;
        getOptionWildcard().setChecked(isWildcard);
        getOptionWildcard().setChecked(!isWildcard);
    }

}
