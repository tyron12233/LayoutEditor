package com.tyron.layouteditor.editor.widget.viewgroup;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Primitive;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("ViewConstructor")
public class LinearLayoutItem extends LinearLayout implements BaseWidget {

    private final Paint backgroundPaint = new Paint();

    private Manager viewManager;

    private boolean isRoot = false;

    public LinearLayoutItem(Context context) {
        super(context);
        init();
    }

    @Override
    public Manager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(@NonNull Manager manager) {
        this.viewManager = manager;
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }

    public boolean isRootView(){
        return isRoot;
    }

    private void init(){
         
		setWillNotDraw(false);

        backgroundPaint.setColor(0xff757575);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);

        backgroundPaint.setStrokeWidth(AndroidUtilities.dp(2));

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
    protected void onDraw(Canvas canvas) {

        getLocalVisibleRect(rect);
        canvas.drawRect(rect, backgroundPaint);
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

    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> arrayList = new ArrayList<>();

        arrayList.addAll(Attributes.getViewAttributes(this));

		arrayList.add(new Attribute( Attributes.LinearLayout.Orientation, new Primitive(getOrientation())));

		if(getParent() instanceof LinearLayoutItem){
			arrayList.add(new Attribute(Attributes.View.Weight, new Primitive(((LinearLayout.LayoutParams)getLayoutParams()).weight)));
		}

		if(getParent() instanceof RelativeLayoutItem){
		    arrayList.addAll(Attributes.getRelativeLayoutChildAttributes(this));
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
            viewManager.updateAttributes((List<Attribute>) args[1]);
		}
	}
}
