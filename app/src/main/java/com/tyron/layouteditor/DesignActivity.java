package com.tyron.layouteditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tyron.layouteditor.adapters.WidgetsAdapter;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.parser.ViewLayoutExporter;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class DesignActivity extends AppCompatActivity {

    private final ArrayList<Widget> widgets = new ArrayList<>();
    private RecyclerView recyclerview_widgets;
    private WidgetsAdapter adapter;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FrameLayout bottomSheetLayout;

    private ViewGroup rootView;
    private EditorView editorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        AndroidUtilities.checkDisplaySize(this, null);

        rootView = findViewById(R.id.editor_container);

        editorView = new EditorView(this);
        rootView.addView(editorView);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,0,0,"VIEW_SOURCE");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == 0){
            //TODO: handle export in the background thread

            String str = "";
            try {
                 str = ViewLayoutExporter.inflate(editorView.getRootEditorView());
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }finally {
                Intent intent = new Intent(this, ViewSourceActivity.class);
                intent.putExtra("source", str);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}