package com.tyron.layouteditor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.tyron.layouteditor.adapters.HierarchyViewBinder;
import com.tyron.layouteditor.adapters.WidgetsAdapter;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.editor.WidgetFactory;
import com.tyron.layouteditor.editor.dialog.ColorPickerDialog;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.HierarchyView;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.parser.ViewLayoutExporter;
import com.tyron.layouteditor.toolbox.tree.TreeNode;
import com.tyron.layouteditor.toolbox.tree.TreeViewAdapter;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import xyz.truenight.dynamic.DynamicLayoutInflater;
import xyz.truenight.dynamic.EditorFactory;
import xyz.truenight.dynamic.compat.CompatDynamicLayoutInflater;

public class DesignActivity extends AppCompatActivity {

    private final List<TreeNode> hierarchy_data = new ArrayList<>();
    private final ArrayList<Widget> widgets = new ArrayList<>();
    private RecyclerView recyclerview_widgets;
    private RecyclerView recyclerview_hierarchy;
    private WidgetsAdapter adapter;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private MaterialCardView bottomSheetLayout;

    private TreeViewAdapter hierarchy_adapter;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private ViewGroup rootView;
    private EditorView editorView;

    private DynamicLayoutInflater inflater;

    private final float bottomSheetRadius = AndroidUtilities.dp(16);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        AndroidUtilities.checkDisplaySize(this, null);

        rootView = findViewById(R.id.editor_container);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        findViewById(R.id.app_bar).bringToFront();
        editorView = new EditorView(this);
        rootView.addView(editorView);

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_app_bar_open_drawer_description,
                R.string.nav_app_bar_close_drawer_description
        );

        if (savedInstanceState != null) {
            Layout savedLayout = null;

            savedLayout = Value.getGson().fromJson(savedInstanceState.getString("savedLayout"), Layout.class);

            if(savedLayout != null) {
                editorView.importLayout(savedLayout);
            }

        }

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
                bottomSheetLayout.setRadius(bottomSheetRadius * slideOffset);
            }
        });


        recyclerview_widgets = findViewById(R.id.recyclerview_widgets);
        recyclerview_widgets.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerview_widgets.setAdapter(adapter = new WidgetsAdapter(widgets, bottomSheetBehavior));
        populate();

        recyclerview_hierarchy = findViewById(R.id.hierarchy_list);
        recyclerview_hierarchy.setLayoutManager(new LinearLayoutManager(this));
        HierarchyViewBinder binder = new HierarchyViewBinder();
        binder.setOnItemLongClickListener(((holder, position, node) -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            ((HierarchyView)node.getContent()).widget.getAsView().performClick();
        }));
        hierarchy_adapter = new TreeViewAdapter(hierarchy_data, Arrays.asList(binder));
        recyclerview_hierarchy.setAdapter(hierarchy_adapter);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                refreshViewHierarchy();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        WidgetFactory factory = new WidgetFactory(editorView.getEditorContext(), editorView.getEditor(), editorView);
        inflater = DynamicLayoutInflater.base(editorView.getEditorContext())
                .setWidgetFactory(factory)
                .setFactory2(new EditorFactory(factory))
                .create();


//        ColorPickerDialog dialog = ColorPickerDialog.newInstance();
//        dialog.show(getSupportFragmentManager(), "");

    }

    private void populate() {
        widgets.add(new Widget(Widget.LINEAR_LAYOUT));
        widgets.add(new Widget(Widget.RELATIVE_LAYOUT));
        widgets.add(new Widget(Widget.FRAME_LAYOUT));
        widgets.add(new Widget(Widget.BUTTON));
        widgets.add(new Widget(Widget.EDITTEXT));
        widgets.add(new Widget(Widget.PROGRESSBAR));
        widgets.add(new Widget(Widget.TEXTVIEW));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        try {
            String savedLayout = saveLayout();

            if (savedLayout != null) {
                savedInstanceState.putString("savedLayout", saveLayout());
            }
        }catch(Exception e){
            //TODO: Handle unknown views
        }
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
                editorView.removeAllViews();
                editorView.setViewListeners(DynamicLayoutInflater.from(editorView.getEditorContext()).inflate(content, editorView));

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


    private String saveLayout() throws ClassCastException{
        BaseWidget rootWidget = editorView.getRootEditorView();
        if (rootWidget == null) {
            return null;
        }
        return Value.getGson().toJson(walkTree(rootWidget), Layout.class);
    }

    private Layout walkTree(@NonNull BaseWidget widget) {

        View view = widget.getAsView();

        Layout layout = widget.getViewManager().getLayout();

        if (view instanceof ViewGroup) {


            ArrayList<Layout> childLayout = new ArrayList<>();

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                BaseWidget childWidget = (BaseWidget) ((ViewGroup) view).getChildAt(i);

                childLayout.add(walkTree(childWidget));

            }

            Layout.Attribute attr = new Layout.Attribute(editorView.getEditor().getAttributeId("android:children", "ViewGroup").id, new Primitive(""));
            if (layout.attributes.contains(attr)) {
                layout.attributes.set(layout.attributes.indexOf(attr), new Layout.Attribute(editorView.getEditor().getAttributeId("android:children", "ViewGroup").id, new com.tyron.layouteditor.values.Array(childLayout.toArray(new Layout[0]))));
            } else {
                layout.attributes.add(new Layout.Attribute(editorView.getEditor().getAttributeId("android:children", "ViewGroup").id, new com.tyron.layouteditor.values.Array(childLayout.toArray(new Layout[0]))));
            }
        }

        return layout;

    }

    private void refreshViewHierarchy(){
        List<TreeNode> nodes = new ArrayList<>();

        BaseWidget rootWidget = editorView.getRootEditorView();

        if(rootWidget == null){
            return;
        }

        nodes.add(addAllNodes(rootWidget.getAsView()));


        hierarchy_adapter.refresh(nodes);
    }

    private TreeNode<HierarchyView> addAllNodes(View parent){

        TreeNode<HierarchyView> newNode = new TreeNode<>(new HierarchyView(((BaseWidget)parent)));

        if(parent instanceof ViewGroup){
            for(int i = 0; i < ((ViewGroup) parent).getChildCount(); i++){
                View child = ((ViewGroup) parent).getChildAt(i);

                newNode.addChild(addAllNodes(child));
            }
        }

        return newNode;

    }
}
