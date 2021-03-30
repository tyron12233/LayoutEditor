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
				
				List<Layout.Attribute> attributes = new ArrayList<>();
				
				ViewTypeParser.AttributeSet.Attribute attribute = editor.getAttributeId(Attributes.View.Height, clazz);
				Value value = attribute.processor.precompile(new Primitive("wrap_content"), context, context.getFunctionManager());
				attributes.add(new Layout.Attribute(attribute.id, value));
				
				ViewTypeParser.AttributeSet.Attribute attributeW = editor.getAttributeId(Attributes.View.Width, clazz);
				Value valueW = Dimension.valueOf("match_parent");//attributeW.processor.precompile(new Primitive("match_parent"), context, context.getFunctionManager());
				attributes.add(new Layout.Attribute(attributeW.id, valueW));
				
				ViewTypeParser.AttributeSet.Attribute attributeP = editor.getAttributeId(Attributes.View.Padding, clazz);
				Value valueP = attributeW.processor.precompile(new Primitive("8dp"), context, context.getFunctionManager());
				attributes.add(new Layout.Attribute(attributeP.id, valueP));
			    
				try{
				int count = AndroidUtilities.countWidgets((ViewGroup) widget, Class.forName("android.widget." + clazz)) + 1;
				
			  while(context.getInflater().getIdGenerator().keyExists(clazz + count)){
			      count++;
			   }
			
			
			
			    ViewTypeParser.AttributeSet.Attribute attributeI = editor.getAttributeId(Attributes.View.Id, clazz);
				Value valueI = attributeI.processor.precompile(new Primitive(clazz + count), context, context.getFunctionManager());
				attributes.add(new Layout.Attribute(attributeI.id, valueI));
			    }catch(Exception e){}
				
				if(clazz.equals(TEXTVIEW) || clazz.equals(BUTTON)){
					ViewTypeParser.AttributeSet.Attribute attributeText = editor.getAttributeId(Attributes.TextView.Text, clazz);
				    Value valueText = attributeText.processor.precompile(new Primitive("TextView"), context, context.getFunctionManager());
				    attributes.add(new Layout.Attribute(attributeText.id, valueText));
			   
				}
				
			    
			/*	ObjectValue attrWidth = new ObjectValue();
				attrs.add(Attributes.View.Width, Dimension.valueOf("match_parent"));
				attributes.add(new Layout.Attribute(editor.getAttributeId(Attributes.View.Width, "LinearLayout").id, attrWidth));
		
				ObjectValue attrPadding = new ObjectValue();
				attrs.add(Attributes.View.Margin, Dimension.valueOf("8dp"));
				attributes.add(new Layout.Attribute(editor.getAttributeId(Attributes.View.Margin, "LinearLayout").id, attrPadding));
				*/
				
				//int count = 1;
				
				/*
				for(Map.Entry<String, Layout> entry : context.getEditorResources().getLayoutManager().getLayouts()){
					if(entry.getValue().type.equals(clazz)){
						count++;
					}
				}*/
				/*
				ObjectValue attrId = new ObjectValue();
				attrId.addProperty(Attributes.View.Id, clazz + count);
				attributes.add(new Layout.Attribute(editor.getAttributeId(Attributes.View.Id, "View").id, attrId));
				*/
				return new Layout(clazz, attributes, null, null);
		}
		
		public static Layout createLinearLayout(){
				return new Layout("LinearLayout", null, null, null);
		}
}
