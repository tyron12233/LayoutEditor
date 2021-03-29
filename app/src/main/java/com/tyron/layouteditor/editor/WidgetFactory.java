package com.tyron.layouteditor.editor;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.Editor;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Widget;

public class WidgetFactory {

       private final EditorContext context;
       private final EditorView editorView;
       private final Editor editor;

       public WidgetFactory(EditorContext context, Editor editor, EditorView editorView) {
        this.context = context;
        this.editor = editor;
        this.editorView = editorView;
    }

    @NonNull
    public <T extends BaseWidget> T createWidget(Widget widget) {
        try {
            
            T view = (T) context.getInflater().inflate(widget.getLayout(editor, context, editorView), null);
            return view;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid widget: " + widget.getClazz());
        }
    }

 /*   public void setDefaultAttributes(View view, Widget widget) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2, -2);
        if (view instanceof ViewGroup) {
            params.width = -1;
            params.height = -2;

        }
        view.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        view.setMinimumWidth(AndroidUtilities.dp(50));
        view.setMinimumHeight(AndroidUtilities.dp(50));
        //view.setBackgroundColor(Color.rgb(SketchwareUtil.getRandom(0,255),SketchwareUtil.getRandom(0,255),SketchwareUtil.getRandom(0,255)));
        try {
            String idString = widget.getName() + AndroidUtilities.countWidgets(root, Class.forName(widget.getClazz()));
            view.setId(SimpleIdGenerator.getInstance().getUnique(idString));
            Toast.makeText(view.getContext(), idString, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
