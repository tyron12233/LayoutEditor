package com.tyron.layouteditor.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.reflect.TypeToken;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.adapters.AttributesAdapter;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public class PropertiesView extends BottomSheetDialogFragment implements NotificationCenter.NotificationCenterDelegate {
	
	String targetId;
	private IdGenerator idGenerator;
	private ArrayList<Attribute> attributes;
	
	private AttributesAdapter adapter;
	private RecyclerView recyclerView;
	
	public PropertiesView() {
		
	}
	
	public static PropertiesView newInstance(ArrayList<Attribute> attributes, String id, IdGenerator idGenerator) {
		PropertiesView dialog = new PropertiesView();
		
		Bundle args = new Bundle();
		String attrString = Value.getGson().toJson(attributes);
		args.putString("attributes", attrString);
		args.putParcelable("idGenerator", idGenerator);
		args.putString("id", id);
		dialog.setArguments(args);
		
		return dialog;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		attributes = Value.getGson().fromJson(
		getArguments().getString("attributes"),
		new TypeToken<ArrayList<Attribute>>() {
		}.getType());
		idGenerator = getArguments().getParcelable("idGenerator");
		targetId = getArguments().getString("id");
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.property_view, container, false);
		
		adapter = new AttributesAdapter(targetId, attributes, idGenerator);
		recyclerView = view.findViewById(R.id.recyclerview_items);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(adapter);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams windowParams = window.getAttributes();
		windowParams.dimAmount = 0f;
		windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(windowParams);
	}
	
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdateWidget);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdateWidget);
	}
	
	@Override
	public void didReceivedNotification(int id, Object... args) {
		if(id == NotificationCenter.didUpdateWidget){
			
			Set<Attribute> noDuplicates = new LinkedHashSet<>((List<Attribute>) args[1]);
			noDuplicates.addAll(attributes);
		    
			//this.attributes.clear();
			//this.attributes.addAll(noDuplicates);
			adapter.updateData(new ArrayList<Attribute>(noDuplicates));
		}
	}
}
