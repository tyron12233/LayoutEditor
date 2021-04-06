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
import com.tyron.layouteditor.editor.dialog.FormDialog;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.models.Choice;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.DrawableValue;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.ViewHolder> {
	
	private final ArrayList<Attribute> data;
	private final ArrayList<String> availableAttributes;
	private final String targetId;
	private final IdGenerator idGenerator;
	
	public AttributesAdapter(String targetId, ArrayList<Attribute> data,
							 List<String> availableAttributes ,IdGenerator idGenerator) {
		this.data = data;
		this.targetId = targetId;
		this.availableAttributes = (ArrayList<String>) availableAttributes;
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
			
			DialogFragment dialog = dialog = EditTextDialog.newInstance(availableAttributes,data, attr, targetId, idGenerator);
			
			switch(Attributes.getType(attributeName)){
				case Attributes.TYPE_COLOR:
				case Attributes.TYPE_DRAWABLE_STRING:
				if(Color.isColor(attr.value.toString())){
					dialog = ColorPickerDialog.newInstance(attr, targetId);
				}
				break;
				case Attributes.TYPE_BOOLEAN:
				dialog = FormDialog.newInstance(attr, targetId, Choice.createBoolean(), attr.value.getAsString().equals("true") ? 0 : 1);
				break;
				case Attributes.TYPE_DIMENSION:
				if(attr.key.equals(Attributes.View.Width) || attr.key.equals(Attributes.View.Height)){
					
					int defaultItem = 0;
					
					String val = attr.value.toString();
					if(val.equals("wrap_content")){
						defaultItem = 1;
					}else if(val.equals("match_parent")){
					    defaultItem = 0;
					}else{
					    defaultItem = 2;
					}
					
					dialog = FormDialog.newInstance(attr, targetId, Choice.createLayoutDimension(), defaultItem);
				}else{
					//TODO: NUMBER DIALOG
				}
				break;
				default:
				dialog = EditTextDialog.newInstance(availableAttributes,data, attr, targetId, idGenerator);
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
