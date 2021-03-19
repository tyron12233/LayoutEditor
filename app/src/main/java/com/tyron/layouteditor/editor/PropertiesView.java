package com.tyron.layouteditor.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.adapters.AttributesAdapter;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PropertiesView extends BottomSheetDialogFragment {

    private final ArrayList<Attribute> attributes;
    //private final Map<String, Value> modified = new HashMap<>();

    private AttributesAdapter adapter;
    private RecyclerView recyclerView;

    BaseWidget target;
    public PropertiesView(BaseWidget view){
        target = view;
        attributes = view.getAttributes();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.property_view, container, false);
        
        adapter = new AttributesAdapter(target, attributes);
        recyclerView = view.findViewById(R.id.recyclerview_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        /*textView.setText(Arrays.toString(attributes.entrySet().toArray()));

        textView.setOnClickListener((v) -> {
            modified.put(Attributes.View.Width, new Primitive(50));
            target.update(modified);
            target.getAsView().requestLayout();
        });
*/
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
}
