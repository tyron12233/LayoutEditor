package com.tyron.layouteditor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tyron.layouteditor.adapters.WidgetsAdapter;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.parser.ViewLayoutExporter;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import xyz.truenight.dynamic.DynamicLayoutInflater;

public class DesignActivity extends AppCompatActivity {

    private final ArrayList<Widget> widgets = new ArrayList<>();
    private RecyclerView recyclerview_widgets;
    private WidgetsAdapter adapter;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FrameLayout bottomSheetLayout;

    private ViewGroup rootView;
    private EditorView editorView;

    private DynamicLayoutInflater inflater;

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
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheet.setElevation(0);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheet.setElevation(AndroidUtilities.dp(12));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });


        recyclerview_widgets = findViewById(R.id.recyclerview_widgets);
        recyclerview_widgets.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerview_widgets.setAdapter(adapter = new WidgetsAdapter(widgets, bottomSheetBehavior));
        populate();

        inflater = DynamicLayoutInflater.base(this)
                .setWidgetFactory(editorView.getWidgetFactory())
                .create();

    }

    private void populate() {
        widgets.add(new Widget(Widget.LINEAR_LAYOUT));
        widgets.add(new Widget(Widget.RELATIVE_LAYOUT));
        widgets.add(new Widget(Widget.TEXTVIEW));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 0, 0, "VIEW_SOURCE");
        menu.add(0, 1, 0, "IMPORT XML");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == 0) {
            //TODO: handle export in the background thread

            String str = "";
            try {
                str = ViewLayoutExporter.inflate(editorView.getRootEditorView());
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            } finally {
                Intent intent = new Intent(this, ViewSourceActivity.class);
                intent.putExtra("source", str);
                startActivity(intent);
            }
        }
        if (id == 1) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/xml");
            intent = Intent.createChooser(intent, "Select layout file");
            startActivityForResult(intent, 20);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == 20) {
            //import the view
            Uri returnUri = data.getData();

            //convert uri to String
            try {
                InputStream inputStream = getContentResolver().openInputStream(returnUri);

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                String content = total.toString();

                //inflate the xml
                //remove all views from previous layout
                ((ViewGroup) editorView.getRootEditorView().getAsView()).removeAllViews();
                DynamicLayoutInflater.from(this).inflate(content, (ViewGroup) editorView.getRootEditorView());
            } catch (FileNotFoundException e) {
                Log.e("ACTIVITY RESULT", "File not found: " + returnUri);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InflateException e) {
                Toast.makeText(this, "Error inflating xml file: " + e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
