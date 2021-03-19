package com.tyron.layouteditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tyron.layouteditor.adapters.WidgetsAdapter;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.util.ArrayList;

public class DesignActivity extends AppCompatActivity {

    private final ArrayList<Widget> widgets = new ArrayList<>();
    private RecyclerView recyclerview_widgets;
    private WidgetsAdapter adapter;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FrameLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        AndroidUtilities.checkDisplaySize(this, null);
        ((FrameLayout)findViewById(R.id.editor_container)).addView(new EditorView(this));
        bottomSheetLayout = findViewById(R.id.bottomsheet_layout);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        recyclerview_widgets = findViewById(R.id.recyclerview_widgets);
        recyclerview_widgets.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerview_widgets.setAdapter(adapter = new WidgetsAdapter(widgets, bottomSheetBehavior));
        populate();

    }

    private void populate(){
        widgets.add(new Widget(Widget.LINEAR_LAYOUT));
        widgets.add(new Widget(Widget.RELATIVE_LAYOUT));
        adapter.notifyDataSetChanged();
    }
}