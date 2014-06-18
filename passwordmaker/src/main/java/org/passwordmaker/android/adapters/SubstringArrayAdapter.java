package org.passwordmaker.android.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.List;

public class SubstringArrayAdapter extends ArrayAdapter<String> {
    private List<String> objects;
    private final List<String> original_objects;
    private final SubStringFilter myfilter = new SubStringFilter();

    public SubstringArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.original_objects = this.objects = objects;
    }

    public SubstringArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.original_objects = this.objects = objects;
    }

    @Override
    public Filter getFilter() {
        return myfilter;
    }

    private class SubStringFilter extends Filter {
        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if ( constraint == null || constraint.length() == 0 ) {
                results.values = new ArrayList<String>(original_objects);
                results.count = original_objects.size();
                return results;
            }
            final ArrayList<String> matched = new ArrayList<String>(
            Collections2.filter(original_objects, new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return s.contains(constraint);
                }
            }));
            results.values = matched;
            results.count = matched.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            objects = (List<String>) results.values;

            if (results.count > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
        }
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String getItem(int position) {
        return objects.get(position);
    }
}
