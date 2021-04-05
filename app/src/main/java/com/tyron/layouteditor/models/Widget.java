package com.tyron.layouteditor.models;

import android.view.ViewGroup;

import com.tyron.layouteditor.editor.Editor;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.editor.EditorView;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.values.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Widget {
		
		public static final String LINEAR_LAYOUT = "LinearLayout";
		public static final String RELATIVE_LAYOUT = "RelativeLayout";
		public static final String FRAME_LAYOUT = "FrameLayout";
		public static final String CONSTRAINT_LAYOUT = "ConstraintLayout";

		public static final String TEXTVIEW = "TextView";
		public static final String EDITTEXT = "EditText";
		public static final String BUTTON = "Button";
		public static final String PROGRESSBAR = "ProgressBar";

		private String clazz;
		private HashMap<String, Object> attributes;
		
		public Widget(String clazz){
				this.clazz = clazz;
		}
		
		public void setClazz(String clazz){
				this.clazz = clazz;
		}
		
		public String getClazz(){
				return clazz;
		}
		
		public String getName(){
				return clazz.replace("com.tyron.layouteditor.editor.widget.", "").replace("Item", "");
		}
		
		public Layout getLayout(Editor editor, EditorContext context, EditorView widget){
				return new Layout(clazz, new ArrayList<>(), null, null);
		}
		
		public static Layout createLinearLayout(){
				return new Layout("LinearLayout", null, null, null);
		}
}
