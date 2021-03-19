package com.tyron.layouteditor.editor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tyron.layouteditor.EditorContext;
import com.tyron.layouteditor.WidgetFactory;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.LinearLayoutItem;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.parser.ViewLayoutInflater;

import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

public class EditorView extends LinearLayout {

	private final EditorContext editorContext;
	private LinearLayoutItem root;
	
	public EditorView(Context context){
		super(context);
		editorContext = new EditorContext(getContext(), new WidgetFactory(this));
		init();
	}


	/**
	 * @return returns the root layout of the editor
	 */
	public BaseWidget getRootEditorView(){
		return root;
	}

	private void init() {
		setOrientation(VERTICAL);

		root = new LinearLayoutItem(editorContext);
		root.setRoot(true);
		root.setOrientation(VERTICAL);
		addView(root, new LayoutParams(-1,-1));

//		View testView = null;
//		try {
//			testView = ViewLayoutInflater.inflate(editorContext, getContext().getAssets().open("test/test.xml"), this);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		//addView(testView);
		
//		LayoutTransition layoutTransition = new LayoutTransition();
//		layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//		layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
//		root.setLayoutTransition(layoutTransition);
	}
}
