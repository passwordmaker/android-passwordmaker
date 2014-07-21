package org.passwordmaker.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.common.base.Preconditions;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountPatternData;
import org.jetbrains.annotations.NotNull;
import org.passwordmaker.android.widgets.SwipeDismissListViewTouchListener;

/**
 * A list fragment representing a list of PatternData. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link PatternDataDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PatternDataListFragment extends ListFragment
        implements SwipeDismissListViewTouchListener.DismissCallbacks {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    public static final String ARG_ACCOUNT_ID = "account_id";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    @SuppressWarnings("FieldCanBeLocal")
    private Account account;
    // We need to keep a reference to this
    @SuppressWarnings("FieldCanBeLocal")
    private SwipeDismissListViewTouchListener touchListener;

    public void setAccountId(@NotNull String accountId) {
        account = PwmApplication.getInstance().getAccountManager().getPwmProfiles().findAccountById(accountId);
        Preconditions.checkNotNull(account, "Can not find account by id: %s", accountId);
        setListAdapter(new ArrayAdapter<AccountPatternData>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, account.getPatterns()));
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int position, AccountPatternData patternData);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private final static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int position, AccountPatternData patternData) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PatternDataListFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        if ( getArguments() != null ) {
            String accountId = getArguments().getString(ARG_ACCOUNT_ID);
            setAccountId(accountId);
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mActivatedPosition = position;
        mCallbacks.onItemSelected(position, getPatternAdapter().getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = getListView();
        touchListener = new SwipeDismissListViewTouchListener(listView, this);
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    public void createNewPattern() {
        getPatternAdapter().add(new AccountPatternData());
        int position = getPatternAdapter().getCount()-1;
        setActivatedPosition(position);
        mCallbacks.onItemSelected(position, getPatternAdapter().getItem(position));
    }

    protected ArrayAdapter<AccountPatternData> getPatternAdapter() {

        @SuppressWarnings("unchecked")
        ArrayAdapter<AccountPatternData> result = (ArrayAdapter<AccountPatternData>)getListAdapter();
        return result;
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick() {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        if ( reverseSortedPositions.length != 1 ) return;
        int position = reverseSortedPositions[0];
        confirmDelete(position);
    }

    protected void reallyDelete(final int position) {
        ArrayAdapter<AccountPatternData> patterns = getPatternAdapter();
        patterns.remove(patterns.getItem(position));
        patterns.notifyDataSetChanged();
    }

    protected void confirmDelete(final int position) {
        final ArrayAdapter<AccountPatternData> patterns = getPatternAdapter();
        AccountPatternData pattern = patterns.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirm_delete)
                .setMessage(getResources().getText(R.string.delete_confirmation_text) + " '" + pattern.getDesc() + "'")
                .setCancelable(true);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reallyDelete(position);
            }
        });
        builder.setNegativeButton(R.string.Cancel, null);
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
