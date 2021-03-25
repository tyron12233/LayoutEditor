package com.tyron.layouteditor.editor;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tyron.layouteditor.EditorContext;
import com.tyron.layouteditor.Function;
import com.tyron.layouteditor.FunctionManager;
import com.tyron.layouteditor.WidgetFactory;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.LinearLayoutItem;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.util.HashMap;
import java.util.Map;

public class EditorView extends LinearLayout{

	private final EditorContext editorContext;
	private final FunctionManager functionManager;

	/**
	 * The view that represents the shadow
	 */
	private View shadow;
	private LinearLayoutItem root;

	public EditorView(Context context){
		super(context);
		functionManager = new FunctionManager(loadFunctions());
		editorContext = new EditorContext(context, new WidgetFactory(this), functionManager);
		shadow = new View(context);
		init();
	}

	private Map<String, Function> loadFunctions() {
		Map<String, Function> map = new HashMap<>();
		map.put(Function.DATE.getName(), Function.DATE);
		map.put(Function.FORMAT.getName(), Function.FORMAT);
		map.put(Function.JOIN.getName(), Function.JOIN);
		map.put(Function.NUMBER.getName(), Function.NUMBER);

		map.put(Function.ADD.getName(), Function.ADD);
		map.put(Function.SUBTRACT.getName(), Function.SUBTRACT);
		map.put(Function.MULTIPLY.getName(), Function.MULTIPLY);
		map.put(Function.DIVIDE.getName(), Function.DIVIDE);
		map.put(Function.MODULO.getName(), Function.MODULO);

		map.put(Function.AND.getName(), Function.AND);
		map.put(Function.OR.getName(), Function.OR);

		map.put(Function.NOT.getName(), Function.NOT);

		map.put(Function.EQUALS.getName(), Function.EQUALS);
		map.put(Function.LESS_THAN.getName(), Function.LESS_THAN);
		map.put(Function.GREATER_THAN.getName(), Function.GREATER_THAN);
		map.put(Function.LESS_THAN_OR_EQUALS.getName(), Function.LESS_THAN_OR_EQUALS);
		map.put(Function.GREATER_THAN_OR_EQUALS.getName(), Function.GREATER_THAN_OR_EQUALS);

		map.put(Function.TERNARY.getName(), Function.TERNARY);

		map.put(Function.CHAR_AT.getName(), Function.CHAR_AT);
		map.put(Function.CONTAINS.getName(), Function.CONTAINS);
		map.put(Function.IS_EMPTY.getName(), Function.IS_EMPTY);
		map.put(Function.LENGTH.getName(), Function.LENGTH);
		map.put(Function.TRIM.getName(), Function.TRIM);

		map.put(Function.MAX.getName(), Function.MAX);
		map.put(Function.MIN.getName(), Function.MIN);

		map.put(Function.SLICE.getName(), Function.SLICE);

		return map;
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
		root.setOnDragListener(dragListener);
		addView(root, new LayoutParams(-1,-1));

		shadow = new View(getContext());
		shadow.setBackgroundColor(0x52000000);
		shadow.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(50)));
		shadow.setMinimumWidth(AndroidUtilities.dp(50));
		shadow.setMinimumHeight(AndroidUtilities.dp(50));
	}

	View.OnDragListener dragListener = new OnDragListener() {
		@Override
		public boolean onDrag(View v, DragEvent event) {

			//don't allow dragging if it's not a ViewGroup
			if (!(v instanceof ViewGroup)) {
				return false;
			}

			ViewGroup hostView = (ViewGroup) v;

			switch (event.getAction()) {
				case DragEvent.ACTION_DROP: {
					hostView.removeView(shadow);

					Object object = event.getLocalState();

					View view;

					//if the object is a new one added by the user,
					//it will be in a form of a view so no need to
					//create a new one
					if (object instanceof Widget) {
						view = editorContext.getWidgetFactory().createWidget(editorContext, (Widget) object);
					} else {
						view = (View) object;
					}

					if (view.getParent() != null) {
						((ViewGroup) view.getParent()).removeView(view);
					}
					hostView.addView(view);
					//set this drag listener to the view
					//so it can be dragged on too
					view.setOnDragListener(this);
					break;
				}

				//make sure that the shadow is removed
				case DragEvent.ACTION_DRAG_ENDED:
				case DragEvent.ACTION_DRAG_EXITED: {
					hostView.removeView(shadow);
					break;
				}
				case DragEvent.ACTION_DRAG_ENTERED: {
					hostView.removeView(shadow);
					hostView.addView(shadow);
					break;
				}
			}
			return true;
		}
	};
	public WidgetFactory getWidgetFactory(){
		return editorContext.getWidgetFactory();
	}

	
}
