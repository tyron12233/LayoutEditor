package com.tyron.layouteditor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.IdGenerator;
import com.tyron.layouteditor.editor.dialog.ColorPickerDialog;
import com.tyron.layouteditor.editor.dialog.EditTextDialog;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.DrawableValue;

import java.util.ArrayList;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.ViewHolder> {
	
	private final ArrayList<Attribute> data;
	private final String targetId;
	private final IdGenerator idGenerator;
	
	public AttributesAdapter(String targetId, ArrayList<Attribute> data, IdGenerator idGenerator) {
		this.data = data;
		this.targetId = targetId;
		this.idGenerator = idGenerator;
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
		//TODO: add different dialogs for different types
		holder.itemView.setOnClickListener((v) -> {
			//Toast.makeText(v.getContext(), attr.value.getAsString(), Toast.LENGTH_LONG).show();
//			EditTextDialog dialog = EditTextDialog.newInstance(data, position, targetId, idGenerator);
//			dialog.show(AndroidUtilities.getActivity(v.getContext()).getSupportFragmentManager(), "");

			String attributeName = attr.key;

			DialogFragment dialog = null;

			switch(Attributes.getType(attributeName)){
				case Attributes.TYPE_COLOR:
				case Attributes.TYPE_DRAWABLE_STRING:
					if(Color.isColor(attr.value.toString())){
						dialog = ColorPickerDialog.newInstance(attr, targetId);
					}
					break;
				default:
					dialog = EditTextDialog.newInstance(data, position, targetId, idGenerator);
					break;
			}
			dialog.show(AndroidUtilities.getActivity(v.getContext()).getSupportFragmentManager(), "");
		});
	}
	
	@Override
	public int getItemCount() {
		return data.size();
	}
	
	public void updateData(List<Attribute> data){
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		
		public TextView name;
		
		public ViewHolder(View view) {
			super(view);
			name = view.findViewById(R.id.textview_attributename);
		}
	}
}
