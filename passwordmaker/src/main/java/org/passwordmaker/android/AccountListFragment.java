package org.passwordmaker.android;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A list fragment representing a list of Accounts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link AccountDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AccountListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    public static final String STATE_ACCOUNT_STACK = "activated_account_stack";
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = "ALF";
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private AccountManager accountManager;

    private Account loadedAccount;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Account account);
        public void onFolderSelected(Account account);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Account account) {
        }

        @Override
        public void onFolderSelected(Account account) {

        }
    };

    private AccountStack accountStack = new AccountStack();



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = PwmApplication.getInstance().getAccountManager();
        setListAdapter(new ArrayAdapter<Account>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1));
        loadIncomingAccount();
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

    private void loadIncomingAccount() {
        Intent intent = getActivity().getIntent();
        String accountId = intent.getStringExtra(AccountDetailFragment.ARG_ITEM_ID);
        ArrayList<String> accStack = intent.getStringArrayListExtra(AccountListFragment.STATE_ACCOUNT_STACK);
        if ( accStack == null || accStack.isEmpty()) {
            accountStack.clearToRoot();
            if ( accountId != null && !accountId.isEmpty() ) {
                List<Account> pathToAccount = accountManager.getPwmProfiles().findPathToAccountById(accountId);
                // this is saved so that we can notify the main list that we should display the details of the selected
                // account right when they set the callback
                loadedAccount = pathToAccount.remove(pathToAccount.size() - 1);
                accountStack.replace(pathToAccount);

            }
        } else {
            accountStack.loadFromIds(accStack);
        }
        refreshList(accountStack.getCurrentAccount());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        if ( loadedAccount != null ) {
            Account acc = loadedAccount;
            loadedAccount = null;
            mCallbacks.onItemSelected(acc);
        }

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

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Account selected = getCurrentAccountList().getItem(position);
        if ( selected.hasChildren() ) {
            goIntoFolder(selected);
        } else {
            mCallbacks.onItemSelected(selected);
        }
    }

    public void goIntoFolder(Account folder) {
        accountStack.pushCurrentAccount(folder);
        refreshList(folder);
        mCallbacks.onFolderSelected(folder);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putStringArrayList(STATE_ACCOUNT_STACK, accountStack.getIds());
    }

    public void saveAccountListState(Intent intent) {
        intent.putStringArrayListExtra(AccountListFragment.STATE_ACCOUNT_STACK, accountStack.getIds());
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Account> getCurrentAccountList() {
        return (ArrayAdapter<Account>) getListAdapter();
    }

    private void refreshList(@NotNull Account account) {
        ArrayAdapter<Account> accounts = getCurrentAccountList();
        accounts.setNotifyOnChange(false);
        accounts.clear();
        accounts.addAll(account.getChildren());
        accounts.notifyDataSetChanged();
    }

    public void createNewAccount(String accountName) {
        try {
            Account account = new Account(accountName, false);
            account.copySettings(accountManager.getDefaultAccount());
            account.setName(accountName);
            account.setIsFolder(false);
            account.clearUrlComponents();
            account.setUrl(accountName);
            account.getPatterns().clear();
            accountManager.getPwmProfiles().addAccount(accountStack.getCurrentAccount(), account);
            getCurrentAccountList().notifyDataSetChanged();
            mCallbacks.onItemSelected(account);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void createNewFolder(String folderName) {
        try {
            Account account = new Account(folderName, true);
            accountManager.getPwmProfiles().addAccount(accountStack.getCurrentAccount(), account);
            getCurrentAccountList().notifyDataSetChanged();
            goIntoFolder(account);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class AccountStack {
        private LinkedList<Account> accountStack = new LinkedList<Account>();

        public int size() {
            return accountStack.size();
        }

        public void clearToRoot() {
            pushCurrentAccount(null);
        }

        public void replace(Collection<Account> accounts) {
            clearToRoot();
            // can't use add all since it will be in reverse order
            for ( Account a : accounts ) accountStack.push(a);
        }

        // I think this might belong in the fragment
        public void pushCurrentAccount(@Nullable Account account) {
            if ( account == null ) {
                accountStack.clear();
                account = accountManager.getPwmProfiles().getRootAccount();
            }
            if ( accountStack.peek() != account )
                accountStack.push(account);
        }

        @SuppressWarnings("UnusedDeclaration")
        public Account popAccount() {
            // always ensure there is at least the root on the stack
            if ( accountStack.isEmpty() )
                accountStack.push(accountManager.getPwmProfiles().getRootAccount());
            return accountStack.peek();
        }

        @NotNull
        public Account getCurrentAccount() {
            if ( accountStack.isEmpty() ) {
                accountStack.push(accountManager.getPwmProfiles().getRootAccount());
            }
            return accountStack.peek();
        }

        public ArrayList<String> getIds() {
            Collection<String> accountStackIds = Lists.transform(accountStack, new Function<Account, String>() {
                @Override
                public String apply(Account account) {
                    return account.getId();
                }
            });
            return new ArrayList<String>(accountStackIds);
        }

        private void loadFromIds(List<String> accountIdStack) {
            if ( accountIdStack == null ) return;
            accountStack.clear();
            Collection<Account> accounts = Collections2.filter(Lists.transform(accountIdStack,
                            new Function<String, Account>() {
                                @Override
                                public Account apply(String id) {
                                    return accountManager.getPwmProfiles().findAccountById(id);
                                }
                            }), new Predicate<Account>() {
                        @Override
                        public boolean apply(Account account) {
                            return account != null;
                        }
                    });
            accountStack.addAll(accounts);
        }

    }
}
