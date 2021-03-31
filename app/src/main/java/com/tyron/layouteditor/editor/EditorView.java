package com.tyron.layouteditor.editor;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Layout;

public class EditorView extends LinearLayout {

    private final EditorContext editorContext;
    private final EditorLayoutInflater layoutInflater;
    private final Editor editor;
    View.OnLongClickListener onLongClickListener = v -> {
        ViewCompat.startDragAndDrop(v, null, new DragShadowBuilder(v), v, 0);
        ((ViewGroup) v.getParent()).removeView(v);
        return true;
    };
    View.OnClickListener onClickListener = v -> {
        BaseWidget currentWidget = (BaseWidget) v;
        final PropertiesView d = PropertiesView.newInstance(currentWidget.getAttributes(), currentWidget.getStringId(), currentWidget.getViewManager().getContext().getInflater().getIdGenerator());
        d.show(AndroidUtilities.getActivity(getContext()).getSupportFragmentManager(), "");
    };
    /**
     * The view that represents the shadow
     */
    private View shadow;
    View.OnDragListener dragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            //don't allow dragging if it's not a ViewGroup
            if (!(v instanceof ViewGroup)) {
                return false;
            }

            ViewGroup hostView = (ViewGroup) v;

            if (v instanceof EditorView && hostView.getChildCount() > 1) {
                removeView(shadow);
                return false;
            }

			int x;
            int y;

            switch (event.getAction()) {
                case DragEvent.ACTION_DROP: {
                    hostView.removeView(shadow);

                    if (v == EditorView.this && hostView.getChildCount() > 1) {
                        return false;
                    }

                    Object object = event.getLocalState();

                    BaseWidget view;

                    //if the object is a new one added by the user,
                    //it will be in a form of a view so no need to
                    //create a new one
                    if (object instanceof Widget) {
                        view = layoutInflater.inflate(((Widget) object).getLayout(editor, editorContext, EditorView.this), null);
                    } else {
                        view = (BaseWidget) object;
                    }

                    if (view.getAsView().getParent() != null) {
                        ((ViewGroup) view.getAsView().getParent()).removeView(view.getAsView());
                    }

                	addView(hostView, view.getAsView(), event);

                    //set this drag listener to the view
                    //so it can be dragged on too
                    view.getAsView().setOnDragListener(this);
                    view.getAsView().setOnClickListener(onClickListener);
                    view.getAsView().setOnLongClickListener(onLongClickListener);

                    break;
                }

                //make sure that the shadow is removed
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DRAG_EXITED: {
                    hostView.removeView(shadow);
                    break;
                }

				case DragEvent.ACTION_DRAG_LOCATION:
                    if (hostView instanceof EditorView && hostView.getChildCount() > 1) {
                        return false;
                    }
                case DragEvent.ACTION_DRAG_ENTERED: {

                    if (hostView instanceof EditorView && hostView.getChildCount() > 1) {
                        return false;
                    }

                	if(shadow.getParent() != null){
                		int indexOfChild = hostView.indexOfChild(shadow);

                		int index = 0;
                		if((hostView instanceof LinearLayoutItem) && ((LinearLayoutItem) hostView).getOrientation() == LinearLayout.VERTICAL) {
							index = getVerticalIndexForEvent(hostView, event);
						}else{
                			index = getHorizontalIndexForEvent(hostView, event);
						}

                		if(indexOfChild != index){
                			hostView.removeView(shadow);
                			addView(hostView, shadow, event);
						}
					}else{
                		hostView.addView(shadow);
					}
                }
            }
            return true;
        }
    };
    private LinearLayoutItem root;

    public EditorView(Context context) {
        super(context);

        editor = new EditorBuilder().build();
        Layout rootLayout = Widget.createLinearLayout();

        editorContext = editor.createContextBuilder(context).build();
        layoutInflater = editorContext.getInflater();

        setOrientation(VERTICAL);
        setOnDragListener(dragListener);

        shadow = new View(context);
        init();
    }

    /**
     * @return returns the root layout of the editor
     */
    public BaseWidget getRootEditorView() {
        return (BaseWidget) getChildAt(0);
    }

    public Editor getEditor() {
        return editor;
    }

    public EditorContext getEditorContext() {
        return editorContext;
    }

    private void init() {
        setOrientation(VERTICAL);


        shadow = new View(getContext());
        shadow.setBackgroundColor(0x52000000);
        shadow.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(50)));
        shadow.setMinimumWidth(AndroidUtilities.dp(50));
        shadow.setMinimumHeight(AndroidUtilities.dp(50));
    }

    /**
     * Convenience method to set listeners to all child views
     * after we import a layout
     *
     * @param view the root layout of the xml
     */
    public void setViewListeners(View view) {

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View child = ((ViewGroup) view).getChildAt(i);
                setViewListeners(child);
            }

        }

        if (view instanceof BaseWidget) {
            view.setOnLongClickListener(onLongClickListener);
            if (view instanceof ViewGroup) {
                view.setOnDragListener(dragListener);
            }
            view.setOnClickListener(onClickListener);
        }
    }


    public void importLayout(Layout layout) {
        removeAllViews();
        View view = layoutInflater.inflate(layout, null, this, -1).getAsView();

        addView(view);
        setViewListeners(view);
    }


    /**
     * Convenience method to add a view based on where it is dropped
     *
     * @param parent the parent view
     * @param child  the view to be added
     * @param event  the DragEvent associated with the view
     */
    private void addView(ViewGroup parent, View child, DragEvent event) {

    	int index = parent.getChildCount();
		if (parent instanceof LinearLayoutItem) {
			if(((LinearLayoutItem) parent).getOrientation() == LinearLayout.VERTICAL){
				index = getVerticalIndexForEvent(parent, event);
			}else{
				index = getHorizontalIndexForEvent(parent, event);
			}
		}
		parent.addView(child, index);

	}

	private int getHorizontalIndexForEvent(ViewGroup parent, DragEvent event){

    	int dropX = (int) event.getX();
    	int dropY = (int) event.getY();

    	int index = 0;

    	for(int i = 0; i < parent.getChildCount(); i++){
    		View childView = parent.getChildAt(i);

    		if(childView == shadow){
    			continue;
			}

    		if(childView.getRight() < dropX){
    			index++;
			}
		}
    	return index;
	}

	private int getVerticalIndexForEvent(ViewGroup parent, DragEvent event){

		int dropX = (int) event.getX();
		int dropY = (int) event.getY();

		int index = 0;

		for(int i = 0; i < parent.getChildCount(); i++){
			View childView = parent.getChildAt(i);

			if(childView == shadow){
				continue;
			}

			if(childView.getTop() < dropY){
				index++;
			}
		}

		return index;
	}

}
