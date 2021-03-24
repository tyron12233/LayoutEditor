package com.tyron.layouteditor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.models.Attribute;

import java.util.ArrayList;
import java.util.List;

public class AttributeAutoCompleteAdapter extends ArrayAdapter<Attribute> {

    Context context;
    int resource, textViewResourceId;
    List<Attribute> items, tempItems, suggestions;

    public AttributeAutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<Attribute> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Attribute>(items); // this makes the difference.
        suggestions = new ArrayList<Attribute>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Attribute attr = items.get(position);
        if (attr != null) {
            TextView lblName = view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(attr.key);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Attribute) resultValue).key;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Attribute attr : tempItems) {
                    if (attr.key.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(attr);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Attribute> filterList = (ArrayList<Attribute>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Attribute attr : filterList) {
                    add(attr);
                    notifyDataSetChanged();
                }
            }
        }
    };
}