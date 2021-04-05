package com.tyron.layouteditor.editor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.view.View;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.tyron.layouteditor.models.Choice;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Value;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class FormDialog extends DialogFragment{
	
	public static FormDialog newInstance(Attribute attribute, String targetId, Choice choice, int defaultItem){
		FormDialog dialog = new FormDialog();
		
		Bundle args = new Bundle();
		args.putParcelable("choice", choice);
		args.putString("attribute", Value.getGson().toJson(attribute));
		args.putString("targetId", targetId);
		args.putInt("default", defaultItem);
		dialog.setArguments(args);
		
		return dialog;
	}
	
	public FormDialog(){
		
	}
	
	private Choice choice;
	private Attribute attribute;
	private String targetId;
	private int defaultItem;
	
	private LinearLayout root;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(getArguments() != null){
			choice = getArguments().getParcelable("choice");
			targetId = getArguments().getString("targetId");
			attribute = Value.getGson().fromJson(getArguments().getString("attribute"), Attribute.class);
		    defaultItem = getArguments().getInt("default");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
		
		builder.setPositiveButton("APPLY", null);
		builder.setNegativeButton("CANCEL", null);
		
		
		if(choice.singleChoice){
			builder.setView(createSingleChoiceForm());
		}else{
		
		}
		
		builder.setTitle(attribute.key);
		
		return builder.create();
	}
	
	private LinearLayout createSingleChoiceForm() {
		
	    root = new LinearLayout(getContext());
		root.setOrientation(LinearLayout.VERTICAL);
		root.setPaddingRelative(AndroidUtilities.dp(8), AndroidUtilities.dp(8), 0,0);
		
		for(int i = 0; i < choice.data.size(); i++){
			FormItemLayout layout = new FormItemLayout(getContext(), choice.data.get(i));
			root.addView(layout);
			
			if(defaultItem == i){
				layout.setChecked(true);
				layout.setValue(attribute.value.toString());
			}
		}
		
		return root;
	}
	
	private RadioButton preCheckedView = null;
	
	private View.OnClickListener onClickListener = (v) -> {
		
		if(preCheckedView != null){
			preCheckedView.setChecked(false);
		}
		
		preCheckedView = (RadioButton) v;
	};
	private class FormItemLayout extends LinearLayout {
		
		private RadioButton button;
		private EditText editText;
		private Choice.Item item;
		
		public FormItemLayout(Context context, Choice.Item item){
			super(context);
			this.item = item;
			
			button = new RadioButton(context);
			button.setOnClickListener(onClickListener);
			addView(button);
			
			if(item.type == Choice.Item.TYPE_EDITTEXT){
				editText = new EditText(context);
				editText.setHint(item.value);
				addView(editText);
				
				button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton view, boolean checked){
						if(editText != null){
							editText.setEnabled(checked);
						}
					}
				});
			}else{
				button.setText(item.value);
				
			}
		}
		
		public void setValue(String value){
			if(editText == null || value == null) return;
			
			editText.setText(value);
		}
		
		public String getValue(){
			return editText.getText().toString();
		}
		
		public void setChecked(boolean val){
			if(val){
			button.performClick();
			}
		}
		
		public boolean isChecked(){
			return button.isChecked();
		}
		
		public Choice.Item getItem(){
			return item;
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		AlertDialog dialog = (AlertDialog) getDialog();
		
		if(dialog != null){
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
				NotificationCenter.getInstance(). postNotificationName (NotificationCenter.didUpdateWidget, targetId, Collections.singletonList(new Attribute(attribute.key, getSelectedValue())));
			});
		}
	}
	
	private Primitive getSelectedValue(){
		for(int i = 0; i < root.getChildCount(); i++){
			FormItemLayout child = (FormItemLayout) root.getChildAt(i);
			
			if(child.isChecked()){
			if(child.item.type == 0){
				return new Primitive(child.item.value);
			}else if(child.item.type == 1){
			    return new Primitive(child.getValue());
		    }
			}
		}
		return new Primitive("");
	}
}
	