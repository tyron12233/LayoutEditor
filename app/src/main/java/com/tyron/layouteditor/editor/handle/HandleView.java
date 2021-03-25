package com.tyron.layouteditor.editor.handle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class HandleView extends View {

    private Paint paint = new Paint();

    public HandleView(Context context) {
        super(context);

        setWillNotDraw(false);
        paint.setColor(0xfffe6262);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);

        canvas.drawCircle(cx, cy, radius, paint);
    }
}
