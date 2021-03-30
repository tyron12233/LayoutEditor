package com.tyron.layouteditor;

import android.graphics.Paint;

import com.tyron.layouteditor.util.AndroidUtilities;

public class Theme {

    private static Paint viewBackgroundPaint;

    public static void initializeResources(){
        viewBackgroundPaint = new Paint();
        viewBackgroundPaint.setColor(0xff757575);
        viewBackgroundPaint.setAntiAlias(true);
        viewBackgroundPaint.setStrokeWidth(AndroidUtilities.dp(2));
        viewBackgroundPaint.setStyle(Paint.Style.STROKE);
    }

    public static Paint getViewBackgroundPaint(){
        if(viewBackgroundPaint == null){
            initializeResources();
        }
        return viewBackgroundPaint;
    }
}
