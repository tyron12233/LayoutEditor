package com.tyron.layouteditor.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.tyron.layouteditor.ApplicationLoader;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;

public class AndroidUtilities {

    public static Point displaySize = new Point();
    public static float density;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            float oldDensity = density;
            density = context.getResources().getDisplayMetrics().density;
            float newDensity = density;


            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }

            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    //screenRefreshRate = display.getRefreshRate();
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static int px(int dp) {
        if (dp == -1 || dp == -2) {
            return dp;
        }
        return (int) (dp / density);
    }

    public static AppCompatActivity getActivity(Context context) {

        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }


    /**
     * Counts the number of widgets for use with ids
     *
     * @param viewGroup The root editor view
     * @param clazz     widget to be counted
     * @return widget count
     */
    public static int countWidgets(ViewGroup viewGroup, Class<?> clazz) {
        int count = 0;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

//			if(child instanceof LinearLayoutItem && ((LinearLayoutItem)child).isRootView()){
//				count--;
//			}
            if (child instanceof ViewGroup) {
                if (((ViewGroup) child).getChildCount() > 0) {
                    count += countWidgets((ViewGroup) child, clazz);
                }
            }
            if (clazz.isAssignableFrom(child.getClass())) {
                count++;
            }
        }
        return count;
    }
   

    public static LinearLayoutItem getRootView(View view) {
        if (view instanceof LinearLayoutItem && ((LinearLayoutItem) view).isRootView()) {
            return (LinearLayoutItem) view;
        }
        return getRootView((ViewGroup) view.getParent());
    }
}
