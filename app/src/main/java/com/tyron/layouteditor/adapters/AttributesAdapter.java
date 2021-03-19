package com.tyron.layouteditor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.dialog.EditTextDialog;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.util.ArrayList;


public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.ViewHolder> {

    private final ArrayList<Attribute> data;
    private final BaseWidget widget;

    public AttributesAdapter(BaseWidget widget, ArrayList<Attribute> data) {
        this.data = data;
        this.widget = widget;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		final Attribute attr = data.get(position);
		
		holder.name.setText(attr.key);
		
		holder.itemView.setOnClickListener((v) -> {
			Toast.makeText(v.getContext(), attr.value.getAsString(), Toast.LENGTH_LONG).show();
            EditTextDialog dialog = new EditTextDialog(attr);
            dialog.setOnUpdateListener(attribute -> {
                data.set(position, attribute);
                widget.update(data);
            });
            dialog.show(AndroidUtilities.getActivity(v.getContext()).getSupportFragmentManager(), "");
		});
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
		
		public TextView name;
        public ViewHolder(View view){
            super(view);
			name = view.findViewById(R.id.textview_attributename);
        }
    }
}
