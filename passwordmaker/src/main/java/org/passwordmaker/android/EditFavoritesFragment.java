package org.passwordmaker.android;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.passwordmaker.android.widgets.SwipeDismissListViewTouchListener;

public class EditFavoritesFragment extends ListFragment implements SwipeDismissListViewTouchListener.DismissCallbacks {

    ArrayAdapter<String> favorites;
    SwipeDismissListViewTouchListener touchListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favorites = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, PwmApplication.getInstance().getAccountManager().getFavoriteUrls());
        setListAdapter(favorites);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = getListView();
        touchListener = new SwipeDismissListViewTouchListener(listView, this);
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    public void addItem(String title) {
        favorites.add(title);
    }

    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            favorites.remove(favorites.getItem(position));
        }
        favorites.notifyDataSetChanged();
    }
}
