package com.tyron.layouteditor.editor;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.FileUtil;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class EditorView extends LinearLayout {

    private final EditorContext editorContext;
    private final EditorLayoutInflater layoutInflater;
    private final Editor editor;
    private final DrawableManager drawableManager;
	
	Rect focusedRect = new Rect();
	private Paint focusedPaint = new Paint();
	private boolean isFocused = false;
	
    View.OnLongClickListener onLongClickListener = v -> {
        ViewCompat.startDragAndDrop(v, null, new DragShadowBuilder(v), v, 0);
        ((ViewGroup) v.getParent()).removeView(v);
        return true;
    };
    View.OnClickListener onClickListener = v -> {
        BaseWidget currentWidget = (BaseWidget) v;
        final PropertiesView d = PropertiesView.newInstance(currentWidget);

        FragmentManager fm = AndroidUtilities.getActivity(getContext()).getSupportFragmentManager();

        d.show(fm, "");

        //make sure that dialog has been opened
        fm.executePendingTransactions();

        //after the dialog has been dismissed, we remove the focused color
        Objects.requireNonNull(d.getDialog()).setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                isFocused = false;
                invalidate();
            }
        });

        //gets the bounds of views so we can draw the box
        v.getHitRect(focusedRect);
        isFocused = true;
        animateColorChange(0xffffffff, 0x12000000);
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

            if ((v instanceof EditorView || v instanceof ScrollView) && getChildCountWithoutShadow(hostView) >= 1) {
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
                        addDefaultAttributes(view);
                        view.getAsView().setMinimumHeight(AndroidUtilities.dp(50));
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

                    if(view.getAsView() instanceof ViewGroup){
                        ViewGroup viewGroup = (ViewGroup) view.getAsView();

                        LayoutTransition layoutTransition = new LayoutTransition();
                        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
                        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
                        layoutTransition.setDuration(180L);
                        viewGroup.setLayoutTransition(layoutTransition);
                        viewGroup.setAnimationCacheEnabled(true);
                    }
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
						}else if((hostView instanceof LinearLayoutItem) && ((LinearLayoutItem) hostView).getOrientation() == LinearLayout.HORIZONTAL){
                			index = getHorizontalIndexForEvent(hostView, event);
						}else{
                		    index = indexOfChild;
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

    public EditorView(Context context) {
        super(context);

        editor = new EditorBuilder().build();
        Layout rootLayout = Widget.createLinearLayout();

        drawableManager = new DrawableManager(new HashMap<>(getImages()));
        editorContext = editor.createContextBuilder(context).setDrawableManager(drawableManager).build();
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
        setWillNotDraw(false);

        shadow = new View(getContext());
        shadow.setBackgroundColor(0x52000000);
        shadow.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(50)));
        shadow.setMinimumWidth(AndroidUtilities.dp(50));
        shadow.setMinimumHeight(AndroidUtilities.dp(50));

        focusedPaint.setColor(0x12000000);;
    }

    /**
     * Convenience method to set listeners to all child views
     * after we import a layout
     *
     * @param view the root layout of the xml
     */
    public void setViewListeners(View view) {

        if (view instanceof ViewGroup) {

            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
            layoutTransition.setDuration(180L);
            ((ViewGroup) view).setLayoutTransition(layoutTransition);
            ((ViewGroup) view).setAnimationCacheEnabled(true);

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View child = ((ViewGroup) view).getChildAt(i);
                setViewListeners(child);
            }

            view.setMinimumHeight(AndroidUtilities.dp(50));
        }

        if (view instanceof BaseWidget) {
            view.setOnLongClickListener(onLongClickListener);
            if (view instanceof ViewGroup) {
                view.setOnDragListener(dragListener);
            }
            view.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //draws on the current focused rect
        if(isFocused){
            canvas.drawRect(focusedRect, focusedPaint);
        }
    }

    /**
     * Convenience method to import a layout and set the appropriate listeners
     * for each view
     *
     * @param layout layout to be inflated
     */
    public void importLayout(Layout layout) {
        removeAllViews();
        View view = layoutInflater.inflate(layout, null).getAsView();

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

    /**
     * Calculates the horizontal index on which the view is going to be inserted
     *
     * @param parent Parent View
     * @param event DragEvent associated with the view
     * @return  returns the index on where to insert the view
     */
	private int getHorizontalIndexForEvent(ViewGroup parent, DragEvent event){

    	int dropX = (int) event.getX();

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

    /**
     * Calculates the vertical index on which the view is going to be inserted
     *
     * @param parent Parent View
     * @param event DragEvent associated with the view
     * @return  returns the index on where to insert the view
     */
	private int getVerticalIndexForEvent(ViewGroup parent, DragEvent event){

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

    /**
     * Self explanatory, gets the child count of the view
     * with the shadow view excluded
     *
     * @param view ViewGroup to be counted
     * @return returns the number of child without the shadow
     */
	public int getChildCountWithoutShadow(ViewGroup view){
		
		int count = 0;
		for(int i = 0; i < view.getChildCount(); i++){
			View child = view.getChildAt(i);
			if(child == shadow){
				continue;
			}
			count++;
		}
		
		return count;
	}

    /**
     * Adds default attributes to a newly created widget
     *
     * @param widget widget to be modified
     */
	private void addDefaultAttributes(BaseWidget widget){
        View view = widget.getAsView();
        List<Attribute> attributes = new ArrayList<>();

        if(view instanceof ViewGroup){
            attributes.add(new Attribute(Attributes.View.Width, new Primitive("match_parent")));
        }else{
            attributes.add(new Attribute(Attributes.View.Width, new Primitive("wrap_content")));
        }

        if(view instanceof TextView){
            attributes.add(new Attribute(Attributes.TextView.Text, new Primitive(view.getClass().getSimpleName())));
        }

        if(view instanceof ImageView){
            attributes.add(new Attribute(Attributes.ImageView.Src, new Primitive("@drawable/ic_launcher")));
        }

        attributes.add(new Attribute(Attributes.View.Height, new Primitive("wrap_content")));
        attributes.add(new Attribute(Attributes.View.Padding, new Primitive("8dp")));

        //generating id
        int count = AndroidUtilities.countWidgets(this, view.getClass());

        while(layoutInflater.getIdGenerator().keyExists(view.getClass().getSimpleName() + count)){
            count++;
        }
        attributes.add(new Attribute(Attributes.View.Id, new Primitive(view.getClass().getSimpleName() + count)));

        widget.getViewManager().updateAttributes(attributes);
        //view.setTag(R.id.attributes, new LinkedHashSet<>(attributes));
    }

    /**
     * get all the images from data/com.tyron.layouteditor/files/images
     * and saves them to a {@link DrawableManager} for use later
     *
     * @return returns a HashMap of it's name and path
     */
    public HashMap<String, String> getImages(){
        HashMap<String, String> images = new HashMap<>();
        ArrayList<String> filePaths = new ArrayList<>();
        FileUtil.listDir(getContext().getExternalFilesDir("images").getPath(), filePaths);

        for(String path : filePaths){
            File file = new File(path);
            images.put(file.getName().substring(0, file.getName().lastIndexOf(".")), file.getPath());
        }
        return images;
    }


    /**
     * animates between two colors
     * @param start start color
     * @param end end color
     */
    private void animateColorChange(int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setEvaluator(new ArgbEvaluatorCompat());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int newColor = (int) animator.getAnimatedValue();
                focusedPaint.setColor(newColor);
                invalidate();
            }
        });
        animator.start();
    }
}
