package com.tyron.layouteditor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.LinearLayoutItem;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WidgetFactory {

    private final EditorView root;

    public WidgetFactory(EditorView root){
        this.root = root;
    }
    @NonNull
    public <T extends BaseWidget> T createWidget(Context context, Widget widget) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(widget.getClazz());
            Constructor<T> constructor = clazz.getConstructor(Context.class);

            T view = constructor.newInstance(context);
            setDefaultAttributes(view.getAsView(), widget);
            return view;
        }catch(Exception e){
            throw new IllegalArgumentException("Invalid widget: " + widget.getClazz());
        }
    }

    public void setDefaultAttributes(View view, Widget widget) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2,-2);
        if(view instanceof ViewGroup){
            params.width = -1;
            params.height = -2;

        }
        view.setPadding(AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        view.setMinimumWidth(AndroidUtilities.dp(50));
        view.setMinimumHeight(AndroidUtilities.dp(50));
		//view.setBackgroundColor(Color.rgb(SketchwareUtil.getRandom(0,255),SketchwareUtil.getRandom(0,255),SketchwareUtil.getRandom(0,255)));
        try {
            String idString = widget.getName() + AndroidUtilities.countWidgets(root, Class.forName(widget.getClazz()));
            view.setId(SimpleIdGenerator.getInstance().getUnique(idString));
            Toast.makeText(view.getContext(), idString, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
