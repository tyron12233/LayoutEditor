package com.tyron.layouteditor.editor.widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.tyron.layouteditor.SimpleIdGenerator;
import com.tyron.layouteditor.WidgetFactory;
import com.tyron.layouteditor.editor.PropertiesView;
import com.tyron.layouteditor.editor.handle.HandleView;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Dimension;

import java.util.ArrayList;


@SuppressLint("ViewConstructor")
public class LinearLayoutItem extends LinearLayout implements BaseWidget,
        View.OnLongClickListener, View.OnClickListener{

    private View shadow;
    private WidgetFactory widgetFactory;

    private final Paint backgroundPaint = new Paint();

    private boolean isRoot = false;

    public LinearLayoutItem(Context context) {
        super(context);
        init();
    }

    public boolean isRootView(){
        return isRoot;
    }

    private void init(){
         
		//setWillNotDraw(false);

        backgroundPaint.setColor(0xfffe6262);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);

        backgroundPaint.setStrokeWidth(AndroidUtilities.dp(4));

        setOnLongClickListener(this);
        setOnClickListener(this);
        setBackgroundColor(0xffffffff);
		
		GradientDrawable gd = new GradientDrawable();
		gd.setStroke(AndroidUtilities.dp(1), 0xfffe6262);
		setBackground(gd);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
        layoutTransition.setDuration(180L);
        setLayoutTransition(layoutTransition);
        setAnimationCacheEnabled(true);

        for(int i = 0; i < handlePoints.length; i++){
            handlePoints[i] = new Point();
        }

    }

    Rect rect = new Rect();
    Point[] handlePoints = new Point[4];

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        getLocalVisibleRect(rect);
        handlePoints[0].x = rect.left;
        handlePoints[0].y = rect.bottom / 2;

        handlePoints[1].x = rect.right / 2;
        handlePoints[1].y = rect.top;

        handlePoints[2].x = rect.right;
        handlePoints[2].y = rect.bottom / 2;

        handlePoints[3].x = rect.right / 2;
        handlePoints[3].y = rect.bottom;

        //canvas.drawCircle(handlePoints[0].x, handlePoints[0].y, AndroidUtilities.dp(16), backgroundPaint);
        //canvas.drawCircle(handlePoints[1].x, handlePoints[1].y, AndroidUtilities.dp(16), backgroundPaint);
        //canvas.drawCircle(handlePoints[2].x, handlePoints[2].y, AndroidUtilities.dp(16), backgroundPaint);
        //canvas.drawCircle(handlePoints[3].x, handlePoints[3].y, AndroidUtilities.dp(16), backgroundPaint);

    }

    public void setRoot(boolean val){
        isRoot = val;
    }

    Point lastTouchedPoint = new Point();
    @Override
    public boolean onTouchEvent(MotionEvent event){
        lastTouchedPoint.x = (int) event.getX();
        lastTouchedPoint.y = (int) event.getY();
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onLongClick(View v) {
        if(isRoot) return false;

        for(Point point : handlePoints){

            int distance = ((point.x - lastTouchedPoint.x) * (point.x - lastTouchedPoint.x))
                    + ((point.y - lastTouchedPoint.y) * (point.y - lastTouchedPoint.y));

            if(distance < AndroidUtilities.dp(16)){
                Toast.makeText(getContext(), "LONG PRESSED POOINT" + point, Toast.LENGTH_LONG).show();
                return true;
            }
        }
        ViewCompat.startDragAndDrop(this, null, new DragShadowBuilder(this), this, 0);
        ((ViewGroup)getParent()).removeView(this);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(isRoot) return;

        final PropertiesView d = PropertiesView.newInstance(getAttributes(), getStringId());
        d.show(AndroidUtilities.getActivity(getContext()).getSupportFragmentManager(), "null");

        Toast.makeText(getContext(), SimpleIdGenerator.getInstance().getString(getId()), Toast.LENGTH_SHORT).show();
    }


    @Override
    public View getAsView(){
        return this;
    }

    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> arrayList = new ArrayList<>();

        arrayList.add(new Attribute(Attributes.View.Width, Dimension.valueOf(getLayoutParams().width)));
        arrayList.add(new Attribute(Attributes.View.Height, Dimension.valueOf(getLayoutParams().height)));
	    arrayList.add(new Attribute( Attributes.LinearLayout.Orientation, new Primitive(getOrientation())));
		
		if(getParent() instanceof LinearLayoutItem){
			arrayList.add(new Attribute(Attributes.View.Weight, new Primitive(((LinearLayout.LayoutParams)getLayoutParams()).weight)));
		}

		if(getParent() instanceof RelativeLayoutItem){
		    arrayList.addAll(Attributes.getRelativeLayoutChildAttributes((RelativeLayout.LayoutParams) getLayoutParams()));
        }
	    return arrayList;
    }
	
	@Override
	public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdateWidget);
    }
	
	
	@Override
	public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdateWidget);
    }
	

	@Override
	public void didReceivedNotification(int id, Object... args){
		if(id == NotificationCenter.didUpdateWidget && ((String)args[0]).equals(getStringId())){
            update((ArrayList<Attribute>) args[1]);
		}
	}
}
